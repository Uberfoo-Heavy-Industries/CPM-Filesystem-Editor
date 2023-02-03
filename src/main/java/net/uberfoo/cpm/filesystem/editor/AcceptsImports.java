package net.uberfoo.cpm.filesystem.editor;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface AcceptsImports extends CpmItemTreeView {

    void importFile(ByteBuffer buffer, String filename, int userNum) throws IOException;

}
