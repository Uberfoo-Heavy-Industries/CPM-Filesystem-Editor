package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;
import net.uberfoo.cpm.filesystem.CpmDisk;

import java.nio.channels.FileChannel;

public class CpmDiskTreeView implements CpmItemTreeView {

    private final CpmDisk disk;
    private final String name;
    private final FileChannel channel;

    public CpmDiskTreeView(CpmDisk disk, String name, FileChannel channel) {
        this.disk = disk;
        this.name = name;
        this.channel = channel;
    }

    @Override
    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", name);
    }

    @Override
    public LongProperty sizeProperty() {
        return new ReadOnlyLongWrapper(this, "size", disk.getDpb().getFilesystemSize());
    }

    public FileChannel getChannel() {
        return channel;
    }
}
