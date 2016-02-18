package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-17.
 */
public class FollowEvent {
    public static final int TYPE_ADD_FOLLOW = 1;
    public static final int TYPE_DELETE_FOLLOW = 2;

    protected String userId;
    protected int type;

    public FollowEvent(String userId, int type) {
        this.userId = userId;
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return this.userId;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }
}
