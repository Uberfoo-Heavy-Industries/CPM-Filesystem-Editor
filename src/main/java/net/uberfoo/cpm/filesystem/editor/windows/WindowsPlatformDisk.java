package net.uberfoo.cpm.filesystem.editor.windows;

import net.uberfoo.cpm.filesystem.editor.PlatformDisk;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskFactory;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskReader;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskWriter;

import java.io.IOException;

import static net.uberfoo.cpm.filesystem.editor.windows.WindowsPlatformUtil.openDisk;

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
