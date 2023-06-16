package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static net.uberfoo.cpm.filesystem.editor.WindowsPlatformDiskFactory.FieldEnum.*;

public class WindowsPlatformDiskFactory extends PlatformDiskFactory {

    public enum FieldEnum {
        NAME,
        SIZE,
        MODEL,
        CAPABILITIES,
        CAPABILITYDESCRIPTIONS
    }

    @Structure.FieldOrder({"cylinders", "mediaType", "tracksPerCylinder", "sectorsPerTrack", "bytesPerSector"})
    public static final class DiskGeometry extends Structure {

        public WinNT.LARGE_INTEGER cylinders;
        public int mediaType;
        public WinDef.DWORD tracksPerCylinder;
        public WinDef.DWORD sectorsPerTrack;
        public WinDef.DWORD bytesPerSector;

    }

    @Structure.FieldOrder({"geometry", "diskSize", "data"})
    public static final class DiskGeometryEx extends Structure {

        public DiskGeometryEx(int bufferSize) {
            data = new WinDef.BYTE[bufferSize];
            allocateMemory();
        }

        public DiskGeometry geometry;
        public WinNT.LARGE_INTEGER diskSize;
        public WinDef.BYTE[] data;
    }

    private static final int IOCTL_DISK_GET_DRIVE_GEOMETRY_EX = 458912;

    private static Kernel32 kernel32 = Kernel32.INSTANCE;

    static {
        Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED).intValue();
        Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, Ole32.RPC_C_AUTHN_LEVEL_DEFAULT,
                Ole32.RPC_C_IMP_LEVEL_IMPERSONATE, null, Ole32.EOAC_NONE, null).intValue();
    }

    @Override
    public List<OSDiskEntry> getDiskList() throws Exception {
        WbemcliUtil.WmiQuery processQuery = new WbemcliUtil.WmiQuery<FieldEnum>("Win32_DiskDrive", FieldEnum.class);
        var q = processQuery.execute();
        List<OSDiskEntry> list = new ArrayList<>(q.getResultCount());
        for (int i = 0; i < q.getResultCount(); i++) {
            if (q.getValue(SIZE, i) == null) {
                continue;
            }
            var addr = q.getValue(NAME, i).toString();
            var geometry = getGeometry(addr);
            long size = geometry.diskSize.getValue();
            if (size > 0) {
                list.add(
                        new OSDiskEntry(
                                q.getValue(MODEL, i).toString(),
                                addr,
                                size,
                                geometry.geometry.bytesPerSector.intValue(),
                                geometry.geometry.mediaType == 11
                        ));
            }
        }
        return list;
    }

    @Override
    public PlatformDisk getDisk(OSDiskEntry disk) throws Exception {
        return new WindowsPlatformDisk(disk);
    }

    private static DiskGeometryEx getGeometry(String physicalDrive) throws Exception {
        WinNT.HANDLE f = kernel32.CreateFile(physicalDrive,
                WinNT.GENERIC_READ,
                WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE,
                null,
                WinNT.OPEN_EXISTING,
                0,
                null);
        var error = kernel32.GetLastError();
        switch (error) {
            case 0:
                break;
            case 161:
            case 3:
            case 2:
                throw new FileNotFoundException("Could not open path " + physicalDrive + ". Code = " + error);
            case 5:
                throw new AccessDeniedException("Could not access path " + physicalDrive + ". Code = " + error);
            default:
                throw new Exception("Unexpected error opening path " + physicalDrive + ". Code = " + error);
        }
        try {
            var geometry = new DiskGeometryEx(128);
            IntByReference bytesReturned = new IntByReference();

            var pass = kernel32.DeviceIoControl(f,
                    IOCTL_DISK_GET_DRIVE_GEOMETRY_EX,
                    null, 0,
                    geometry.getPointer(), geometry.size(),
                    bytesReturned,
                    null);

            if (!pass) {
                throw new Exception("Unexpected error for path " + physicalDrive + ". " + WindowsPlatformUtil.getError());
            }

            // Must re-read the memory into the struct
            geometry.read();

            return geometry;
        } finally {
            kernel32.CloseHandle(f);
        }
    }

}
