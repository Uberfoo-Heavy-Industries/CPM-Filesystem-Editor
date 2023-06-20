package net.uberfoo.cpm.filesystem.editor.linux;

import com.sun.jna.platform.linux.Udev;
import net.uberfoo.cpm.filesystem.editor.GenericPlatformDisk;
import net.uberfoo.cpm.filesystem.editor.PlatformDisk;
import net.uberfoo.cpm.filesystem.editor.PlatformDiskFactory;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LinuxPlatformDiskFactory extends PlatformDiskFactory {

    private static final Udev UDEV = Udev.INSTANCE;

    @Override
    public List<OSDiskEntry> getDiskList() throws Exception {
        List<OSDiskEntry> entries;
        try (var stream = Files.list(Path.of("/sys/block"))) {
            var diskPaths = stream.toList();

            entries = new ArrayList<>(diskPaths.size());

            for (var p : diskPaths) {

                var removable = Files.readString(Path.of(p.toString() + "/removable")).startsWith("1");
                var size = Long.valueOf(Files.readString(Path.of(p.toString() + "/size")).trim());

                Udev.UdevContext context = UDEV.udev_new();
                Udev.UdevDevice dev = UDEV.udev_device_new_from_syspath(context, p.toString());

                var address = dev.getPropertyValue("DEVNAME");
                var name = StringEscapeUtils.unescapeJson(dev.getPropertyValue("ID_MODEL_ENC")
                        .replaceAll("\\\\x", "\\\\u00"));

                var entry = new OSDiskEntry(name, address, size * 512, 512, removable);
                entries.add(entry);
            }
        }
        return entries;
    }

    @Override
    public PlatformDisk getDisk(OSDiskEntry disk) throws Exception {
        return new GenericPlatformDisk(disk);
    }
}
