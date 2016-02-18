package com.xiaobukuaipao.youngmam.gpuimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

/**
 * Created by xiaobu1 on 15-8-31.
 * GPUImageView is a ViewGroup, add GLSurfaceView
 */
// @RemoteView--这个标记指明View的子类可以使用RemoteViews机制
public class GPUImageView extends FrameLayout {
    private static final String TAG = GPUImageView.class.getSimpleName();

    private GLSurfaceView mGLSurfaceView;
    private GPUImage mGPUImage;
    // 图片过滤器--图片滤镜
    private GPUImageFilter mFilter;
    // 强制的大小
    public Size mForceSize = null;
    // 比率, 系数
    private float mRatio = 0.0f;

    public GPUImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GPUImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mGLSurfaceView = new GPUImageGLSurfaceView(context, attrs);
        addView(mGLSurfaceView);

        mGPUImage = new GPUImage(getContext());
        mGPUImage.setGLSurfaceView(mGLSurfaceView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatio != 0.0f) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            Log.d(TAG, "old width : " + width);
            Log.d(TAG, "old height : " + height);

            int newWidth;
            int newHeight;

            if (width / mRatio < height) {
                newWidth = width;
                // round--四舍五入
                newHeight = Math.round(width / mRatio);
            } else {
                newHeight = height;
                newWidth = Math.round(height * mRatio);
            }

            Log.d(TAG, "new width : " + width);
            Log.d(TAG, "new height : " + height);

            int newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY);
            int newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY);

            super.onMeasure(newWidthSpec, newHeightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Retrieve(取回) the GPUImage instance used by this view.
     *
     * @return used GPUImage instance
     */
    public GPUImage getGPUImage() {
        return mGPUImage;
    }

    /**
     * 得到GPUImageView的宽度和高度
     */
    public int getGPUImageViewWidth() {
        return mGLSurfaceView.getMeasuredWidth();
    }

    public int getGPUImageViewHeight() {
        return mGLSurfaceView.getMeasuredHeight();
    }

    /**
     * 设置比率
     * @param ratio
     */
    // TODO Should be an xml attribute. But then GPUImage can not be distributed as .jar anymore.
    public void setRatio(float ratio) {
        mRatio = ratio;
        // 只要调用了requestlayout, 那么measure和onMeasure, 以及layout和onlayout, draw和onDraw都会被调用
        mGLSurfaceView.requestLayout();
        // 此时, GLSurfaceView重新布局, 所以GPUImage也要做适当的改变
        mGPUImage.deleteImage();
    }

    /**
     * 强制设置GLSurfaceView的画布大小
     * @param size
     */
    public void setSize(Size size) {
        mForceSize = size;
        mGLSurfaceView.requestLayout();
        mGPUImage.deleteImage();
    }

    /**
     * Set the scale type of GPUImage.
     * 设置比例缩放
     *
     * @param scaleType the new ScaleType
     */
    public void setScaleType(GPUImage.ScaleType scaleType) {
        mGPUImage.setScaleType(scaleType);
    }

    /**
     * Sets the rotation of the displayed image.
     * 设置显示图片的角度
     *
     * @param rotation new rotation
     */
    public void setRotation(Rotation rotation) {
        mGPUImage.setRotation(rotation);
        // 请求渲染
        requestRender();
    }

    /**
     * Set the filter to be applied on the image.
     * 设置过滤器--设置图片滤镜
     *
     * @param filter Filter that should be applied on the image.
     */
    public void setFilter(GPUImageFilter filter) {
        mFilter = filter;
        mGPUImage.setFilter(filter);
        // 似乎是调用了两次--待测试是否去掉
        requestRender();
    }

    /**
     * Get the current applied filter.
     * 得到当前使用的过滤器
     *
     * @return the current filter
     */
    public GPUImageFilter getFilter() {
        return mFilter;
    }

    /**
     * Sets the image on which the filter should be applied.
     *
     * @param bitmap the new image
     */
    public void setImage(final Bitmap bitmap) {
        mGPUImage.setImage(bitmap);
    }

    /**
     * Sets the image on which the filter should be applied from a Uri.
     *
     * @param uri the uri of the new image
     */
    public void setImage(final Uri uri) {
        mGPUImage.setImage(uri);
    }

    /**
     * Sets the image on which the filter should be applied from a File.
     *
     * @param file the file of the new image
     */
    public void setImage(final File file) {
        mGPUImage.setImage(file);
    }

    /**
     * 重新渲染
     */
    public void requestRender() {
        mGLSurfaceView.requestRender();
    }

    /**
     * Save current image with applied filter to Pictures. It will be stored on
     * the default Picture folder on the phone below the given folderName and
     * fileName. <br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param folderName the folder name
     * @param fileName the file name
     * @param listener the listener
     */
    public void saveToPictures(final String folderName, final String fileName, final OnPictureSavedListener listener) {
        new SaveTask(folderName, fileName, listener).execute();
    }

    /**
     * Save current image with applied filter to Pictures. It will be stored on
     * the default Picture folder on the phone below the given folderName and
     * fileName. <br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param folderName the folder name
     * @param fileName   the file name
     * @param width      requested output width
     * @param height     requested output height
     * @param listener   the listener
     */
    public void saveToPictures(final String folderName, final String fileName, int width, int height, final OnPictureSavedListener listener) {
        new SaveTask(folderName, fileName, width, height, listener).execute();
    }

    /**
     * Retrieve current image with filter applied and given size as Bitmap.
     *
     * @param width  requested Bitmap width
     * @param height requested Bitmap height
     * @return Bitmap of picture with given size
     * @throws InterruptedException
     */
    public Bitmap capture(final int width, final int height) throws InterruptedException {
        // This method needs to run on a background thread because it will take a longer time
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Do not call this method from the UI thread");
        }

        mForceSize = new Size(width, height);
        // 信号量
        // 信号量维护一个许可集
        // 通过acquire()和release()获取和释放访问许可
        // 如果当前资源计数等于0, 那么信号量处于未触发状态
        final Semaphore waiter = new Semaphore(0);

        // Layout with new size
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                // 释放许可
                waiter.release();
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                // show loading
                addView(new com.xiaobukuaipao.youngmam.view.LoadingView(getContext()));

                mGLSurfaceView.requestLayout();
            }
        });

        // 获取许可
        waiter.acquire();

        // Run one render pass
        mGPUImage.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                waiter.release();
            }
        });

        requestRender();

        waiter.acquire();

        Bitmap bitmap = capture();

        mForceSize = null;

        post(new Runnable() {
            @Override
            public void run() {
                mGLSurfaceView.requestLayout();
            }
        });

        requestRender();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                // Remove Loading View
                removeViewAt(1);
            }
        }, 300);

        return bitmap;
    }

    /**
     * Capture the current image with the size as it is displayed and retrieve it as Bitmap.
     * @return current output as Bitmap
     * @throws InterruptedException
     */
    public Bitmap capture() throws InterruptedException {
        final Semaphore waiter = new Semaphore(0);

        final int width = mGLSurfaceView.getMeasuredWidth();
        final int height = mGLSurfaceView.getMeasuredHeight();

        Log.d(TAG, "GLSurfaceView width : " + width);
        Log.d(TAG, "GLSurfaceView height : " + height);

        // Take Picture On OpenGL Thread
        final int[] pixelMirroredArray = new int[width * height];

        mGPUImage.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                final IntBuffer pixelBuffer = IntBuffer.allocate(width * height);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
                int[] pixelArray = pixelBuffer.array();

                // Convert upside down mirror-reversed image to right-side up normal image.
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i * width + j];
                    }
                }

                waiter.release();
            }
        });

        requestRender();

        waiter.acquire();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray));

        return bitmap;
    }

    /**
     * Pauses the GLSurfaceView.
     */
    public void onPause() {
        mGLSurfaceView.onPause();
    }

    /**
     * Resumes the GLSurfaceView.
     */
    public void onResume() {
        mGLSurfaceView.onResume();
    }

    /**
     * 大小
     */
    public static class Size {
        int width;
        int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 扩展的GLSurfaceView
     */
    private class GPUImageGLSurfaceView extends GLSurfaceView {

        public GPUImageGLSurfaceView(Context context) {
            super(context);
        }

        public GPUImageGLSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (mForceSize != null) {
                // 此时, 指定大小
                super.onMeasure(MeasureSpec.makeMeasureSpec(mForceSize.width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(mForceSize.height, MeasureSpec.EXACTLY));
            } else {
                // 此时, 不指定大小
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private class LoadingView extends FrameLayout {
        public LoadingView(Context context) {
            super(context);
            init();
        }

        public LoadingView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public LoadingView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            ProgressBar view = new ProgressBar(getContext());
            view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            addView(view);
            // 设置背景颜色--黑色
            setBackgroundColor(Color.BLACK);
        }
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private final String mFolderName;
        private final String mFileName;
        private final int mWidth;
        private final int mHeight;
        private final OnPictureSavedListener mListener;
        private final Handler mHandler;

        public SaveTask(final String folderName, final String fileName, final OnPictureSavedListener listener) {
            this(folderName, fileName, 0, 0, listener);
        }

        public SaveTask(final String folderName, final String fileName, int width, int height, final OnPictureSavedListener listener) {
            mFolderName = folderName;
            mFileName = fileName;
            mWidth = width;
            mHeight = height;
            mListener = listener;
            mHandler = new Handler();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {
                Bitmap result = mWidth != 0 ? capture(mWidth, mHeight) : capture();
                saveImage(mFolderName, mFileName, result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void saveImage(final String folderName, final String fileName, final Bitmap image) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, folderName + "/" + fileName);

            try {
                file.getParentFile().mkdirs();
                image.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));

                MediaScannerConnection.scanFile(getContext(), new String[] { file.toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(final String path, final Uri uri) {
                        if (mListener != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mListener.onPictureSaved(uri);
                                }
                            });
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 图片保存接口
     */
    public interface OnPictureSavedListener {
        void onPictureSaved(Uri uri);
    }

    public void deleteImage() {
        mGPUImage.deleteImage();
    }
}
