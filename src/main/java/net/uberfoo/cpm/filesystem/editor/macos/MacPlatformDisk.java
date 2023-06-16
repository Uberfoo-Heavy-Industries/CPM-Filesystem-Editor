package net.uberfoo.cpm.filesystem.editor.macos;

import net.uberfoo.cpm.filesystem.editor.PlatformDisk;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskFactory;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskReader;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskWriter;

import java.io.IOException;
import java.nio.file.Path;

public class MacPlatformDisk implements PlatformDisk {

    private final PlatformDiskFactory.OSDiskEntry diskEntry;

    public MacPlatformDisk(PlatformDiskFactory.OSDiskEntry diskEntry) {
        this.diskEntry = diskEntry;
    }

    @Override
    public long getSize() throws Exception {
        return diskEntry.size();
    }

    @Override
    public PlatformDiskReader openReader() throws IOException {
        return new MacPlatformDiskReader(Path.of(diskEntry.address()));
    }

    @Override
    public PlatformDiskWriter openWriter() throws IOException {
        return null;
    }
}
