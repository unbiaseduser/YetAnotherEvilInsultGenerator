package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.generator.GeneratorViewModel
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.main.MainView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.main.MainViewModel
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.offline.OfflineViewModel
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils.fxmlLoader
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils.resourceBundle
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfficialOnlineInsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfflineInsultsDatabase
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource.InsultsLocalDataSource
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.datasource.InsultsRemoteDataSource
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.repository.InsultsRepository
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

class MyApplication : Application() {

    private lateinit var database: OfflineInsultsDatabase
    private lateinit var insultsRepository: InsultsRepository
    private lateinit var backupInsultsRepository: InsultsRepository
    private lateinit var mainView: MainView
    private lateinit var generatorViewModel: GeneratorViewModel
    private lateinit var offlineViewModel: OfflineViewModel

    override fun init() {
        database = OfflineInsultsDatabase.newInstance()
        val localDataSource = InsultsLocalDataSource(database)
        insultsRepository = InsultsRepository(InsultsRemoteDataSource(OfficialOnlineInsultGeneratorService(OfficialOnlineInsultGeneratorService.Url.MAIN)), localDataSource)
        backupInsultsRepository = InsultsRepository(InsultsRemoteDataSource(OfficialOnlineInsultGeneratorService(OfficialOnlineInsultGeneratorService.Url.BACKUP)), localDataSource)
        generatorViewModel = GeneratorViewModel(insultsRepository, backupInsultsRepository)
        offlineViewModel = OfflineViewModel(insultsRepository)
    }

    override fun start(stage: Stage) {
        val res = resourceBundle("misc")
        val rootView = fxmlLoader("main").apply {
            setControllerFactory { _ -> MainView(MainViewModel(), generatorViewModel, offlineViewModel).also { mainView = it } }
            load()
        }.getController<MainView>()

        with(stage) {
            title = res.getString("app.name")
            scene = Scene(rootView.root, 800.0, 500.0)
            show()
        }
    }

    override fun stop() {
        database.close()
        mainView.close()
    }

}

fun main() {
    Application.launch(MyApplication::class.java)
}