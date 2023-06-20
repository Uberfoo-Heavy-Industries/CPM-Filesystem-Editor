package net.uberfoo.cpm.filesystem.editor;

import net.uberfoo.cpm.filesystem.editor.PlatformDiskWriter;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GenericPlatformDiskWriter implements PlatformDiskWriter {

    private final OutputStream outputStream;

    public GenericPlatformDiskWriter(Path path) throws IOException {
        this.outputStream = Files.newOutputStream(path, StandardOpenOption.SYNC);
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
