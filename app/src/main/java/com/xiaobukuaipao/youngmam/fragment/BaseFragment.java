package com.xiaobukuaipao.youngmam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by xiaobu1 on 15-4-23.
 */
public abstract  class BaseFragment extends Fragment {

    protected View view;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            onRestoreState(savedInstanceState);
        }
        // 初始化数据和UI元素
        initUIAndData();
    }

    protected abstract void initUIAndData();

    /**
     * 恢复临时状态
     */
    protected void onRestoreState(Bundle savedState) {

    }
}
