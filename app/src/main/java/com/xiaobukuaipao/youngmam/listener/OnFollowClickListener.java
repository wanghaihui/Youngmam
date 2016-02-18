package com.xiaobukuaipao.youngmam.listener;

/**
 * Created by xiaobu1 on 15-10-17.
 */
public interface OnFollowClickListener {
    public static final int TYPE_FOLLOW_ADD = 0;
    public static final int TYPE_FOLLOW_DELETE = 1;

    public void onFollowClick(String toId, int type);
}
