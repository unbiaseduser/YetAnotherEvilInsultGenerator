package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.offline

import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.interfaces.AutoClosableView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.utils.*
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultSortOptions
import io.github.palexdev.materialfx.controls.MFXTableColumn
import io.github.palexdev.materialfx.controls.MFXTableRow
import io.github.palexdev.materialfx.controls.MFXTableView
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import io.github.palexdev.materialfx.filter.EnumFilter
import io.github.palexdev.materialfx.filter.StringFilter
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.geometry.Side
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class OfflineView(private val viewModel: OfflineViewModel) : AutoClosableView {

    private val scope = MainScope()

    @FXML
    private lateinit var table: MFXTableView<Insult>

    @FXML
    override lateinit var root: Pane

    private val offlineStrings = resourceBundle("offline")

    private lateinit var deleteSuccessDialog: MFXStageDialog

    init {
        scope.launch {
            deleteSuccessDialog = MFXGenericDialogBuilder.build()
                .setContentText(offlineStrings.getString("delete.success"))
                .toStageDialogBuilder()
                .setOwnerNode(root)
                .get()
        }
    }

    @FXML
    private fun initialize() {
        root.allowLargestSizePossible()
        val languageStrings = resourceBundle("languages")
        val insultColumnName = offlineStrings.getString("insult")
        val languageColumnName = offlineStrings.getString("language")
        val insultColumn = MFXTableColumn(insultColumnName, true, InsultSortOptions.Mode.CONTENT_ALPHABETICAL_ASCENDING.insultComparator).apply {
            setRowCellFactory { MFXTableRowCell(Insult::insult) }
        }
        val languageColumn = MFXTableColumn<Insult>(languageColumnName, true).apply {
            setRowCellFactory { MFXTableRowCell { it.getLanguage().getLabel(languageStrings) } }
        }
        val dateCreatedColumn = MFXTableColumn(offlineStrings.getString("date.created"), true, Comparator.comparing(Insult::getCreatedDateTime)).apply {
            setRowCellFactory { MFXTableRowCell(Insult::created) }
        }
        val createdByColumn = MFXTableColumn<Insult>(offlineStrings.getString("created.by"), true).apply {
            setRowCellFactory { MFXTableRowCell { it.createdBy.orEmpty() } }
        }
        val commentColumn = MFXTableColumn<Insult>(offlineStrings.getString("comment"), true).apply {
            setRowCellFactory { MFXTableRowCell { it.comment.orEmpty() } }
        }

        with(table) {
            setTableRowFactory { insult ->
                MFXTableRow(table, insult).apply {
                    setOnContextMenuRequested {
                        val menuItems = mutableListOf<MenuItem>()
                        if (isSelected) {
                            menuItems.add(MenuItem(offlineStrings.getString("delete.selected")).apply {
                                setOnAction {
                                    scope.launch(Dispatchers.IO) {
                                        viewModel.deleteLocalInsults(selectionModel.selectedValues)
                                    }.invokeOnCompletion {
                                        scope.launch {
                                            deleteSuccessDialog.showDialog()
                                            selectionModel.clearSelection()
                                        }
                                    }
                                }
                            })
                        }
                        if (menuItems.isNotEmpty()) {
                            ContextMenu().apply {
                                items.addAll(menuItems)
                            }.show(this, Side.BOTTOM, it.x, it.y)
                        }
                    }
                    addEventFilter(MouseEvent.DRAG_DETECTED) { startFullDrag() }
                    setOnMouseDragEntered { selectionModel.toggleSelection(data) }
                }
            }
            addSelectionChangeListener {
                if (it.map.containsKey(it.key)) {
                    getCell(it.key).background = GRAY_BACKGROUND
                } else {
                    getCell(it.key).background = Background.EMPTY
                }
            }
            allowLargestSizePossible()
            tableColumns.addAll(listOf(
                insultColumn,
                languageColumn,
                dateCreatedColumn,
                createdByColumn,
                commentColumn
            ))
            filters.addAll(listOf(
                StringFilter(insultColumnName, Insult::insult),
                EnumFilter(languageColumnName, Insult::getLanguage, Language::class.java, object : StringConverter<Language>() {
                    override fun toString(`object`: Language): String {
                        return `object`.getLabel(languageStrings)
                    }

                    override fun fromString(string: String): Language {
                        for (languageCode in languageStrings.keys) {
                            if (languageStrings.getString(languageCode) == string) {
                                return Language.values().first { it.languageCode == languageCode }
                            }
                        }
                        error("No language has this label: $string")
                    }
                })
            ))
        }

        scope.launch {
            viewModel.allAvailableInsults.collect {
                table.items = FXCollections.observableList(it)
            }
        }

    }

    override fun close() {
        scope.cancel()
    }

}