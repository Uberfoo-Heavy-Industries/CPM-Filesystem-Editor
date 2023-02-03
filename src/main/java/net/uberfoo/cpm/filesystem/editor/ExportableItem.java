package net.uberfoo.cpm.filesystem.editor;

import java.nio.ByteBuffer;

public interface ExportableItem {

    ByteBuffer retrieveFileContents();

    String getName();
}
