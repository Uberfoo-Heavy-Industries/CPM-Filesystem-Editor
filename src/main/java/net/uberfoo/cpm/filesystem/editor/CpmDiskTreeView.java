package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;
import net.uberfoo.cpm.filesystem.CpmDisk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.BitSet;

import static net.uberfoo.cpm.filesystem.editor.Util.toMegabytes;

public class CpmDiskTreeView implements CpmItemTreeView, ClosableItem, AcceptsImports, DiskItem, SavableItem {

    private final CpmDisk disk;
    private final StringProperty nameProperty;
    private final BooleanProperty dirtyProperty;

    public CpmDiskTreeView(CpmDisk disk, String name) {
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
    public void close() throws IOException {

    }

    @Override
    public void importFile(ByteBuffer buffer, String filename, int userNum, boolean overwrite) throws IOException {
        if (overwrite && disk.findFile(filename, userNum).isPresent()) {
            disk.deleteFile(filename, userNum);
        }
        disk.createFile(filename, userNum, new BitSet(11), buffer);
        dirtyProperty.setValue(true);
    }

    @Override
    public void save(FileChannel channel) throws Exception {
        channel.write(disk.getBuffer());
        dirtyProperty.setValue(false);
    }

    @Override
    public BooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

    @Override
    public CpmDisk disk() {
        return disk;
    }
}
