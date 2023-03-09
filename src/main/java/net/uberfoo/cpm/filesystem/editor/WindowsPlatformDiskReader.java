package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;

import static net.uberfoo.cpm.filesystem.editor.WindowsPlatformUtil.closeHandle;
import static net.uberfoo.cpm.filesystem.editor.WindowsPlatformUtil.getError;

public class WindowsPlatformDiskReader implements PlatformDiskReader {

    private static Kernel32 kernel32 = Kernel32.INSTANCE;

    private final WinNT.HANDLE fileHandle;

    public WindowsPlatformDiskReader(WinNT.HANDLE fileHandle) {
        this.fileHandle = fileHandle;
    }

    @Override
    public byte[] read() throws IOException {
        var arr = new byte[4096];
        var bytesRead = new IntByReference();
        var success = kernel32.ReadFile(fileHandle,
                arr,
                4096,
                bytesRead,
                null);

        if (!success) throw new IOException(getError());

        return arr;
    }

    @Override
    public void close() throws IOException {
        closeHandle(fileHandle);
    }
}
