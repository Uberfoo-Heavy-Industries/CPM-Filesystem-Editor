package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.BooleanProperty;

import java.nio.channels.FileChannel;

public interface SavableItem extends CpmItemTreeView {

    void save(FileChannel channel) throws Exception;

}
