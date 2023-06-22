package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;

public interface CpmItemTreeView {
    StringProperty nameProperty();

    StringProperty sizeProperty();

    BooleanProperty dirtyProperty();

}
