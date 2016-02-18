package com.xiaobukuaipao.youngmam.notification;

/**
 * Created by xiaobu1 on 15-5-25.
 */
public class BaiduPushEvent {

    private String userId;
    private String channelId;

    public BaiduPushEvent(String userId, String channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return this.userId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    public String getChannelId() {
        return this.channelId;
    }

}
