package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.*;
import net.uberfoo.cpm.filesystem.CpmDisk;

public class CpmDiskTreeView {

    private final CpmDisk disk;
    private final String name;

    public CpmDiskTreeView(CpmDisk disk, String name) {
        this.disk = disk;
        this.name = name;
    }

    public StringProperty nameProperty() {
        return new ReadOnlyStringWrapper(this, "name", name);
    }

    public LongProperty sizeProperty() {
        return new ReadOnlyLongWrapper(this, "size", disk.getDpb().getFilesystemSize());
    }
}
