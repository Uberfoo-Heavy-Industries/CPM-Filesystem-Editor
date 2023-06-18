package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;

import static net.uberfoo.cpm.filesystem.editor.WindowsPlatformUtil.closeHandle;
import static net.uberfoo.cpm.filesystem.editor.WindowsPlatformUtil.getError;

public class WindowsPlatformDiskWriter implements PlatformDiskWriter {

    private static Kernel32 kernel32 = Kernel32.INSTANCE;

    private final WinNT.HANDLE fileHandle;

    public WindowsPlatformDiskWriter(WinNT.HANDLE fileHandle) {
        this.fileHandle = fileHandle;
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        var bytesWritten = new IntByReference();
        var success = kernel32.WriteFile(fileHandle,
                bytes,
                bytes.length,
                bytesWritten,
                null);

        if (!success) throw new IOException(getError());
    }

    @Override
    public void close() throws IOException {
        closeHandle(fileHandle);
    }
}
