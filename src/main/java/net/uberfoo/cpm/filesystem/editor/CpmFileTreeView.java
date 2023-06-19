package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import net.uberfoo.cpm.filesystem.AllocationTableFile;

import java.nio.ByteBuffer;

import static net.uberfoo.cpm.filesystem.editor.Util.toByteUnit;

public record CpmFileTreeView(AllocationTableFile file, DiskItem parent) implements CpmItemTreeView, DeletableItem, ExportableItem {

    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", file.getFilename());
    }

    public StringProperty sizeProperty() {
        return new ReadOnlyStringWrapper(this, "size", toByteUnit(file.size()));
    }

    @Override
    public void delete() {
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
