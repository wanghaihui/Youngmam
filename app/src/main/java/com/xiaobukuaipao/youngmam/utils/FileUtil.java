package com.xiaobukuaipao.youngmam.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xiaobu1 on 15-5-18.
 */
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    // 根目录
    public static final String SD_PATH = Environment.getExternalStorageDirectory() + "/huayoungmam/";

    // 临时图片目录
    public static final String SD_PATH_PHOTO_TEMP = SD_PATH + "photo/temp/";

    public static final String SD_PATH_PHOTO_AVATAR = SD_PATH + "photo/avatar/";

    /**
     * 分隔符替换
     */
    public static String separatorReplace(String path) {
        return path.replace("\\", "/");
    }

    /**
     * 创建文件夹
     */
    public static void createFolder(String path) throws Exception {
        path = separatorReplace(path);
        File folder = new File(path);
        if (folder.isDirectory()) {
            return;
        } else if (folder.isFile()) {
            deleteFile(path);
        }

        folder.mkdirs();
    }

    /**
     * 创建文件
     */
    public static File createFile(String path) throws Exception {
        path = separatorReplace(path);
        File file = new File(path);
        if (file.isFile()) {
            return file;
        } else if (file.isDirectory()) {
            // 删除文件夹
            deleteFolder(path);
        }

        // 创建文件
        return createFile(file);
    }

    public static File createFile(File file) throws Exception {
        createParentFolder(file);
        if (!file.createNewFile()) {
            throw new Exception("create file failure!");
        }
        return file;
    }

    /**
     * 创建父文件夹
     */
    public static void createParentFolder(File file) throws Exception {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Exception("create parent directory failure!");
            }
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String path) throws Exception {
        path = separatorReplace(path);
        File file = getFile(path);
        if (!file.delete()) {
            throw new Exception("delete file failure");
        }
    }

    /**
     * 得到文件
     */
    public static File getFile(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File file = new File(path);
        if (!file.isFile()) {
            throw new FileNotFoundException("file not found!");
        }
        return file;
    }

    /**
     * 删除文件夹
     */
    public static void deleteFolder(String path) throws Exception {
        path = separatorReplace(path);
        File folder = getFolder(path);
        File[] files = folder.listFiles();
        for(File file : files) {
            if (file.isDirectory()) {
                deleteFolder(file.getAbsolutePath());
            } else if (file.isFile()) {
                deleteFile(file.getAbsolutePath());
            }
        }

        folder.delete();
    }

    /**
     * 删除文件夹下的所有图片
     */
    public static void deleteFolderFiles(String path) throws Exception {
        path = separatorReplace(path);
        File folder = getFolder(path);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteFolder(file.getAbsolutePath());
            } else if (file.isFile()) {
                deleteFile(file.getAbsolutePath());
            }
        }
    }

    /**
     * 得到文件夹
     */
    public static File getFolder(String path) throws FileNotFoundException {
        path = separatorReplace(path);
        File folder = new File(path);
        if (!folder.isDirectory()) {
            throw new FileNotFoundException("folder not found!");
        }
        return folder;
    }

    /**
     * 保存Bitmap
     */
    public static void saveBitmap(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Log.d(TAG, "已经保存");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
