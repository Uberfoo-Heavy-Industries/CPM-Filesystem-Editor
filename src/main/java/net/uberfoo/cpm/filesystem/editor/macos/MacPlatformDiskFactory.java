package net.uberfoo.cpm.filesystem.editor.macos;

import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.sun.jna.platform.mac.DiskArbitration;
import net.uberfoo.cpm.filesystem.editor.GenericPlatformDisk;
import net.uberfoo.cpm.filesystem.editor.PlatformDisk;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MacPlatformDiskFactory extends PlatformDiskFactory {

    private static final DiskArbitration DA = DiskArbitration.INSTANCE;
    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final CFStringRef REMOVABLE_KEY = CFStringRef.createCFString("DAMediaRemovable");
    private static final CFStringRef BLK_SIZE_KEY = CFStringRef.createCFString("DAMediaBlockSize");
    private static final CFStringRef DISK_SIZE_KEY = CFStringRef.createCFString("DAMediaSize");
    private static final CFStringRef DISK_NAME_KEY = CFStringRef.createCFString("DAMediaName");

    @Override
    public List<OSDiskEntry> getDiskList() throws Exception {
        try (var stream = Files.list(Path.of("/dev"))) {
            var diskPaths = stream.filter(f -> f.getFileName().toString().matches("^disk[0-9]+$"))
                    .toList();

            List<OSDiskEntry> entries = new ArrayList<>(diskPaths.size());

            diskPaths.forEach(p -> {
                var alloc = CF.CFAllocatorGetDefault();
                var session = DA.DASessionCreate(alloc);
                var diskRef = DA.DADiskCreateFromBSDName(alloc, session, p.toString());
                var dict = DA.DADiskCopyDescription(diskRef);

                var desc = CF.CFCopyDescription(dict);
                System.out.println(desc.stringValue());

                var removable = new CFBooleanRef(dict.getValue(REMOVABLE_KEY));
                var blkSize = new CoreFoundation.CFNumberRef(dict.getValue(BLK_SIZE_KEY));
                var diskSize = new CoreFoundation.CFNumberRef(dict.getValue(DISK_SIZE_KEY));
                var diskName = new CoreFoundation.CFStringRef(dict.getValue(DISK_NAME_KEY));

                var entry = new OSDiskEntry(diskName.stringValue(),
                        p.toString(),
                        diskSize.longValue(),
                        blkSize.intValue(),
                        removable.booleanValue());
                entries.add(entry);
            });

            return entries;
        }
    }

    @Override
    public PlatformDisk getDisk(OSDiskEntry disk) throws Exception {
        return new GenericPlatformDisk(disk);
    }
}
