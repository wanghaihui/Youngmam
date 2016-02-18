package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-5-19.
 * 亲子状态
 */
public class ChildStatus {
    private int id;
    private String name;
    private boolean checked;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public boolean getChecked() {
        return this.checked;
    }
}
