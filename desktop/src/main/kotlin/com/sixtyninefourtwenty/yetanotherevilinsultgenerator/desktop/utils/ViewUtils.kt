package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.MyApplication
import io.github.palexdev.materialfx.controls.MFXTableView
import io.github.palexdev.materialfx.selection.base.IMultipleSelectionModel
import javafx.collections.MapChangeListener
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.paint.Paint
import javafx.stage.Stage

val GRAY_BACKGROUND = Background(BackgroundFill(Paint.valueOf("#e6e6e6"), CornerRadii.EMPTY, Insets.EMPTY))

fun Region.allowLargestSizePossible() {
    maxWidth = Double.MAX_VALUE
    maxHeight = Double.MAX_VALUE
}

/**
 * The *correct* way to set a listener to a [MFXTableView]. See [the solution](https://github.com/palexdev/MaterialFX/issues/186#issuecomment-1110018988).
 */
fun <T> MFXTableView<T>.addSelectionChangeListener(listener: MapChangeListener<Int, T>) {
    selectionModel.selectionProperty().addListener(listener)
}

fun <T> IMultipleSelectionModel<T>.toggleSelection(data: T) {
    if (selection.containsValue(data)) {
        deselectItem(data)
    } else {
        selectItem(data)
    }
}

fun fxmlLoader(fileNameWithoutExtension: String) = FXMLLoader(MyApplication::class.java.getResource("layout/$fileNameWithoutExtension.fxml"))
