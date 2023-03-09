package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.Platform;

import java.util.List;

public abstract class PlatformDiskFactory {

    public static final PlatformDiskFactory getInstance() throws Exception {
        switch (Platform.getOSType()) {

            case Platform.WINDOWS:
                return new WindowsPlatformDiskFactory();

            default:
                throw new Exception("Unsupported platform");
        }
    }

    public abstract List<OSDiskEntry> getDiskList() throws Exception;

    public abstract PlatformDisk getDisk(OSDiskEntry disk) throws Exception;

    public record OSDiskEntry(String name, String address, long size) {}
}
