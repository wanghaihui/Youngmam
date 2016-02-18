package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-6-10.
 */
public class ActivityParticipateEvent {

    private boolean participate;

    public ActivityParticipateEvent(boolean participate) {
        this.participate = participate;
    }

    public void setParticipate(boolean participate) {
        this.participate = participate;
    }
    public boolean getParticipate() {
        return this.participate;
    }

}
