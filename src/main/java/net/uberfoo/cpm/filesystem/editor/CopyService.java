package net.uberfoo.cpm.filesystem.editor;

import java.io.File;
import java.util.List;

public interface CopyService {

    void copyToCpm(DiskItem diskView, List<File> files, String name);

}
