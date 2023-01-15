package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import net.uberfoo.cpm.filesystem.AllocationTableFile;

public class CpmFileTreeView {

    private final AllocationTableFile file;

    public CpmFileTreeView(AllocationTableFile file) {
        this.file = file;
    }

    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", file.getFilename());
    }

    public LongProperty sizeProperty() {
        return new ReadOnlyLongWrapper(this, "size", file.size());
    }
}
