package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-6-13.
 */
public class NotifyIndicatorEvent {

    private boolean indicator;

    public NotifyIndicatorEvent(boolean indicator) {
        this.indicator = indicator;
    }

    public void setIndicator(boolean indicator) {
        this.indicator = indicator;
    }
    public boolean getIndicator() {
        return this.indicator;
    }

}
