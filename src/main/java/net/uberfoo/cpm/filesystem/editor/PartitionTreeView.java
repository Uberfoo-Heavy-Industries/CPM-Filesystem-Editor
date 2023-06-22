package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;
import net.uberfoo.cpm.filesystem.CpmDisk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

import static net.uberfoo.cpm.filesystem.editor.Util.toMegabytes;

public class PartitionTreeView implements CpmItemTreeView, AcceptsImports, DiskItem {

    private final CpmDisk disk;
    private final StringProperty nameProperty;
    private final BooleanProperty dirtyProperty;

    public PartitionTreeView(CpmDisk disk, String name) {
        this.disk = disk;
        nameProperty = new SimpleStringProperty(name);
        dirtyProperty = new SimpleBooleanProperty(false);
    }

    @Override
    public StringProperty nameProperty() {
        return nameProperty;
    }

    @Override
    public StringProperty sizeProperty() {
        return new ReadOnlyStringWrapper(this, "size", toMegabytes(disk.getDpb().getFilesystemSize()));
    }

    @Override
    public void importFile(ByteBuffer buffer, String filename, int userNum) throws IOException {
        disk.createFile(filename, userNum, new BitSet(11), buffer);
        dirtyProperty.setValue(true);
    }

    public BooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

    @Override
    public CpmDisk disk() {
        return disk;
    }

}
