package net.uberfoo.cpm.filesystem.editor.macos;

import com.sun.jna.platform.mac.IOKitUtil;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskReader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MacPlatformDiskReader implements PlatformDiskReader {

    private final Path diskPath;
    private final InputStream inputStream;

    public MacPlatformDiskReader(Path diskPath) throws IOException {
        this.diskPath = diskPath;
        inputStream = Files.newInputStream(diskPath);
    }

    @Override
    public byte[] read() throws IOException {
        return inputStream.readNBytes(2048);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(inputStream);
    }
}
