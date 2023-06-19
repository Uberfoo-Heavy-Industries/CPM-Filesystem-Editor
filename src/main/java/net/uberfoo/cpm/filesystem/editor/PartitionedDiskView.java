package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import net.uberfoo.cpm.filesystem.PartitionedDisk;

import java.io.Closeable;
import java.io.IOException;

import static net.uberfoo.cpm.filesystem.editor.Util.toMegabytes;

public record PartitionedDiskView(String name, PartitionedDisk partitionedDisk, Closeable channel) implements CpmItemTreeView, ClosableItem {

    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", name);
    }

    @Override
    public StringProperty sizeProperty() {
        return new ReadOnlyStringWrapper(this, "size", toMegabytes(partitionedDisk.getDiskSize()));
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
