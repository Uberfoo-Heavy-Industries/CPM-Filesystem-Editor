package net.uberfoo.cpm.filesystem.editor.test;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import net.uberfoo.cpm.filesystem.editor.WindowsPlatformDiskFactory;
import org.junit.jupiter.api.Test;

public class PlatformTest {

    private static Kernel32 kernel32 = Kernel32.INSTANCE;

    @Test
    public void test() throws Exception {
        var bytesRet = new IntByReference();
        var mem = new Memory(128);
        var geo = new WindowsPlatformDiskFactory.DiskGeometryEx(128);


        WinNT.HANDLE f = kernel32.CreateFile("\\\\.\\PhysicalDrive3", 268435456, 0, null, 3, 0x02000000, null);

        var ret = kernel32.DeviceIoControl(f, 458912, null, 0, geo.getPointer(), 512, bytesRet, null);

        geo.read();
        System.out.println(ret);
        System.out.println(kernel32.GetLastError());
        System.out.println(geo.getPointer().getByte(0));
        System.out.println(geo.geometry.bytesPerSector);
    }

}
