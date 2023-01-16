package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;

public class CpmUserGroupView implements CpmItemTreeView {

    private final int userNumber;

    public CpmUserGroupView(int userNumber) {
        this.userNumber = userNumber;
    }

    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", String.format("%02d:", userNumber));
    }

    @Override
    public LongProperty sizeProperty() {
        return null;
    }

    public int getUserNumber() {
        return userNumber;
    }
}
