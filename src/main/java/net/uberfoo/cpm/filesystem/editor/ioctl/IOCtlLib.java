package net.uberfoo.cpm.filesystem.editor.ioctl;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibCAPI;

public interface IOCtlLib extends Library, LibCAPI {

    int O_RDWR = 0x2;
    int O_RDONLY = 0x0;
    int O_WRONLY = 0x1;

    String NAME = "c";
    IOCtlLib INSTANCE = Native.load(NAME, IOCtlLib.class);

    int open(String pathname, int flags);
    int ioctl(int fd, long param, Object... o);
    String strerror( int errno );

}
