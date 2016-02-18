package com.xiaobukuaipao.youngmam.filter.util;

import android.os.Environment;

import com.xiaobukuaipao.youngmam.HuaYoungApplication;

import java.io.File;

/**
 * Created by xiaobu1 on 15-9-19.
 */
public class FileUtils {
    private static String    BASE_PATH;
    private static String    STICKER_BASE_PATH;

    private static FileUtils mInstance;

    public static FileUtils getInstance() {
        if (mInstance == null) {
            synchronized (FileUtils.class) {
                if (mInstance == null) {
                    mInstance = new FileUtils();
                }
            }
        }
        return mInstance;
    }

    private FileUtils() {
        String sdcardState = Environment.getExternalStorageState();
        // 如果没SD卡, 则放缓存
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            // 得到外部存储器路径
            BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/huayoungmam/";
        } else {
            BASE_PATH = HuaYoungApplication.getInstance().getCacheDirPath();
        }

        STICKER_BASE_PATH = BASE_PATH + "/stickers/";
    }

    public String getPhotoSavedPath() {
        return BASE_PATH + "beautify";
    }

    public String getPhotoDownloadPath() {
        return BASE_PATH + "download";
    }

    public boolean mkdir(File file) {
        while (!file.getParentFile().exists()) {
            mkdir(file.getParentFile());
        }
        return file.mkdir();
    }
}
