package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.BooleanProperty;
import net.uberfoo.cpm.filesystem.CpmDisk;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface DiskItem extends AcceptsImports {

    CpmDisk disk();

    BooleanProperty dirtyProperty();

    default void importBootTracks(ByteBuffer buffer) throws IOException {
        disk().writeBootTracks(buffer);
        dirtyProperty().setValue(true);
    }

}
