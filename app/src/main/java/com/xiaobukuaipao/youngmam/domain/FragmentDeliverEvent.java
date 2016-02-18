package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-5-26.
 */
public class FragmentDeliverEvent {

    public static final int USER_STATIS = 0;
    public static final int RED_INDICATOR = 1;

    private int indicate;

    public FragmentDeliverEvent(int indicate) {
        this.indicate = indicate;
    }

    public void setIndicate(int indicate) {
        this.indicate = indicate;
    }
    public int getIndicate() {
        return this.indicate;
    }
}
