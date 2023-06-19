package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.Platform;
import net.uberfoo.cpm.filesystem.editor.macos.MacPlatformDiskFactory;
import net.uberfoo.cpm.filesystem.editor.windows.WindowsPlatformDiskFactory;

import java.util.List;

public abstract class PlatformDiskFactory {

    public static final PlatformDiskFactory getInstance() throws Exception {
        switch (Platform.getOSType()) {

            case Platform.WINDOWS:
                return new WindowsPlatformDiskFactory();

            case Platform.MAC:
                return new MacPlatformDiskFactory();

            default:
                throw new Exception("Unsupported platform");
        }
    }

    public abstract List<OSDiskEntry> getDiskList() throws Exception;

    public abstract PlatformDisk getDisk(OSDiskEntry disk) throws Exception;

    public record OSDiskEntry(String name, String address, long size, int blockSize, boolean removable) {}
}
