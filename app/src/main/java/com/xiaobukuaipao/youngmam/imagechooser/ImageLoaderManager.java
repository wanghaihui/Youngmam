package com.xiaobukuaipao.youngmam.imagechooser;

/**
 * Created by xiaobu1 on 15-4-14.
 */

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.xiaobukuaipao.youngmam.cache.DiskLruCache;
import com.xiaobukuaipao.youngmam.utils.SDCardUtil;
import com.xiaobukuaipao.youngmam.utils.StringUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * LruCache和DiskLruCache双缓存
 * 当需要加载一张图片，首先把加载图片加入任务队列，然后使用loop线程（子线程）中的hander发送一个消息
 * 提示有任务到达，loop()（子线程）中会接着取出一个任务，去加载图片，当图片加载完成，会使用UI线程的handler发送一个消息去更新UI界面
 */

/**
 * 待完善--双缓存机制--2.15.04.15--目前未使用
 */
public class ImageLoaderManager {

    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private int mCompressQuality = 70;

    /**
     * LruCache--图片内存缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;

    /**
     * DiskLruCache--图片硬盘缓存的核心类
     */
    private DiskLruCache mDiskCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;

    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;

    /**
     * 轮询线程
     */
    private Thread mPoolThread;
    /**
     * 轮询线程对应的Handler
     */
    private Handler mPoolThreadHandler;

    /**
     * 运行在UI线程的Handler,用于给ImageView设置图片
     */
    private Handler mUIHandler;


    // Semaphore -- 内部permits设置为0，也就是说acquire()永远获取不到permit，会一直被阻塞着
    // Java语言包含两种内在的同步机制:同步块(或方法)和volatile变量
    // 防止mPoolThreadHander未初始化完成
    private volatile Semaphore mSemaphore = new Semaphore(0);

    // 线程阻塞Semaphore
    // 由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
    private volatile Semaphore mPoolSemaphore;

    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;

    /**
     * 单例模式
     */
    private static ImageLoaderManager instance;

    /**
     * 队列的调度方式
     */
    public enum Type {
        FIFO, LIFO
    }

    /**
     * 获得该实例
     */
    public static ImageLoaderManager getInstance(int threadCount, Type type, String diskCachePath, int appVersion) {
        if (instance == null) {
            synchronized (ImageLoaderManager.class) {
                if (instance == null) {
                    instance = new ImageLoaderManager(threadCount, type, diskCachePath, appVersion);
                }
            }
        }

        return instance;
    }

    private ImageLoaderManager(int threadCount, Type type, String diskCachePath, int appVersion) {
        init(threadCount, type, diskCachePath, appVersion);
    }

    /**
     * 初始化工作
     */
    private void init(int threadCount, Type type, String diskCachePath, int appVersion) {
        // 首先,初始化后台的轮询线程
        initBackThread();

        // 初始化LruCache
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;

        mLruCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                return value.getRowBytes() * value.getHeight();
            };
        };

        // 初始化硬盘缓存
        try {
            File cacheDir = SDCardUtil.getDiskCacheDir(diskCachePath, "bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            mDiskCache = DiskLruCache.open(cacheDir, appVersion, 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 初始化线程池实例
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        // 线程池信号量
        mPoolSemaphore = new Semaphore(threadCount);
        // 初始化任务队列
        mTasks = new LinkedList<Runnable>();
        // 默认LIFO
        mType = type == null ? Type.LIFO : type;
    }

    /**
     * 初始化后台的轮询线程
     */
    private void initBackThread() {
        // 在一个子线程中维护一个Loop实例,子线程中也就有了MessageQueue, Looper会一直在那Loop停着等待消息的到达
        // 当有消息到达时,从任务队列中按照队列调度的方式(FIFO, LIFO),取出一个任务放入线程池中进行处理
        // 一个最标准的异步消息处理线程
        mPoolThread = new Thread() {
            // 重写run
            @Override
            public void run() {
                Looper.prepare();

                // 子线程中创建Handler
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        // 处理Message
                        mThreadPool.execute(getTask());

                        // 此时,线程池执行了一个任务,所以空出一个线程
                        try {
                            // 获得许可,说明又有一个任务可以获得线程的使用权
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {

                        }
                    }
                };

                mSemaphore.release();
                Looper.loop();
            }
        };

        mPoolThread.start();
    }

    /**
     * 加载图片
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        // 设置Path作为Tag
        imageView.setTag(path);

        if (mUIHandler == null) {

            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 处理Message
                    ImageBean imageBean = (ImageBean) msg.obj;
                    ImageView imgView = imageBean.imageView;
                    Bitmap bmp = imageBean.bitmap;
                    String pth = imageBean.path;

                    if (imgView.getTag().toString().equals(path)) {
                        // 加载图片
                        imgView.setImageBitmap(bmp);
                    }
                }
            };

        }

        // 首先,从内存中查找
        Bitmap bmp = getBitmapFromLruCache(path);

        if (bmp != null) {
            // 此时,从内存中查找到对应的Bitamp
            ImageBean imageBean = new ImageBean();
            imageBean.imageView = imageView;
            imageBean.bitmap = bmp;
            imageBean.path = path;

            Message message = Message.obtain();
            message.obj = imageBean;
            mUIHandler.sendMessage(message);
        } else {

            // 此时,先生成原图对应的缩略图,然后将缩略图加入到内存缓存和硬盘缓存中
            addTask(new Runnable() {
                @Override
                public void run() {
                    ImageSize imageSize = getImageViewWidth(imageView);

                    int reqWidth = imageSize.width;
                    int reqHeight = imageSize.height;

                    Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
                            reqHeight);

                    // 首先添加到内存缓存
                    addBitmapToLruCache(path, bm);
                    // 然后添加到硬盘缓存
                    // addBitmapToDiskLruCache(path, bm);

                    ImageBean imageBean = new ImageBean();
                    imageBean.imageView = imageView;
                    imageBean.bitmap = bm;
                    imageBean.path = path;

                    Message message = Message.obtain();
                    message.obj = imageBean;
                    mUIHandler.sendMessage(message);

                    mPoolSemaphore.release();

                }
            });

            // 继续从硬盘中查找
            /*try {
                String key = StringUtil.hashKeyForDisk(path);
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);

                if (snapshot != null) {
                    // 此时,说明硬盘中存在此数据
                    InputStream is = snapshot.getInputStream(0);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    ImageBean imageBean = new ImageBean();
                    imageBean.imageView = imageView;
                    imageBean.bitmap = bitmap;
                    imageBean.path = path;

                    Message message = Message.obtain();
                    message.obj = imageBean;
                    mUIHandler.sendMessage(message);
                } else {
                    // 此时,先生成原图对应的缩略图,然后将缩略图加入到内存缓存和硬盘缓存中
                    addTask(new Runnable() {
                        @Override
                        public void run() {
                            ImageSize imageSize = getImageViewWidth(imageView);

                            int reqWidth = imageSize.width;
                            int reqHeight = imageSize.height;

                            Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
                                    reqHeight);

                            // 首先添加到内存缓存
                            addBitmapToLruCache(path, bm);
                            // 然后添加到硬盘缓存
                            // addBitmapToDiskLruCache(path, bm);

                            ImageBean imageBean = new ImageBean();
                            imageBean.imageView = imageView;
                            imageBean.bitmap = bm;
                            imageBean.path = path;

                            Message message = Message.obtain();
                            message.obj = imageBean;
                            mUIHandler.sendMessage(message);

                            mPoolSemaphore.release();

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    /**
     * 向硬盘缓存中添加一张图片
     */
    private void addBitmapToDiskLruCache(String path, Bitmap bitmap) {
        try {
            String key = StringUtil.hashKeyForDisk(path);
            DiskLruCache.Snapshot snapshot = mDiskCache.get(key);

            if (snapshot == null) {

                if (bitmap != null) {
                    // 此时,加入到硬盘缓存中
                    DiskLruCache.Editor editor = mDiskCache.edit(key);
                    if (editor != null) {
                        if (writeBitmapToFile(bitmap, editor)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                    mDiskCache.flush();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将Bitmap写入文件
     * @param bitmap
     * @param editor
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    private boolean writeBitmapToFile( Bitmap bitmap, DiskLruCache.Editor editor )
            throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream( 0 ), SDCardUtil.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromLruCache(key) == null)
        {
            if (bitmap != null) {
                mLruCache.put(key, bitmap);
            }
        }
    }

    /**
     * 从LruCache中查找对应的Bitmap
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * 添加一个任务
     */
    private synchronized void addTask(Runnable runnable) {
        try
        {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHandler == null) {
                // 完全阻塞
                mSemaphore.acquire();
            }
        } catch (InterruptedException e) {

        }

        mTasks.add(runnable);

        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }

        return null;
    }

    /**
     * Message的obj
     */
    private class ImageBean {
        ImageView imageView;
        Bitmap bitmap;
        String path;
    }

    /**
     * Image的大小
     */
    private class ImageSize {
        int width;
        int height;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // 图片处理部分
    /**
    * 根据ImageView获得适当的压缩的宽和高
    *
    * @param imageView
    * @return
    */
    private ImageSize getImageViewWidth(ImageView imageView)
    {
        ImageSize imageSize = new ImageSize();
        final DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        final LayoutParams params = imageView.getLayoutParams();

        int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getWidth(); // Get actual image width
        if (width <= 0)
            width = params.width; // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
        // maxWidth
        // parameter
        if (width <= 0)
            width = displayMetrics.widthPixels;
        int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0)
            height = params.height; // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels;
        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName)
    {
        int value = 0;
        try
        {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
            {
                value = fieldValue;

                Log.e("TAG", value + "");
            }
        } catch (Exception e)
        {
        }
        return value;
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight)
    {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        return bitmap;
    }

    /**
     * 计算inSampleSize，用于压缩图片
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight)
    {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight)
        {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }
}
