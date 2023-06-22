package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;

public class NameCell extends TreeTableCell<CpmItemTreeView, String> {

    public NameCell() {
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
    }
}
