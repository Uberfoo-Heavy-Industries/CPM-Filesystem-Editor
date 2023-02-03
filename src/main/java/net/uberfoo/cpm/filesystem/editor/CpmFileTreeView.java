package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import net.uberfoo.cpm.filesystem.AllocationTableFile;
import net.uberfoo.cpm.filesystem.CpmDisk;

import java.io.IOException;
import java.nio.ByteBuffer;

public record CpmFileTreeView(AllocationTableFile file, CpmDiskTreeView parent) implements CpmItemTreeView, DeletableItem, ExportableItem {

    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", file.getFilename());
    }

    public LongProperty sizeProperty() {
        return new ReadOnlyLongWrapper(this, "size", file.size());
    }

    @Override
    public void delete() throws IOException {
        file.delete();
    }

    @Override
    public ByteBuffer retrieveFileContents() {
        return file.retrieveFileContents();
    }

    @Override
    public String getName() {
        return file.getFilename();
    }
}
