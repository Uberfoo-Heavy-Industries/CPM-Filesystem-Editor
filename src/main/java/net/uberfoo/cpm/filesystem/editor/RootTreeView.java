package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;

public class RootTreeView implements CpmItemTreeView {
    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", "root");
    }

    @Override
    public StringProperty sizeProperty() {
        return null;
    }
}
