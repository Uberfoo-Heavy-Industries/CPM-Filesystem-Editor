package net.uberfoo.cpm.filesystem.editor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface PlatformDiskWriter extends Closeable {

    void write(byte[] bytes) throws IOException;

}
