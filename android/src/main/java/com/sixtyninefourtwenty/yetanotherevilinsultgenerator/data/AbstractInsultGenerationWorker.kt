package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.myApplication
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

abstract class AbstractInsultGenerationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    protected abstract suspend fun handleInsult(insult: Insult)

    protected open val numberOfInsults: Int = 1

    final override suspend fun doWork(): Result {
        check(numberOfInsults >= 1) { "Number of insults generated must be >= 1" }
        val app = applicationContext.myApplication
        val prefs = app.preferencesRepository
        if (numberOfInsults == 1) {
            val generationResult = app.generateRemoteInsult(prefs.generatorUrl, prefs.generatorLanguage)
            return if (generationResult.isSuccess) {
                handleInsult(generationResult.getOrThrow())
                Result.success()
            } else {
                Result.retry()
            }
        } else {
            val deferreds = mutableListOf<Deferred<kotlin.Result<Insult>>>()
            repeat(numberOfInsults) {
                coroutineScope {
                    deferreds.add(async { app.generateRemoteInsult(prefs.generatorUrl, prefs.generatorLanguage) })
                }
            }
            deferreds.awaitAll().forEach { result ->
                result.onSuccess { handleInsult(it) }
            }
            return Result.success()
        }

    }

}