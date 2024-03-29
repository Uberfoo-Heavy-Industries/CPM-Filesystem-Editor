package net.uberfoo.cpm.filesystem.editor;

import java.io.Closeable;
import java.io.IOException;

public interface PlatformDiskReader extends Closeable {

    void read(byte[] block) throws IOException;

}
