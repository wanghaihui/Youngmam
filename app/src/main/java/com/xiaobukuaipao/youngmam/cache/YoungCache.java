package com.xiaobukuaipao.youngmam.cache;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xiaobu1 on 15-5-22.
 */
public class YoungCache {

    public static final String CACHE_MINE_PUBLISH = "cache_mine_publish";
    public static final String CACHE_MINE_LIKE = "cache_mine_like";
    public static final String CACHE_MINE_COMMENT = "cache_mine_comment";
    public static final String CACHE_MINE_NOTIFY = "cache_mine_notify";

    public static final int TIME_HOUR = 60 * 60;
    public static final int TIME_DAY = TIME_HOUR * 24;

    // 50M
    private static final int MAX_SIZE = 1000 * 1000 * 50;
    // 不限制存放数据的数量
    private static final int MAX_COUNT = Integer.MAX_VALUE;

    private static Map<String, YoungCache> mInstanceMap = new HashMap<String, YoungCache>();

    private YoungCacheManager mCache;

    public static YoungCache get(Context context) {
        return get(context, "YoungCache");
    }

    public static YoungCache get(Context context, String cacheName) {
        File file = new File(context.getCacheDir(), cacheName);
        return get(file, MAX_SIZE, MAX_COUNT);
    }

    public static YoungCache get(File cacheDir) {
        return get(cacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static YoungCache get(File cacheDir, long maxSize, int maxCount) {
        YoungCache manager = mInstanceMap.get(cacheDir.getAbsolutePath() + myPid());
        if (manager == null) {
            manager = new YoungCache(cacheDir, maxSize, maxCount);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    private YoungCache(File cacheDir, long maxSize, int maxCount) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        }

        mCache = new YoungCacheManager(cacheDir, maxSize, maxCount);
    }

    /**
     * Sring数据读写到缓存中
     */
    public void put(String key, String value) {
        File file = mCache.newFile(key);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCache.put(file);
        }
    }

    public void put(String key, String value, int saveTime) {
        put(key, CacheUtils.newStringWithDateInfo(saveTime, value));
    }

    /**
     * 读取String数据
     */
    public String getAsString(String key) {
        File file = mCache.get(key);

        if (!file.exists()) {
            return null;
        }

        boolean removeFile = false;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(file));
            String readString = "";
            String currentLine;

            while ((currentLine = in.readLine()) != null) {
                readString += currentLine;
            }

            if (!CacheUtils.isDue(readString)) {
                return CacheUtils.clearDateInfo(readString);
            } else {
                removeFile = true;
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (removeFile) {
                remove(key);
            }
        }
    }

    /**
     * 移除某个key
     *
     * @param key
     * @return 是否移除成功
     */
    public boolean remove(String key) {
        return mCache.remove(key);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        mCache.clear();
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 缓存管理器
     */
    public class YoungCacheManager {
        // 缓存大小
        // AtomicLong已经是非常好的解决方案了，涉及并发的地方都是使用CAS操作，在硬件层次上去做 compare and set操作。效率非常高
        // AtomicLong的实现方式是内部有个value 变量，当多线程并发自增，自减时，均通过cas 指令从机器指令级别操作保证并发的原子性
        // AtomicLong已经使用CAS指令，非常高效了（比起各种锁）
        private final AtomicLong cacheSize;

        // 缓存的数量
        private final AtomicInteger cacheCount;

        // 大小限制
        private final long sizeLimit;

        // 数量限制
        private final int countLimit;

        // Collections.synchronizedMap()的方法包装一个thread-safe的Map
        private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());

        protected File cacheDir;

        private YoungCacheManager(File cacheDir, long sizeLimit, int countLimit) {
            this.cacheDir = cacheDir;
            this.sizeLimit = sizeLimit;
            this.countLimit = countLimit;

            cacheSize = new AtomicLong();
            cacheCount = new AtomicInteger();

            calculateCacheSizeAndCacheCount();
        }

        /**
         * 计算缓存大小和缓存文件
         */
        private void calculateCacheSizeAndCacheCount() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int size = 0;
                    int count = 0;

                    File[] cachedFiles = cacheDir.listFiles();
                    if (cachedFiles != null) {
                        for (File cachedFile : cachedFiles) {
                            size += calculateSize(cachedFile);
                            count += 1;
                            // 上次使用的时间
                            lastUsageDates.put(cachedFile, cachedFile.lastModified());
                        }

                        cacheSize.set(size);
                        cacheCount.set(count);
                    }
                }
            }).start();
        }

        private void put(File file) {
            int curCacheCount = cacheCount.get();
            while (curCacheCount + 1 > countLimit) {
                long freedSize = removeNext();
                cacheSize.addAndGet(-freedSize);
                curCacheCount = cacheCount.addAndGet(-1);
            }
            cacheCount.addAndGet(1);

            long valueSize = calculateSize(file);
            long curCacheSize = cacheSize.get();

            while (curCacheSize + valueSize > sizeLimit) {
                long freedSize = removeNext();
                curCacheSize = cacheSize.addAndGet(-freedSize);
            }
            cacheSize.addAndGet(valueSize);

            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);
        }

        private File get(String key) {
            File file = newFile(key);
            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);

            return file;
        }

        private File newFile(String key) {
            return new File(cacheDir, key.hashCode() + "");
        }

        private boolean remove(String key) {
            File image = get(key);
            return image.delete();
        }

        private void clear() {
            lastUsageDates.clear();
            cacheSize.set(0);
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        /**
         * 移除旧的文件
         */
        private long removeNext() {
            if (lastUsageDates.isEmpty()) {
                return 0;
            }

            Long oldestUsage = null;
            File mostLongUsedFile = null;

            Set<Entry<File, Long>> entries = lastUsageDates.entrySet();

            synchronized (lastUsageDates) {
                for (Entry<File, Long> entry : entries) {
                    if (mostLongUsedFile == null) {
                        mostLongUsedFile = entry.getKey();
                        oldestUsage = entry.getValue();
                    } else {
                        Long lastValueUsage = entry.getValue();
                        if (lastValueUsage < oldestUsage) {
                            oldestUsage = lastValueUsage;
                            mostLongUsedFile = entry.getKey();
                        }
                    }
                }
            }

            long fileSize = calculateSize(mostLongUsedFile);

            if (mostLongUsedFile.delete()) {
                lastUsageDates.remove(mostLongUsedFile);
            }

            return fileSize;
        }

        private long calculateSize(File file) {
            return file.length();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    /**
     * 时间计算工具类
     */
    private static class CacheUtils {
        /**
         * 判断缓存的String数据是否到期
         *
         * @param str
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(String str) {
            return isDue(str.getBytes());
        }

        /**
         * 判断缓存的byte数据是否到期
         *
         * @param data
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr
                            .substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }


        private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        private static final char mSeparator = ' ';

        private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == '-'
                    && indexOf(data, mSeparator) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14,
                        indexOf(data, mSeparator)));
                return new String[] { saveDate, deleteAfter };
            }
            return null;
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0)
                throw new IllegalArgumentException(from + " > " + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0,
                    Math.min(original.length - from, newLength));
            return copy;
        }

        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1,
                        strInfo.length());
            }
            return strInfo;
        }

        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1,
                        data.length);
            }
            return data;
        }
    }

}
