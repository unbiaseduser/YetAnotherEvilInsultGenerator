package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.generator

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.interfaces.AutoClosableView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils.fxmlLoader
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils.resourceBundle
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.OfficialOnlineInsultGeneratorService
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.ViewState
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.utils.resetTo
import io.github.palexdev.materialfx.controls.MFXButton
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RootGeneratorView(private val viewModel: GeneratorViewModel) : AutoClosableView {

    private val scope = MainScope()

    private val emptyView = fxmlLoader("generator_empty_view")
        .apply { load() }
        .getController<GeneratorEmptyView>()

    private val emptyRootView: Parent get() = emptyView.root

    private val loadingView = fxmlLoader("generic_loading_view")
        .load<Parent>()

    private val contentView = fxmlLoader("generator_content_view")
        .apply { load() }
        .getController<GeneratorContentView>()

    private val contentRootView: Parent get() = contentView.root

    private val errorView = fxmlLoader("generator_error_view")
        .apply { load() }
        .getController<GeneratorErrorView>()

    private val errorRootView: Parent get() = errorView.root

    private val mutableRoot = StackPane()

    override val root: Node get() = mutableRoot

    override fun close() {
        scope.cancel()
    }

    init {
        emptyView.generate.setOnAction {
            scope.launch {
                viewModel.generateRemoteInsult(Language.ENGLISH, OfficialOnlineInsultGeneratorService.Url.MAIN)
            }
        }
        contentView.generate.setOnAction {
            scope.launch {
                viewModel.generateRemoteInsult(Language.ENGLISH, OfficialOnlineInsultGeneratorService.Url.MAIN)
            }
        }
        errorView.retry.setOnAction {
            scope.launch {
                viewModel.generateRemoteInsult(Language.ENGLISH, OfficialOnlineInsultGeneratorService.Url.MAIN)
            }
        }
        scope.launch {
            viewModel.viewState.collect {
                when (it) {
                    is ViewState.Empty -> mutableRoot.children.resetTo(emptyRootView)
                    is ViewState.Error -> mutableRoot.children.resetTo(errorRootView)
                    is ViewState.Loading -> mutableRoot.children.resetTo(loadingView)
                    is ViewState.Content -> {
                        contentView.insult.text = it.data.insult
                        mutableRoot.children.resetTo(contentRootView)
                    }
                }
            }
        }
    }

}

class GeneratorContentView {

    @FXML
    lateinit var insult: Text

    @FXML
    lateinit var generate: MFXButton

    @FXML
    lateinit var root: Parent

    @FXML
    private fun initialize() {
        val res = resourceBundle("generator")
        generate.text = res.getString("generate")
    }

}

class GeneratorErrorView {

    @FXML
    lateinit var insult: Text

    @FXML
    lateinit var retry: MFXButton

    @FXML
    lateinit var root: Parent
}

class GeneratorEmptyView {

    @FXML
    lateinit var root: Parent

    @FXML
    lateinit var text: Text

    @FXML
    lateinit var generate: MFXButton

    @FXML
    private fun initialize() {
        val res = resourceBundle("generator")
        text.text = res.getString("generate.prompt")
        generate.text = res.getString("generate")
    }

}