package ru.molkov.collapsar.utils;

import android.os.Environment;

import java.io.File;

public class FileUtils {

    public static boolean createDownloadPath() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        File picturesFolder = new File(Environment.getExternalStorageDirectory().toString() + Constants.PICTURES_PATH);
        if (!picturesFolder.exists()) {
            if (!picturesFolder.mkdir()) {
                return false;
            }
        }
        File collapsarFolder = new File(Environment.getExternalStorageDirectory().toString() + Constants.DOWNLOAD_IMAGE_PATH);
        if (!collapsarFolder.exists()) {
            if (!collapsarFolder.mkdir()) {
                return false;
            }
        }
        return true;
    }

}
