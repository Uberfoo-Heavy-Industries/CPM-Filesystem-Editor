package net.uberfoo.cpm.filesystem.editor;

import java.io.IOException;

public interface ClosableItem extends CpmItemTreeView {

    void close() throws IOException;

}
