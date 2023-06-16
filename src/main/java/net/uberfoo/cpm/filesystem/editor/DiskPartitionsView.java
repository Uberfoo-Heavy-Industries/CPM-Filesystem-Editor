package net.uberfoo.cpm.filesystem.editor;

import net.uberfoo.cpm.filesystem.DiskParameterBlock;
import net.uberfoo.cpm.filesystem.PartitionTable;
import org.nield.dirtyfx.beans.DirtyIntegerProperty;
import org.nield.dirtyfx.collections.DirtyObservableList;
import org.nield.dirtyfx.tracking.CompositeDirtyProperty;

import java.util.Arrays;
import java.util.List;

public class DiskPartitionsView {

    private final String name;
    private final PartitionTable partitionTable;

    public DiskPartitionsView() {
        this("",null);
    }

    public DiskPartitionsView(String name, PartitionTable table) {
        this.name = name;
        this.partitionTable = table;
    }

    public String getName() {
        return name;
    }

    public PartitionTable getPartitionTable() {
        return partitionTable;
    }

    @Override
    public String toString() {
        return name;
    }
}
