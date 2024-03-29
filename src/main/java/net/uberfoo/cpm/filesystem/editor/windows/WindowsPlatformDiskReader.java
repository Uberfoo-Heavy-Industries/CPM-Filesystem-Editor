package net.uberfoo.cpm.filesystem.editor.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskReader;

import java.io.IOException;

import static net.uberfoo.cpm.filesystem.editor.windows.WindowsPlatformUtil.closeHandle;
import static net.uberfoo.cpm.filesystem.editor.windows.WindowsPlatformUtil.getError;

public class WindowsPlatformDiskReader implements PlatformDiskReader {

    private static final Kernel32 kernel32 = Kernel32.INSTANCE;

    private final WinNT.HANDLE fileHandle;

    public WindowsPlatformDiskReader(WinNT.HANDLE fileHandle) {
        this.fileHandle = fileHandle;
    }

    @Override
    public void read(byte[] block) throws IOException {
        var bytesRead = new IntByReference();
        var success = kernel32.ReadFile(fileHandle,
                block,
                block.length,
                bytesRead,
                null);

        if (!success) throw new IOException(getError());
    }

    @Override
    public void close() throws IOException {
        closeHandle(fileHandle);
    }
}
