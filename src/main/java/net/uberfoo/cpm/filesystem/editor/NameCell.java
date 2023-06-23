package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.List;

public class NameCell extends TreeTableCell<CpmItemTreeView, String> {

    private final CopyService copyService;
    private final RefreshHandler refreshHandler;

    public NameCell(CopyService copyService, RefreshHandler refreshHandler) {
        this.copyService = copyService;
        this.refreshHandler = refreshHandler;
        textProperty().bind(Bindings
                .when(BooleanExpression.booleanExpression(
                        tableRowProperty()
                                .flatMap(TreeTableRow::itemProperty)
                                .flatMap(CpmItemTreeView::dirtyProperty))
                        .and(BooleanExpression.booleanExpression(
                                tableRowProperty()
                                        .flatMap(TreeTableRow::itemProperty)
                                        .map(x -> !(x instanceof PartitionTreeView)))))
                .then(Bindings.concat("* ", itemProperty()))
                .otherwise(Bindings.convert(itemProperty().orElse(""))));

        setOnDragOver(e -> {
            var dragBoard = e.getDragboard();
            if (dragBoard.hasFiles()
                    && tableRowProperty().isNotNull().getValue()
                    && tableRowProperty().getValue().getTreeItem().getValue() instanceof CpmDiskTreeView) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        setOnDragDropped(e -> {
            var dragBoard = e.getDragboard();
            if (dragBoard.hasFiles() && tableRowProperty().isNotNull().get()
                    && tableRowProperty().getValue().getTreeItem().getValue() instanceof CpmDiskTreeView diskTreeItem) {
                copyService.copyToCpm(diskTreeItem, dragBoard.getFiles(), "Copy");
                refreshHandler.refreshDisk(tableRowProperty().getValue().getTreeItem());
            }
        });

    }

    @FXML
    public void initialize() {
    }
}
