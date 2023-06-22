package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.BooleanProperty;
import net.uberfoo.cpm.filesystem.CpmDisk;

public interface DiskItem extends AcceptsImports {

    CpmDisk disk();

    BooleanProperty dirtyProperty();

}
