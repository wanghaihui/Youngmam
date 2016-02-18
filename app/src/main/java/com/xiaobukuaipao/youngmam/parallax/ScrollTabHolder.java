package com.xiaobukuaipao.youngmam.parallax;

import android.widget.AbsListView;

/**
 * Created by xiaobu1 on 15-4-29.
 */
public interface ScrollTabHolder {
    /**
     * 调整滚动的高度
     * @param scrollHeight
     */
    void adjustScroll(int scrollHeight);

    /**
     * 滚动
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     * @param pagePosition
     */
    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);
}
