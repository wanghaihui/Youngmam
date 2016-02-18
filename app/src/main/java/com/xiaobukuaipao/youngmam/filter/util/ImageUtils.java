package com.xiaobukuaipao.youngmam.filter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;

import com.xiaobukuaipao.youngmam.utils.DisplayUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaobu1 on 15-9-10.
 */
public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    // 保存图片文件
    public static String saveToFile(String fileFolderStr, boolean isDir, Bitmap croppedImage) throws FileNotFoundException, IOException {
        File jpgFile;
        if (isDir) {
            File fileFolder = new File(fileFolderStr);
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
            String filename = format.format(date) + ".jpg";
            if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
                FileUtils.getInstance().mkdir(fileFolder);
            }
            jpgFile = new File(fileFolder, filename);
        } else {
            jpgFile = new File(fileFolderStr);
            if (!jpgFile.getParentFile().exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
                FileUtils.getInstance().mkdir(jpgFile.getParentFile());
            }
        }

        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流

        croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        IOUtil.closeStream(outputStream);
        return jpgFile.getPath();
    }

    /**
     * 获取图片的原始宽度和高度--根据文件路径
     */
    public static int[] calculateSourceImageWH(String pathName) {
        int sourceWH[] = new int[2];

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        // 设置inPurgeable和inInputShareable--让系统能及时回收内存
        options.inInputShareable = true;
        options.inPurgeable = true;

        BitmapFactory.decodeFile(pathName, options);

        sourceWH[0] = options.outWidth;
        sourceWH[1] = options.outHeight;

        final int degrees = getImageDegrees(pathName);

        if (degrees == 90 || degrees == 270) {
            sourceWH[0] = options.outHeight;
            sourceWH[1] = options.outWidth;
        }

        return sourceWH;
    }

    /**
     * 根据图片的原始长度和宽度, 以及图片被Center Inside/Crop之后的图片的长度和宽度, 来决定画布的大小
     */
    public static int[] calculateDrawableOverlay(int sourceWidth, int sourceHeight,
                                                 int gpuImageWidth, int gpuImageHeight) {
        int[] overlay = new int[2];

        if (sourceWidth == sourceHeight) {
            overlay[0] = gpuImageWidth;
            overlay[1] = gpuImageWidth;
        } else if (sourceWidth > sourceHeight) {
            overlay[0] = gpuImageWidth;
            overlay[1] = gpuImageWidth * sourceHeight / sourceWidth;
        } else {
            overlay[0] = gpuImageHeight * sourceWidth / sourceHeight;
            overlay[1] = gpuImageHeight;
        }

        return overlay;
    }

    /**
     * 从文件中读取Bitmap
     */
    public static Bitmap decodeBitmapWithOrientationMax(String pathName, int width, int height) {
        return decodeBitmapWithSize(pathName, width, height, false);
    }

    private static Bitmap decodeBitmapWithSize(String pathName, int width, int height, boolean useBigger) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        // 设置inPurgeable和inInputShareable--让系统能及时回收内存
        options.inInputShareable = true;
        options.inPurgeable = true;

        BitmapFactory.decodeFile(pathName, options);

        int decodeWidth = width;
        int decodeHeight = height;

        final int degrees = getImageDegrees(pathName);

        if (degrees == 90 || degrees == 270) {
            decodeWidth = height;
            decodeHeight = width;
        }

        if (decodeWidth > 2048) {
            decodeWidth = 2048;
        }
        if (decodeHeight > 2048) {
            decodeHeight = 2048;
        }

        if (useBigger) {
            options.inSampleSize = (int) Math.min(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        } else {
            options.inSampleSize = (int) Math.max(((float) options.outWidth / decodeWidth),
                    ((float) options.outHeight / decodeHeight));
        }

        /*if (options.outWidth > 2048 && options.outHeight > 2048) {
            if(options.outWidth >= options.outHeight) {
                // 以宽为主
                int tempWidth = 2048;
                int tempHeight = 2048 * options.outHeight / options.outWidth;

                options.outWidth = tempWidth;
                options.outHeight = tempHeight;
            } else {
                // 以高为主
                int tempHeight = 2048;
                int tempWidth = 2048 * options.outWidth / options.outHeight;

                options.outWidth = tempWidth;
                options.outHeight = tempHeight;
            }
        } else if (options.outWidth > 2048 && options.outHeight <= 2048) {
            int tempWidth = 2048;
            int tempHeight = 2048 * options.outHeight / options.outWidth;

            options.outWidth = tempWidth;
            options.outHeight = tempHeight;
        } else if (options.outWidth <= 2048 && options.outHeight > 2048) {
            int tempHeight = 2048;
            int tempWidth = 2048 * options.outWidth / options.outHeight;

            options.outWidth = tempWidth;
            options.outHeight = tempHeight;
        }*/

        options.inJustDecodeBounds = false;

        Bitmap sourceBitmap = BitmapFactory.decodeFile(pathName, options);

        return imageWithFixedRotation(sourceBitmap, degrees);
    }

    public static int getImageDegrees(String pathName) {
        int degrees = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(pathName);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degrees;
    }

    public static Bitmap imageWithFixedRotation(Bitmap bitmap, int degrees) {

        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        if (degrees == 0) {
            return bitmap;
        }

        final Matrix matrix = new Matrix();
        // 设置矩阵的旋转角度
        matrix.postRotate(degrees);

        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (result != bitmap) {
            bitmap.recycle();
        }

        return result;
    }

    public static void asyncLoadImag(Context context, Uri imageUri, LoadImageCallback callback) {
        new LoadImageUriTask(context, imageUri, callback).execute();
    }

    private static class LoadImageUriTask extends AsyncTask<Void, Void, Bitmap> {

        private final Context context;
        private final Uri imageUri;
        private LoadImageCallback callback;

        public LoadImageUriTask(Context context, Uri imageUri, LoadImageCallback callback) {
            this.context = context;
            this.imageUri = imageUri;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                // 输入流
                InputStream inputStream;

                if (imageUri.getScheme().startsWith("http") || imageUri.getScheme().startsWith("https")) {
                    inputStream = new URL(imageUri.toString()).openStream();
                    return BitmapFactory.decodeStream(inputStream);
                } else {
                    inputStream = context.getContentResolver().openInputStream(imageUri);

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, options);

                    // 此时, 要调整图片的方向, 同时调整图片的大小
                    if (options.outWidth > DisplayUtil.getScreenWidth(context)) {
                        return decodeBitmapWithOrientationMax(imageUri.getPath(),
                                options.outWidth,
                                options.outHeight);
                    } else {
                        return decodeBitmapWithOrientationMax(imageUri.getPath(),
                                DisplayUtil.getScreenWidth(context),
                                DisplayUtil.getScreenWidth(context));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            callback.callback(result);
        }
    }

    public static void asyncLoadSmallImage(Context context, Uri imageUri, LoadImageCallback callback) {
        new LoadSmallImageTask(context, imageUri, callback).execute();
    }

    private static class LoadSmallImageTask extends AsyncTask<Void, Void, Bitmap> {
        private final Context context;
        private final Uri imageUri;
        private LoadImageCallback callback;

        public LoadSmallImageTask(Context context, Uri imageUri, LoadImageCallback callback) {
            this.context = context;
            this.imageUri = imageUri;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            // 得到
            return getResizedBitmap(context, imageUri, DisplayUtil.dip2px(context, 90), DisplayUtil.dip2px(context, 90));
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            callback.callback(result);
        }
    }

    /**
     * 得到指定大小的Bitmap对象
     */
    public static Bitmap getResizedBitmap(Context context, Uri imageUri, int width, int height) {
        InputStream inputStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            inputStream = context.getContentResolver().openInputStream(imageUri);

            BitmapFactory.decodeStream(inputStream, null, options);

            options.outWidth = width;
            options.outHeight = height;
            options.inJustDecodeBounds = false;

            IOUtil.closeStream(inputStream);

            inputStream = context.getContentResolver().openInputStream(imageUri);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            return ThumbnailUtils.extractThumbnail(imageWithFixedRotation(bitmap, getImageDegrees(imageUri.getPath())),
                    DisplayUtil.dip2px(context, 60), DisplayUtil.dip2px(context, 60));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(inputStream);
        }

        return null;
    }

    /**
     * 异步加载图片
     */
    public interface LoadImageCallback {
        void callback(Bitmap result);
    }
}
