package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-6-3.
 */
public class AvatarModifyEvent {
    private boolean modify;

    public AvatarModifyEvent(boolean modify) {
        this.modify = modify;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }
    public boolean getModify() {
        return this.modify;
    }
}
