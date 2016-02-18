package com.xiaobukuaipao.youngmam.imagechooser;

import android.os.AsyncTask;

/**
 * Created by xiaobu1 on 15-4-13.
 */

/**
 * 异步抽象加载基类
 */
public abstract class BaseImageLoaderTask extends AsyncTask<Void, Void, Boolean> {
    /**
     * 失败的时候错误提示
     */
    protected String error = "";

    /**
     * 是否被终止
     */
    protected boolean interrupt = false;

    /**
     * 结果
     */
    protected Object result = null;

    /**
     * 任务执行完后的回调接口
     */
    protected OnTaskResultListener resultListener = null;

    public void setOnTaskResultListener(OnTaskResultListener resultListener) {
        this.resultListener = resultListener;
    }

    /**
     * 中断异步任务
     */
    public void cancle() {
        super.cancel(true);
        interrupt = true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (!interrupt && resultListener != null) {
            resultListener.onResult(success, error, result);
        }
    }

}
