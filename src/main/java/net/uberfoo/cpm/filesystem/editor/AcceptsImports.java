package net.uberfoo.cpm.filesystem.editor;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface AcceptsImports extends CpmItemTreeView {

    default void importFile(ByteBuffer buffer, String filename, int userNum) throws IOException {
        importFile(buffer, filename, userNum, false);
    }

    void importFile(ByteBuffer buffer, String filename, int userNum, boolean overwrite) throws IOException;

}
