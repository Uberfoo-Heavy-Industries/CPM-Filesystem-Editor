package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;

public class RootTreeView implements CpmItemTreeView {
    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", "root");
    }

    @Override
    public StringProperty sizeProperty() {
        return null;
    }

    @Override
    public BooleanProperty dirtyProperty() {
        return new ReadOnlyBooleanWrapper(false);
    }
}
