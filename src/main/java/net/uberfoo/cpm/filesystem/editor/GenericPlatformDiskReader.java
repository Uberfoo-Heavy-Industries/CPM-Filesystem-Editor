package net.uberfoo.cpm.filesystem.editor;

import net.uberfoo.cpm.filesystem.editor.PlatformDiskReader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenericPlatformDiskReader implements PlatformDiskReader {

    private final Path diskPath;
    private final InputStream inputStream;

    public GenericPlatformDiskReader(Path diskPath) throws IOException {
        this.diskPath = diskPath;
        inputStream = Files.newInputStream(diskPath);
    }

    @Override
    public void read(byte[] block) throws IOException {
        inputStream.read(block);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(inputStream);
    }
}
