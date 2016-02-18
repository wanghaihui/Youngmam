package com.xiaobukuaipao.youngmam.fragment;

/**
 * Created by xiaobu1 on 15-5-15.
 */
public interface UiInterface {
    /**
     * 显示Toast
     * @param message
     */
    void showToast(CharSequence message);

    /**
     * 显示进度条, 可取消
     * @param message
     */
    void showProgress(String message);

    /**
     * 显示并设置进度条是否可取消
     * @param message
     * @param cancelable
     */
    void showProgress(String message, boolean cancelable);

    /**
     * 隐藏进度条
     */
    void hideProgress();
}
