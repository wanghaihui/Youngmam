package com.xiaobukuaipao.youngmam.imagechooser;

/**
 * Created by xiaobu1 on 15-4-13.
 */

/**
 * 任务完成后的回调接口
 */
public interface OnTaskResultListener {

    /**
     * 返回执行结果
     * @param success
     * @param error
     * @param result
     */
    void onResult(final boolean success, final String error, final Object result);

}
