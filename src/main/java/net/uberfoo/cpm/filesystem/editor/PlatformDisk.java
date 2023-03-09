package net.uberfoo.cpm.filesystem.editor;

import java.io.IOException;
import java.util.List;

public interface PlatformDisk {

    long getSize() throws Exception;

    PlatformDiskReader openReader() throws IOException;

    PlatformDiskWriter openWriter() throws IOException;

}
