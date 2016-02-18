package com.xiaobukuaipao.youngmam.fragment;

/**
 * Created by xiaobu1 on 15-4-17.
 */
public interface ViewPagerDialogListener {
    /**
     * 操作完成
     */
    void onDone(int currentPosition);

    /**
     * 操作取消
     */
    void onDismiss();
}

