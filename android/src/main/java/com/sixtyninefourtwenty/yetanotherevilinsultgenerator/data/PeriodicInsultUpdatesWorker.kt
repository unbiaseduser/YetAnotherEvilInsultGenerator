package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.myApplication
import java.time.Duration

class PeriodicInsultUpdatesWorker(
    appContext: Context,
    params: WorkerParameters
) : AbstractInsultGenerationWorker(appContext, params) {

    override val numberOfInsults: Int
        get() = inputData.getInt(NUMBER_OF_INSULTS_INPUT, 1)

    override suspend fun handleInsult(insult: Insult) {
        applicationContext.myApplication.insultsRepository.addOrUpdateLocalInsult(insult)
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "periodic_insult_update_worker"
        const val NUMBER_OF_INSULTS_INPUT = "number_of_insults"
        private fun createInputData(numberOfInsults: Int) = Data.Builder()
            .putInt(NUMBER_OF_INSULTS_INPUT, numberOfInsults)
            .build()
        @SuppressLint("NewApi")
        fun enqueueUniquePeriodicWork(workManager: WorkManager,
                                      constraints: Constraints,
                                      duration: Duration,
                                      numberOfInsults: Int) {
            workManager.enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                PeriodicWorkRequestBuilder<PeriodicInsultUpdatesWorker>(duration)
                    .setInitialDelay(duration)
                    .setConstraints(constraints)
                    .setInputData(createInputData(numberOfInsults))
                    .build()
            )
        }
        fun cancelUniqueWork(workManager: WorkManager) = workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

}