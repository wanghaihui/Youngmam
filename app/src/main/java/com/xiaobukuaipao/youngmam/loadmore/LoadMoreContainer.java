package com.xiaobukuaipao.youngmam.loadmore;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by xiaobu1 on 15-5-15.
 */
public interface LoadMoreContainer {

    public void setShowLoadingForFirstPage(boolean showLoading);

    public void setAutoLoadMore(boolean autoLoadMore);

    public void setOnScrollListener(AbsListView.OnScrollListener l);

    public void setLoadMoreView(View view);

    public void setLoadMoreUIHandler(LoadMoreUIHandler handler);

    public void setLoadMoreHandler(LoadMoreHandler handler);

    /**
     * When data has loaded
     */
    public void loadMoreFinish(boolean emptyResult, boolean hasMore);

    /**
     * When something unexpected happened while loading the data
     */
    public void loadMoreError(int errorCode, String errorMessage);
}
