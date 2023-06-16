package net.uberfoo.cpm.filesystem.editor.macos;

import com.sun.jna.ptr.LongByReference;
import net.uberfoo.cpm.filesystem.editor.PlatformDisk;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskFactory;
import net.uberfoo.cpm.filesystem.editor.ioctl.IOCtlLib;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MacPlatformDiskFactory extends PlatformDiskFactory {

    private static final long DKIOCGETBLOCKSIZE = 0x40046418;
    private static final long DKIOCGETBLOCKCOUNT = 0x40086419;

    private static final IOCtlLib C = IOCtlLib.INSTANCE;

    @Override
    public List<OSDiskEntry> getDiskList() throws Exception {
        try (var stream = Files.list(Path.of("/dev"))) {
            var diskPaths = stream.filter(f -> f.getFileName().toString().startsWith("disk"))
                    .toList();

            List<OSDiskEntry> entries = new ArrayList<>(diskPaths.size());

            diskPaths.forEach(p -> {
                int fd = C.open(p.toString(), 0);
                LongByReference blkSize = new LongByReference();
                C.ioctl(fd, DKIOCGETBLOCKSIZE, blkSize);
                LongByReference blkCount = new LongByReference();
                C.ioctl(fd, DKIOCGETBLOCKCOUNT, blkCount);
                var entry = new OSDiskEntry(p.toString(), p.toString(), blkSize.getValue() * blkCount.getValue(), (int)blkSize.getValue(), false);
                C.close(fd);
                entries.add(entry);
            });

            return entries;
        }
    }

    @Override
    public PlatformDisk getDisk(OSDiskEntry disk) throws Exception {
        return new MacPlatformDisk(disk);
    }
}
