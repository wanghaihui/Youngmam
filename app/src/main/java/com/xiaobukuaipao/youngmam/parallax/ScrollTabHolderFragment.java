package com.xiaobukuaipao.youngmam.parallax;

import android.widget.AbsListView;

import com.xiaobukuaipao.youngmam.fragment.BaseEventHttpFragment;

/**
 * Created by xiaobu1 on 15-4-29.
 */
public abstract class ScrollTabHolderFragment extends BaseEventHttpFragment implements ScrollTabHolder {

    protected ScrollTabHolder scrollTabHolder;

    public void setScrollTabHolder(ScrollTabHolder scrollTabHolder) {
        this.scrollTabHolder = scrollTabHolder;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        // do nothing

    }
}
