package net.uberfoo.cpm.filesystem.editor;

import net.uberfoo.cpm.filesystem.DiskParameterBlock;
import net.uberfoo.cpm.filesystem.PartitionTable;
import net.uberfoo.cpm.filesystem.PartitionTableEntry;

import java.util.ArrayList;

import static net.uberfoo.cpm.filesystem.DiskParameterBlock.createSkewTab;

public class PartitionTables {
    public static final DiskParameterBlock Z80RB_DPB = new DiskParameterBlock(
            512,
            128,
            5,
            31,
            1,
            2047,
            511,
            240,
            0,
            0,
            0,
            new int[0]
    );

    public static final DiskParameterBlock Z80RB_BOOT_DPB = new DiskParameterBlock(
            512,
            128,
            5,
            31,
            1,
            2047,
            511,
            240,
            0,
            0,
            1,
            new int[0]
    );

    public static final DiskParameterBlock OSBORNE_1_DPB = new DiskParameterBlock(
            256,
            20,
            4,
            15,
            1,
            45,
            63,
            0x80,
            0x00,
            0,
            3,
            createSkewTab(2, 10)
    );


    public static final PartitionTable Z80RB_TABLE;

    static {
        var entries = new ArrayList<PartitionTableEntry>(16);
        entries.add(new PartitionTableEntry(0, "A", Z80RB_BOOT_DPB));
        entries.add(new PartitionTableEntry(0x800000, "B", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x1000000, "C", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x1800000, "D", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x2000000, "E", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x2800000, "F", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x3000000, "G", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x3800000, "H", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x4000000, "I", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x4800000, "J", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x5000000, "K", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x5800000, "L", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x6000000, "M", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x6800000, "N", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x7000000, "O", Z80RB_DPB));
        entries.add(new PartitionTableEntry(0x7800000, "P", Z80RB_DPB));
        Z80RB_TABLE = new PartitionTable(entries);

    }
}
