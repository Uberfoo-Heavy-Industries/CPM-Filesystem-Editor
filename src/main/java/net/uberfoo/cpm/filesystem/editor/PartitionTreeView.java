package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import net.uberfoo.cpm.filesystem.CpmDisk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

import static net.uberfoo.cpm.filesystem.editor.Util.toMegabytes;

public record PartitionTreeView(CpmDisk disk, String name) implements CpmItemTreeView, AcceptsImports, DiskItem {

    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", name);
    }

    @Override
    public StringProperty sizeProperty() {
        return new ReadOnlyStringWrapper(this, "size", toMegabytes(disk.getDpb().getFilesystemSize()));
    }

    @Override
    public void importFile(ByteBuffer buffer, String filename, int userNum) throws IOException {
        disk.createFile(filename, userNum, new BitSet(11), buffer);
    }

}
