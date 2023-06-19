package net.uberfoo.cpm.filesystem.editor.macos;

import net.uberfoo.cpm.filesystem.editor.PlatformDiskWriter;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MacPlatformDiskWriter implements PlatformDiskWriter {

    private final OutputStream outputStream;

    public MacPlatformDiskWriter(Path path) throws IOException {
        this.outputStream = Files.newOutputStream(path);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(outputStream);
    }

}
