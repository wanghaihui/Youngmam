package com.xiaobukuaipao.youngmam.gpuimage;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 渲染器--3.0以上
 */
@TargetApi(11)
public class GPUImageRenderer implements Renderer, Camera.PreviewCallback {
    private static final String TAG = GPUImageRenderer.class.getSimpleName();

    // 没有图片
    public static final int NO_IMAGE = -1;

    // 立方形--cube--基本坐标
    // Opengl ES 主要为了2D贴图, 所以此时不涉及Z轴
    // 由于Opengl ES本身就是opengl的缩略版, 所以能直接画的形状就只有三角形, 别的复杂的都要由三角形来组成
    // GLES20.GL_TRIANGLE_STRIP指的就是一种三角形的绘制模式, 对应这个顶点数组
    static final float CUBE[] = {
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    };

    private GPUImageFilter mFilter;

    /**
     * SurfaceChange Waiter
     */
    public final Object mSurfaceChangedWaiter = new Object();

    // 材质id
    private int mGLTextureId = NO_IMAGE;

    // Captures frames from an image stream as an OpenGL ES texture
    private SurfaceTexture mSurfaceTexture = null;

    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;

    private IntBuffer mGLRgbBuffer;

    private int mOutputWidth;
    private int mOutputHeight;

    private int mImageWidth;
    private int mImageHeight;

    private int mAddedPadding;

    /**
     * 正在绘制的
     */
    private final Queue<Runnable> mRunOnDraw;
    private final Queue<Runnable> mRunOnDrawEnd;

    // 图片的选择角度
    private Rotation mRotation;
    // 是否横翻
    private boolean mFlipHorizontal;
    // 是否竖翻
    private boolean mFlipVertical;

    private GPUImage.ScaleType mScaleType = GPUImage.ScaleType.CENTER_CROP;

    public GPUImageRenderer(final GPUImageFilter filter) {
        mFilter = filter;
        mRunOnDraw = new LinkedList<Runnable>();
        mRunOnDrawEnd = new LinkedList<Runnable>();

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        // 黑色背景--设置默认颜色--设置屏幕背景色RGBA
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // 关闭深度检测
        // 深度--其实就是该像素点在3d世界中距离摄象机的距离
        // 深度缓存--存储着每个像素点(绘制在屏幕上的)的深度值
        // 深度测试--决定了是否绘制较远的像素点(或较近的像素点), 通常选用较近的, 而较远优先能实现透视的效果
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // 过滤器初始化
        mFilter.init();
    }

    /**
     * Surface改变的时候调用
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(final GL10 gl, int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;

        Log.d(TAG, "onSurfaceChanged : " + width);
        Log.d(TAG, "onSurfaceChanged : " + height);
        GLES20.glViewport(0, 0, width, height);

        GLES20.glUseProgram(mFilter.getProgram());

        mFilter.onOutputSizeChanged(width, height);

        adjustImageScaling();

        synchronized (mSurfaceChangedWaiter) {
            // 唤醒所有的
            mSurfaceChangedWaiter.notifyAll();
        }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        // 设置Mask--颜色Buffer和深度Buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // GLSurfaceView重新渲染的时候会调用 -- 此时是赋值过程
        runAll(mRunOnDraw);

        /**
         * 具体的绘制过程
         */
        mFilter.onDraw(mGLTextureId, mGLCubeBuffer, mGLTextureBuffer);

        // 绘制完之后的处理
        runAll(mRunOnDrawEnd);

        if (mSurfaceTexture != null) {
            // Update the texture image to the most recent frame from the image stream
            mSurfaceTexture.updateTexImage();
        }
    }

    /**
     * 执行队列中所有的任务
     * @param queue
     */
    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while(!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        final Size previewSize = camera.getParameters().getPreviewSize();
        if (mGLRgbBuffer == null) {
            mGLRgbBuffer = IntBuffer.allocate(previewSize.width * previewSize.height);
        }

        /*if (mRunOnDraw.isEmpty()) {
            runOnDraw(new Runnable() {
                @Override
                public void run() {
                    GPUImageNativeLibrary.YUVtoRBGA(data, previewSize.width, previewSize.height, mGLRgbBuffer.array());
                    mGLTextureId = OpenGlUtils.loadTexture(mGLRgbBuffer, previewSize, mGLTextureId);
                    camera.addCallbackBuffer(data);

                    if (mImageWidth != previewSize.width) {
                        mImageWidth = previewSize.width;
                        mImageHeight = previewSize.height;
                        adjustImageScaling();
                    }
                }
            });
        }*/
    }

    /**
     * 设置Surface纹理
     */
    public void setUpSurfaceTexture(final Camera camera) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                int[] textures = new int[1];
                GLES20.glGenTextures(1, textures, 0);
                mSurfaceTexture = new SurfaceTexture(textures[0]);
                try {
                    camera.setPreviewTexture(mSurfaceTexture);
                    camera.setPreviewCallback(GPUImageRenderer.this);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置过滤器
     */
    public void setFilter(final GPUImageFilter filter) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                final GPUImageFilter oldFilter = mFilter;
                mFilter = filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }

                // 滤镜初始化
                if (filter != null) {
                    mFilter.init();
                    GLES20.glUseProgram(mFilter.getProgram());
                    mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
                }
            }
        });
    }

    /**
     * 删除Image
     */
    public void deleteImage() {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                // 删除纹理--删除数组中的offset开始之后的n个纹理
                GLES20.glDeleteTextures(1, new int[]{ mGLTextureId }, 0);
                mGLTextureId = NO_IMAGE;
            }
        });
    }

    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, true);
    }

    /**
     * 设置Bitmap式的Image
     * @param bitmap
     * @param recycle
     */
    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null) {
            return;
        }

        runOnDraw(new Runnable() {
            @Override
            public void run() {
                // 调整大小后的Bitmap
                Bitmap resizedBitmap = null;

                if (bitmap.getWidth() % 2 == 1) {
                    // 此时,宽度为奇数
                    resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 1, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(resizedBitmap);
                    canvas.drawARGB(0x00, 0x00, 0x00, 0x00);
                    canvas.drawBitmap(bitmap, 0, 0, null);

                    mAddedPadding = 1;
                } else {
                    mAddedPadding = 0;
                }

                // 材质id
                mGLTextureId = OpenGlUtils.loadTexture(resizedBitmap != null ? resizedBitmap : bitmap, mGLTextureId, recycle);

                if (resizedBitmap != null) {
                    resizedBitmap.recycle();
                }

                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();

                // 调整图片的大小
                adjustImageScaling();

            }
        });
    }

    public void setScaleType(GPUImage.ScaleType scaleType) {
        mScaleType = scaleType;
    }

    protected int getFrameWidth() {
        return mOutputWidth;
    }

    protected int getFrameHeight() {
        return mOutputHeight;
    }

    /**
     * 调整图片Image的大小--真正的核心代码--准备绘制的数据
     */
    private void adjustImageScaling() {
        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;

        if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
            outputWidth = mOutputHeight;
            outputHeight = mOutputWidth;
        }

        float ratio1 = outputWidth / mImageWidth;
        float ratio2 = outputHeight / mImageHeight;

        // 取最大的比率
        float ratioMax = Math.max(ratio1, ratio2);

        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        /**
         * 默认的世界坐标
         */
        float[] cube = CUBE;

        /**
         * 得到纹理坐标
         */
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, mFlipHorizontal, mFlipVertical);

        // 图形学知识
        // CENTER_CROP--均衡的缩放图像--保持图像原始比例--Center Crop
        if (mScaleType == GPUImage.ScaleType.CENTER_CROP) {
            // 数学知识
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;

            textureCords = new float[] {
                addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical)
            };
        } else {

            cube = new float[] {
                CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                CUBE[6] / ratioHeight, CUBE[7] / ratioWidth
            };
        }

        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);

        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public void setRotationCamera(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        setRotation(rotation, flipHorizontal, flipVertical);
    }

    /**
     * 设置旋转角度
     * @param rotation
     */
    public void setRotation(final Rotation rotation) {
        mRotation = rotation;
        // 调整图片的Scale或者Rotation
        adjustImageScaling();
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        // 横向翻转
        mFlipHorizontal = flipHorizontal;
        // 竖向翻转
        mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    public Rotation getRotation() {
        return mRotation;
    }

    public boolean isFlippedHorizontally() {
        return mFlipHorizontal;
    }

    public boolean isFlippedVertically() {
        return mFlipVertical;
    }

    protected void runOnDraw(final Runnable runnable) {
        // 线程操作--做同步处理
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }

    protected void runOnDrawEnd(final Runnable runnable) {
        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.add(runnable);
        }
    }
}
