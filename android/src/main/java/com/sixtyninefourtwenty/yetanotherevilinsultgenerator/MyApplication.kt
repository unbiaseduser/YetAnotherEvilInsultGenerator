package com.sixtyninefourtwenty.yetanotherevilinsultgenerator

import android.app.Application
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.repository.PreferencesRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfficialOnlineInsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfflineInsultsDatabase
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource.InsultsLocalDataSource
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource.InsultsRemoteDataSource
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository

class MyApplication : Application() {
    private val database by lazy { OfflineInsultsDatabase.newInstance() }
    val insultsRepository by lazy { InsultsRepository(InsultsRemoteDataSource(OfficialOnlineInsultGeneratorService(OfficialOnlineInsultGeneratorService.Url.MAIN)), InsultsLocalDataSource(database)) }
    val backupInsultsRepository by lazy { InsultsRepository(InsultsRemoteDataSource(OfficialOnlineInsultGeneratorService(OfficialOnlineInsultGeneratorService.Url.BACKUP)), InsultsLocalDataSource(database)) }
    val preferencesRepository by lazy { PreferencesRepository(this) }

    suspend fun generateRemoteInsult(generatorUrl: OfficialOnlineInsultGeneratorService.Url, language: Language) = runCatching {
        when (generatorUrl) {
            OfficialOnlineInsultGeneratorService.Url.MAIN -> insultsRepository.generateRemoteInsult(language)
            OfficialOnlineInsultGeneratorService.Url.BACKUP -> backupInsultsRepository.generateRemoteInsult(language)
        }
    }

    override fun onCreate() {
        super.onCreate()
        preferencesRepository.performMigrations()
    }

}