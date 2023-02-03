package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;
import net.uberfoo.cpm.filesystem.CpmDisk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;

public record CpmDiskTreeView(CpmDisk disk, String name,
                              FileChannel channel) implements CpmItemTreeView, ClosableItem, AcceptsImports {

    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", name);
    }

    @Override
    public LongProperty sizeProperty() {
        return new ReadOnlyLongWrapper(this, "size", disk.getDpb().getFilesystemSize());
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public void importFile(ByteBuffer buffer, String filename, int userNum) throws IOException {
        disk.createFile(filename, userNum, new BitSet(11), buffer);
    }
}
