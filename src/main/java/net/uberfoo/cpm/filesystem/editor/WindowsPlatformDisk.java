package net.uberfoo.cpm.filesystem.editor;

import java.io.IOException;

import static net.uberfoo.cpm.filesystem.editor.WindowsPlatformUtil.openDisk;

public class WindowsPlatformDisk implements PlatformDisk {

    private final PlatformDiskFactory.OSDiskEntry diskEntry;

    protected WindowsPlatformDisk(PlatformDiskFactory.OSDiskEntry diskEntry) {
        this.diskEntry = diskEntry;
    }

    @Override
    public long getSize() {
        return diskEntry.size();
    }

    @Override
    public PlatformDiskReader openReader() throws IOException {
        return new WindowsPlatformDiskReader(openDisk(diskEntry.address()));
    }

    @Override
    public PlatformDiskWriter openWriter() throws IOException {
        return new WindowsPlatformDiskWriter(openDisk(diskEntry.address()));
    }

}
