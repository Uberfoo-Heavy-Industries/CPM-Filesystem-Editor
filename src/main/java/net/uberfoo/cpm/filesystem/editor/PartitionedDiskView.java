package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;
import net.uberfoo.cpm.filesystem.PartitionedDisk;

import java.io.IOException;
import java.nio.channels.FileChannel;

import static net.uberfoo.cpm.filesystem.editor.Util.toMegabytes;

public class PartitionedDiskView implements CpmItemTreeView, ClosableItem, SavableItem {

    private final PartitionedDisk partitionedDisk;
    private final StringProperty nameProperty;

    private final BooleanProperty dirtyProperty;

    public PartitionedDiskView(String name, PartitionedDisk partitionedDisk) {
        this.partitionedDisk = partitionedDisk;
        nameProperty = new SimpleStringProperty(name);
        dirtyProperty = new SimpleBooleanProperty(false);
    }

    @Override
    public StringProperty nameProperty() {
        return nameProperty;
    }

    @Override
    public StringProperty sizeProperty() {
        return new ReadOnlyStringWrapper(this, "size", toMegabytes(partitionedDisk.getDiskSize()));
    }

    public PartitionedDisk partitionedDisk() {
        return partitionedDisk;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void save(FileChannel channel) throws Exception {
        channel.write(partitionedDisk.createDisk());
    }

    @Override
    public BooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

}
