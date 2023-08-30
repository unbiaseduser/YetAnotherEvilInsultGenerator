package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.main

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.generator.GeneratorViewModel
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.generator.RootGeneratorView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.interfaces.AutoClosableView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.interfaces.View
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.offline.OfflineView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.offline.OfflineViewModel
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils.fxmlLoader
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils.resourceBundle
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.utils.resetTo
import io.github.palexdev.materialfx.controls.MFXButton
import io.github.palexdev.materialfx.controls.MFXTitledPane
import io.github.palexdev.materialfx.enums.HeaderPosition
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainView(
    private val mainViewModel: MainViewModel,
    private val generatorViewModel: GeneratorViewModel,
    private val offlineViewModel: OfflineViewModel
) : AutoClosableView {

    private var currentView: View? = null

    private fun disposeView() {
        val view = currentView
        if (view is AutoClosableView?) {
            view?.close()
        }
        currentView = null
    }

    private val scope = MainScope()

    @FXML
    override lateinit var root: BorderPane

    @FXML
    private lateinit var content: Pane

    private lateinit var offlineButton: MFXButton

    private lateinit var generatorButton: MFXButton

    @FXML
    private fun initialize() {
        val generatorRes = resourceBundle("generator")
        val offlineRes = resourceBundle("offline")
        root.left = MFXTitledPane(
            "",
            VBox(
                MFXButton(generatorRes.getString("generator")).apply {
                    maxWidth = Double.MAX_VALUE
                    setOnAction {
                        mainViewModel.screen = MainViewModel.Screen.GENERATOR
                    }
                }.also { generatorButton = it },
                MFXButton(offlineRes.getString("offline")).apply {
                    VBox.setMargin(this, Insets(10.0, 0.0, 0.0, 0.0))
                    maxWidth = Double.MAX_VALUE
                    setOnAction {
                        mainViewModel.screen = MainViewModel.Screen.OFFLINE
                    }
                }.also { offlineButton = it }
            )
        ).apply {
            isExpanded = true
            headerPos = HeaderPosition.LEFT
        }

        scope.launch {
            mainViewModel.screenFlow.collect {
                when (it) {
                    MainViewModel.Screen.GENERATOR -> displayView(RootGeneratorView(generatorViewModel))
                    MainViewModel.Screen.OFFLINE -> displayView(
                        fxmlLoader("offline")
                            .apply {
                                setControllerFactory { _ -> OfflineView(offlineViewModel) }
                                load()
                            }
                            .getController<OfflineView>()
                    )
                }
            }
        }
    }

    private fun displayView(view: View) {
        disposeView()
        with(view) {
            content.children.resetTo(root)
            currentView = this
        }
    }

    override fun close() {
        disposeView()
        scope.cancel()
    }

}