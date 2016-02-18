package com.xiaobukuaipao.youngmam.imagechooser;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-13.
 */
public class ImageLoaderTask extends BaseImageLoaderTask {
    private static final String TAG = ImageLoaderTask.class.getSimpleName();

    /**
     * 上下文对象
     */
    private Context context = null;

    /**
     * 存放图片<文件夹,该文件夹下的图片列表>键值对
     */
    private List<ImageDirectory> mImageDirList = new ArrayList<ImageDirectory>();

    private ImageDirectory mTotalImageDir = new ImageDirectory();

    public ImageLoaderTask(Context context) {
        super();
        this.context = context;
        // 添加全部文件夹目录
        mTotalImageDir.setDirName(context.getResources().getString(R.string.youngmam_all_image));
        mImageDirList.add(mTotalImageDir);

        result = mImageDirList;
    }

    public ImageLoaderTask(Context context, OnTaskResultListener resultListener) {
        super();
        this.context = context;
        // 添加全部文件夹目录
        mTotalImageDir.setDirName(context.getResources().getString(R.string.youngmam_all_image));
        mImageDirList.add(mTotalImageDir);

        result = mImageDirList;
        setOnTaskResultListener(resultListener);
    }

    /**
     * 后台执行线程
     * @param params
     * @return 执行是否成功
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        // 图片的外部存储路径
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();

        // 构建查询条件,现在只查询jpeg和png
        StringBuilder selection = new StringBuilder();
        selection.append(MediaStore.Images.Media.MIME_TYPE).append("=?");
        selection.append(" or ");
        selection.append(MediaStore.Images.Media.MIME_TYPE).append("=?");

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(imageUri, null, selection.toString(),
                    new String[] {"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_ADDED + " desc");
            /**
             * 遍历Cursor
             */
            while (cursor.moveToNext()) {
                /**
                 * 以大小做为过滤条件,过滤掉小于10K的照片
                 */
                if (cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)) > 1024 * 10) {
                    ImageModel item = new ImageModel();
                    // 获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    /**
                     * 如果路径有问题, 则直接跳过--解决问题
                     */
                    if (!new File(path).exists()) {
                        continue;
                    }

                    // 问题--截图ScreenShot不能立刻拿到路径,为什么?
                    item.path = path;
                    mTotalImageDir.addImage(item);

                    // 获取该图片所在的文件夹路径
                    File file = new File(path);

                    String parentName = "";
                    if (file.getParentFile() != null) {
                        parentName = file.getParentFile().getName();
                    } else {
                        parentName = file.getName();
                    }

                    // 构建一个ImageDirectory对象
                    ImageDirectory imageDirectory = new ImageDirectory();
                    // 设置ImageDirectory的文件夹名称
                    imageDirectory.setDirName(parentName);

                    int searchIdx = mImageDirList.indexOf(imageDirectory);
                    if (searchIdx >= 0) {
                        // 如果该目录已经存在,则添加一张图片
                        ImageDirectory directory = mImageDirList.get(searchIdx);
                        directory.addImage(item);
                    } else {
                        // 否则,将目录添加到列表中
                        imageDirectory.addImage(item);
                        mImageDirList.add(imageDirectory);
                    }
                }

            }
        } catch (Exception e) {
            // 此时,代表查找失败
            return false;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return true;
    }
}
