package com.xiaobukuaipao.youngmam.annotation;

import android.app.Activity;
import android.view.View;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class ViewFinder {
    private View view;
    private Activity activity;

    public ViewFinder(Activity activity) {
        this.activity = activity;
    }

    public ViewFinder(View view) {
        this.view = view;
    }

    /**
     * 根据id查找view
     * @param id
     */
    public View findViewById(int id) {
        return activity != null ? activity.findViewById(id) : view.findViewById(id);
    }
}
