package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-5-26.
 */
public class NotifyEvent {

    private boolean notify;

    public NotifyEvent(boolean notify) {
        this.notify = notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
    public boolean getNotify() {
        return this.notify;
    }

}
