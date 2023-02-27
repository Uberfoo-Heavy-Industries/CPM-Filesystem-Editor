package net.uberfoo.cpm.filesystem.editor;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Ole32;

import java.util.ArrayList;
import java.util.List;

import static net.uberfoo.cpm.filesystem.editor.PlatformDiskUtils.Windows.FieldEnum.*;

public final class PlatformDiskUtils {

    public record OSDiskEntry(String name, String address, long size) {}

    public class Windows {

        public enum FieldEnum {
            NAME,
            SIZE,
            CAPABILITIES,
            CAPABILITYDESCRIPTIONS
        }

        static {
            Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED).intValue();
            Ole32.INSTANCE.CoInitializeSecurity(null, -1, null, null, Ole32.RPC_C_AUTHN_LEVEL_DEFAULT,
                    Ole32.RPC_C_IMP_LEVEL_IMPERSONATE, null, Ole32.EOAC_NONE, null).intValue();
        }

        public synchronized static final List<OSDiskEntry> getDiskList()  {
            WbemcliUtil.WmiQuery processQuery = new WbemcliUtil.WmiQuery<FieldEnum>("Win32_DiskDrive", FieldEnum.class);
            var q = processQuery.execute();

            List<OSDiskEntry> list = new ArrayList<>(q.getResultCount());
            for (int i = 0; i < q.getResultCount(); i++) {
                var sizeValue = q.getValue(SIZE, i);
                if (sizeValue != null) {
                    list.add(new OSDiskEntry(q.getValue(NAME, i).toString(), q.getValue(NAME, i).toString(), Long.parseLong(sizeValue.toString())));
                }
            }

            return list;
        }

    }


}
