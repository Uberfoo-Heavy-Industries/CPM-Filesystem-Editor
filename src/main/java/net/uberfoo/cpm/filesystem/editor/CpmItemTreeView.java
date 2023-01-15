package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public interface CpmItemTreeView {
    StringProperty nameProperty();

    LongProperty sizeProperty();
}
