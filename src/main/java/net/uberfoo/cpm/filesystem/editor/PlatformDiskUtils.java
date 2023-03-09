package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static net.uberfoo.cpm.filesystem.editor.WindowsPlatformDiskFactory.FieldEnum.*;

public final class PlatformDiskUtils {

    public class Windows {

        private static Kernel32 kernel32 = Kernel32.INSTANCE;

        static {
            Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED).intValue();
            Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, Ole32.RPC_C_AUTHN_LEVEL_DEFAULT,
                    Ole32.RPC_C_IMP_LEVEL_IMPERSONATE, null, Ole32.EOAC_NONE, null).intValue();
        }

        public synchronized static final List<PlatformDiskFactory.OSDiskEntry> getDiskList() throws Exception {
            WbemcliUtil.WmiQuery processQuery = new WbemcliUtil.WmiQuery<WindowsPlatformDiskFactory.FieldEnum>("Win32_DiskDrive", WindowsPlatformDiskFactory.FieldEnum.class);
            var q = processQuery.execute();
            List<PlatformDiskFactory.OSDiskEntry> list = new ArrayList<>(q.getResultCount());
            for (int i = 0; i < q.getResultCount(); i++) {
                if (q.getValue(SIZE, i) == null) {
                    continue;
                }
                var name = q.getValue(NAME, i).toString();
                long size = getSize(name);
                if (size > 0) {
                    list.add(new PlatformDiskFactory.OSDiskEntry(q.getValue(MODEL, i).toString(), q.getValue(NAME, i).toString(), size));
                }
            }

            return list;
        }

        public static final long getSize(String physicalDrive) throws Exception {
            WinNT.HANDLE f = kernel32.CreateFile(physicalDrive, -2147483648, 3, null, 3, 128, null);
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
            try (var mem = new Memory(8)) {
                IntByReference bytesReturned = new IntByReference();
                var pass = kernel32.DeviceIoControl(f, 475228, null, 0, mem, 8, bytesReturned, null);
                error = kernel32.GetLastError();
                if (!pass || error != 0) {
                    throw new Exception("Unexpected error for path " + physicalDrive + ". Code = " + error);
                }
                return mem.getLong(0);
            } finally {
                kernel32.CloseHandle(f);
            }
        }
    }


}
