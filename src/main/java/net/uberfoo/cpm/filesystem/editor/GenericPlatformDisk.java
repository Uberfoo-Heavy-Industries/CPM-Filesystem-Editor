package net.uberfoo.cpm.filesystem.editor;

import net.uberfoo.cpm.filesystem.editor.*;

import java.io.IOException;
import java.nio.file.Path;

public class GenericPlatformDisk implements PlatformDisk {

    private final PlatformDiskFactory.OSDiskEntry diskEntry;

    public GenericPlatformDisk(PlatformDiskFactory.OSDiskEntry diskEntry) {
        this.diskEntry = diskEntry;
    }

    @Override
    public long getSize() throws Exception {
        return diskEntry.size();
    }

    @Override
    public PlatformDiskReader openReader() throws IOException {
        return new GenericPlatformDiskReader(Path.of(diskEntry.address()));
    }

    @Override
    public PlatformDiskWriter openWriter() throws IOException {
        return new GenericPlatformDiskWriter(Path.of(diskEntry.address()));
    }
}
