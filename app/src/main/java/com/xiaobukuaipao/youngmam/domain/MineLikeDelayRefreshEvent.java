package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-6-12.
 */
public class MineLikeDelayRefreshEvent {
    private boolean delayRefresh;

    public MineLikeDelayRefreshEvent(boolean delayRefresh) {
        this.delayRefresh = delayRefresh;
    }

    public void setDelayRefresh(boolean delayRefresh) {
        this.delayRefresh = delayRefresh;
    }
    public boolean getDelayRefresh() {
        return this.delayRefresh;
    }
}
