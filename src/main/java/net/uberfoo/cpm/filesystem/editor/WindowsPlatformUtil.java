package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

import java.io.IOException;

public final class WindowsPlatformUtil {

    private static final Kernel32 kernel32 = Kernel32.INSTANCE;

    private WindowsPlatformUtil() {}

    public static String getErrorString(int err) {
        var pointer = new PointerByReference();
        kernel32.FormatMessage(
                WinBase.FORMAT_MESSAGE_ALLOCATE_BUFFER
                        | WinBase.FORMAT_MESSAGE_FROM_SYSTEM
                        | WinBase.FORMAT_MESSAGE_IGNORE_INSERTS,
                null,
                err,
                0,
                pointer,
                0,
                null);

        return pointer.getValue().getWideString(0);
    }

    public static void closeHandle(WinNT.HANDLE handle) throws IOException {
        var success = kernel32.CloseHandle(handle);
        if (!success) {
            throw new IOException(getErrorString(kernel32.GetLastError()));
        }
    }

    public static String getError() {
        var err = kernel32.GetLastError();
        return getErrorString(err);
    }

    public static WinNT.HANDLE openDisk(String name) throws IOException {
        WinNT.HANDLE fileHandle = kernel32.CreateFile(
                name,
                268435456,
                0,
                null,
                3,
                0x02000000,
                null);

        if (fileHandle == WinBase.INVALID_HANDLE_VALUE) throw new IOException(getError());

        return fileHandle;
    }
}
