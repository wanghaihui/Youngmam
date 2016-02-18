package com.xiaobukuaipao.youngmam.filter.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by xiaobu1 on 15-9-10.
 */
public class IOUtil {

    /**
     * 关闭流
     */
    public static void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                // 关闭流
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
