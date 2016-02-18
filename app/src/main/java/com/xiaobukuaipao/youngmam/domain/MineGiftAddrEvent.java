package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-7-17.
 */
public class MineGiftAddrEvent {

    private boolean mineGiftAddr;

    public MineGiftAddrEvent(boolean mineGiftAddr) {
        this.mineGiftAddr = mineGiftAddr;
    }

    public void setMineGiftAddr(boolean mineGiftAddr) {
        this.mineGiftAddr = mineGiftAddr;
    }
    public boolean getMineGiftAddr() {
        return this.mineGiftAddr;
    }
}
