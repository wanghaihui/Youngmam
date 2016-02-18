package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-29.
 */
public class MineQuestionDelayRefreshEvent {
    private boolean delayRefresh;

    public MineQuestionDelayRefreshEvent(boolean delayRefresh) {
        this.delayRefresh = delayRefresh;
    }

    public void setDelayRefresh(boolean delayRefresh) {
        this.delayRefresh = delayRefresh;
    }
    public boolean getDelayRefresh() {
        return this.delayRefresh;
    }
}
