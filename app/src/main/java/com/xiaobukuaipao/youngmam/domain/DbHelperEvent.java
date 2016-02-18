package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-6-2.
 */
public class DbHelperEvent {

    private boolean relogin;

    public DbHelperEvent(boolean relogin) {
        this.relogin = relogin;
    }

    public void setRelogin(boolean relogin) {
        this.relogin = relogin;
    }
    public boolean getRelogin() {
        return this.relogin;
    }
}
