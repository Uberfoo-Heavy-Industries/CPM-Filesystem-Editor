package net.uberfoo.cpm.filesystem.editor;

public class Util {

    private Util() {}

    public static String toMegabytes(long bytes) {
        return String.format("%.2f Mb", bytes / (1024f * 1024f));
    }

    public static String toKilobytes(long bytes) {
        return String.format("%.2f Kb", bytes / 1024f);
    }

    public static String toByteUnit(long bytes) {
        if (bytes < 1024) {
            return bytes + " b";
        }
        if (bytes < 1024 * 1024) {
            return toKilobytes(bytes);
        }
        return toMegabytes(bytes);
    }
}
