package com.xiaobukuaipao.youngmam.imagechooser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-27.
 * 工具缓存类
 */
public class Bimp {
    private static final String TAG = Bimp.class.getSimpleName();

    public static List<Bitmap> bitmaps = new ArrayList<Bitmap>();

    /**
     * 压缩图片
     * @param path
     */
    public static Bitmap revitionImageSize(String path) throws IOException {
        if (path == null) {
            Log.d(TAG, "path is null");
        }
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        in.close();

        int i = 0;
        Bitmap bitmap = null;

        while (true) {
            if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
                in = new BufferedInputStream(new FileInputStream(new File(path)));
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }

        return bitmap;
    }
}
