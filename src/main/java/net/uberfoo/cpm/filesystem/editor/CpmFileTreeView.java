package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;
import net.uberfoo.cpm.filesystem.AllocationTableEntry;
import net.uberfoo.cpm.filesystem.AllocationTableFile;

import java.nio.ByteBuffer;

import static net.uberfoo.cpm.filesystem.editor.Util.toByteUnit;

public class CpmFileTreeView implements CpmItemTreeView, DeletableItem, ExportableItem {

    private final AllocationTableFile file;
    private final DiskItem parent;
    public CpmFileTreeView(AllocationTableFile file, DiskItem parent) {
        this.file = file;
        this.parent = parent;
    }

    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", file.getFilename());
    }

    public StringProperty sizeProperty() {
        return new ReadOnlyStringWrapper(this, "size", toByteUnit(file.size()));
    }

    @Override
    public BooleanProperty dirtyProperty() {
        return new ReadOnlyBooleanWrapper(false);
    }

    @Override
    public void delete() {
        file.delete();
        parent.dirtyProperty().setValue(true);
    }

    @Override
    public ByteBuffer retrieveFileContents() {
        return file.retrieveFileContents();
    }

    @Override
    public String getName() {
        return file.getFilename();
    }

    public DiskItem parent() {
        return parent;
    }

    public AllocationTableFile file() {
        return file;
    }
}
