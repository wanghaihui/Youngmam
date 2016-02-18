package com.xiaobukuaipao.youngmam.imagechooser;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by xiaobu1 on 15-4-14.
 */
public class TaskExecuteUtil {

    private TaskExecuteUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 执行异步任务,android 2.3及以下使用execute()方法, android 3.0 及以上使用executeOnExecutor方法
     * @param task
     * @param params
     * @param <Params>
     * @param <Progress>
     * @param <Result>
     */
    public static <Params, Progress, Result> void execute(AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
}
