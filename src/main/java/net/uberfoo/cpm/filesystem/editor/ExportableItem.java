package net.uberfoo.cpm.filesystem.editor;

import java.nio.ByteBuffer;

public interface ExportableItem extends CpmItemTreeView {

    ByteBuffer retrieveFileContents();

    String getName();
}
