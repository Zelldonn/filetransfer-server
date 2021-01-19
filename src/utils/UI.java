package utils;

import java.io.File;

public class UI {
    /**
     * Converts byte to string with appropriate unit  * @param byte
     *
     * @return human readable string
     */
    static public String byte2Readable(double _byte) {
        String s = "";
        float mebibyte = 1_048_576F;

        if (_byte < mebibyte / 1024F)
            s = (int) _byte + " B";
        else if (_byte < mebibyte)
            s = String.format("%.2f", _byte / 1024F) + " kB";
        else if (_byte < mebibyte * 1024F) {
            s = String.format("%.2f", _byte / mebibyte) + " MB";
        } else if (_byte < mebibyte * mebibyte) {
            s = String.format("%.2f", _byte / (mebibyte * 1024F)) + " GB";
        }
        return s;
    }

    static public long calculateTotalSize(File[] fileList) {
        long l = 0L;
        for (File file : fileList) {
            if (!file.isDirectory())
                l += file.length();
            else {
                l += calculateTotalSize(file.listFiles());
            }
        }
        return l;
    }

    static public int calculateTotalFiles(File[] fileList) {
        int size = 0;
        for (File file : fileList) {
            if (!file.isDirectory())
                size++;
            else {
                size += calculateTotalFiles(file.listFiles());
            }
        }
        return size;
    }
}