package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;

public record CpmUserGroupView(int userNumber) implements CpmItemTreeView {

    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", String.format("%02d:", userNumber));
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
