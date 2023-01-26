package net.uberfoo.cpm.filesystem.editor;

import net.uberfoo.cpm.filesystem.DiskParameterBlock;
import org.nield.dirtyfx.beans.DirtyIntegerProperty;
import org.nield.dirtyfx.collections.DirtyObservableList;
import org.nield.dirtyfx.tracking.CompositeDirtyProperty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DiskParameterBlockView {

    private final String name;

    private DirtyIntegerProperty sectorSize;
    private DirtyIntegerProperty recordsPerTrack;
    private DirtyIntegerProperty blockShiftFactor;
    private DirtyIntegerProperty blockMask;
    private DirtyIntegerProperty extentMask;
    private DirtyIntegerProperty storageSize;
    private DirtyIntegerProperty numDirectoryEntries;
    private DirtyIntegerProperty directoryAllocationBitmap1;
    private DirtyIntegerProperty directoryAllocationBitmap2;
    private DirtyIntegerProperty checkVectorSize;
    private DirtyIntegerProperty offset;
    private DirtyObservableList<Integer> skewTab;

    private CompositeDirtyProperty dirty;

    public DiskParameterBlockView(
            String name,
            int sectorSize,
            int recordsPerTrack,
            int blockShiftFactor,
            int blockMask,
            int extentMask,
            int storageSize,
            int numDirectoryEntries,
            int directoryAllocationBitmap1,
            int directoryAllocationBitmap2,
            int checkVectorSize,
            int offset,
            int[] skewTab
    ) {
        this.name = name;
        dirty = new CompositeDirtyProperty();

        this.sectorSize = new DirtyIntegerProperty(sectorSize);
        dirty.add(this.sectorSize);

        this.recordsPerTrack = new DirtyIntegerProperty(recordsPerTrack);
        dirty.add(this.recordsPerTrack);

        this.blockShiftFactor = new DirtyIntegerProperty(blockShiftFactor);
        dirty.add(this.blockShiftFactor);

        this.blockMask = new DirtyIntegerProperty(blockMask);
        dirty.add(this.blockMask);

        this.extentMask = new DirtyIntegerProperty(extentMask);
        dirty.add(this.extentMask);

        this.storageSize = new DirtyIntegerProperty(storageSize);
        dirty.add(this.storageSize);

        this.numDirectoryEntries = new DirtyIntegerProperty(numDirectoryEntries);
        dirty.add(this.numDirectoryEntries);

        this.directoryAllocationBitmap1 = new DirtyIntegerProperty(directoryAllocationBitmap1);
        dirty.add(this.directoryAllocationBitmap1);

        this.directoryAllocationBitmap2 = new DirtyIntegerProperty(directoryAllocationBitmap2);
        dirty.add(this.directoryAllocationBitmap2);

        this.checkVectorSize = new DirtyIntegerProperty(checkVectorSize);
        dirty.add(this.checkVectorSize);

        this.offset = new DirtyIntegerProperty(offset);
        dirty.add(this.offset);

        this.skewTab = new DirtyObservableList<>(Arrays.stream(skewTab).boxed().toList());
        dirty.add(this.skewTab);
    }

    public DiskParameterBlockView() {
        this("",0,0,0,0,0,0,0,0,0,0,0,new int[0]);
    }

    public DiskParameterBlockView(String name, DiskParameterBlock dpb) {
        this(
                name,
                dpb.sectorSize(),
                dpb.recordsPerTack(),
                dpb.blockShiftFactor(),
                dpb.blockMask(),
                dpb.extentMask(),
                dpb.storageSize(),
                dpb.numDirectoryEntries(),
                dpb.directoryAllocationBitmap1(),
                dpb.directoryAllocationBitmap2(),
                dpb.checkVectorSize(),
                dpb.offset(),
                dpb.skewTab()
        );
    }

    public int getRecordsPerTrack() {
        return recordsPerTrack.get();
    }

    public DirtyIntegerProperty recordsPerTrackProperty() {
        return recordsPerTrack;
    }

    public void setRecordsPerTrack(int recordsPerTrack) {
        this.recordsPerTrack.set(recordsPerTrack);
    }

    public int getBlockShiftFactor() {
        return blockShiftFactor.get();
    }

    public DirtyIntegerProperty blockShiftFactorProperty() {
        return blockShiftFactor;
    }

    public void setBlockShiftFactor(int blockShiftFactor) {
        this.blockShiftFactor.set(blockShiftFactor);
    }

    public int getBlockMask() {
        return blockMask.get();
    }

    public DirtyIntegerProperty blockMaskProperty() {
        return blockMask;
    }

    public void setBlockMask(int blockMask) {
        this.blockMask.set(blockMask);
    }

    public int getExtentMask() {
        return extentMask.get();
    }

    public DirtyIntegerProperty extentMaskProperty() {
        return extentMask;
    }

    public void setExtentMask(int extentMask) {
        this.extentMask.set(extentMask);
    }

    public int getStorageSize() {
        return storageSize.get();
    }

    public DirtyIntegerProperty storageSizeProperty() {
        return storageSize;
    }

    public void setStorageSize(int storageSize) {
        this.storageSize.set(storageSize);
    }

    public int getNumDirectoryEntries() {
        return numDirectoryEntries.get();
    }

    public DirtyIntegerProperty numDirectoryEntriesProperty() {
        return numDirectoryEntries;
    }

    public void setNumDirectoryEntries(int numDirectoryEntries) {
        this.numDirectoryEntries.set(numDirectoryEntries);
    }

    public int getDirectoryAllocationBitmap1() {
        return directoryAllocationBitmap1.get();
    }

    public DirtyIntegerProperty directoryAllocationBitmap1Property() {
        return directoryAllocationBitmap1;
    }

    public void setDirectoryAllocationBitmap1(int directoryAllocationBitmap1) {
        this.directoryAllocationBitmap1.set(directoryAllocationBitmap1);
    }

    public int getDirectoryAllocationBitmap2() {
        return directoryAllocationBitmap2.get();
    }

    public DirtyIntegerProperty directoryAllocationBitmap2Property() {
        return directoryAllocationBitmap2;
    }

    public void setDirectoryAllocationBitmap2(int directoryAllocationBitmap2) {
        this.directoryAllocationBitmap2.set(directoryAllocationBitmap2);
    }

    public int getCheckVectorSize() {
        return checkVectorSize.get();
    }

    public DirtyIntegerProperty checkVectorSizeProperty() {
        return checkVectorSize;
    }

    public void setCheckVectorSize(int checkVectorSize) {
        this.checkVectorSize.set(checkVectorSize);
    }

    public int getOffset() {
        return offset.get();
    }

    public DirtyIntegerProperty offsetProperty() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset.set(offset);
    }

    public List<Integer> getSkewTab() {
        return skewTab.getCurrentList();
    }

    public DirtyObservableList skewTabProperty() {
        return skewTab;
    }

    public void setSkewTab(int[] skewTab) {
        this.skewTab.setAll(Arrays.stream(skewTab).boxed().toList());
    }

    public Boolean isDirty() {
        return dirty.isDirty();
    }

    public CompositeDirtyProperty dirtyProperty() {
        return dirty;
    }

    public int getSectorSize() {
        return sectorSize.get();
    }

    public DirtyIntegerProperty sectorSizeProperty() {
        return sectorSize;
    }

    public void setSectorSize(int sectorSize) {
        this.sectorSize.set(sectorSize);
    }

    @Override
    public String toString() {
        return name;
    }

    public DiskParameterBlock toDiskParameterBlock() {
        return new DiskParameterBlock(
                sectorSize.get(),
                recordsPerTrack.get(),
                blockShiftFactor.get(),
                blockMask.get(),
                extentMask.get(),
                storageSize.get(),
                numDirectoryEntries.get(),
                directoryAllocationBitmap1.get(),
                directoryAllocationBitmap2.get(),
                checkVectorSize.get(),
                offset.get(),
                skewTab.getCurrentList().stream().mapToInt(Integer::intValue).toArray());
    }

}
