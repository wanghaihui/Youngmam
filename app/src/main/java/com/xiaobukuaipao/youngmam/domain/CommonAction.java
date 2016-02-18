package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-11-3.
 * 通用操作, 比如保存, 删除, 复制等
 */
public class CommonAction {
    public static final int ACTION_SAVE = 1;

    private int actionId;
    private String actionName;

    public CommonAction(int actionId, String actionName) {
        this.actionId = actionId;
        this.actionName = actionName;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }
    public int getActionId() {
        return this.actionId;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
    public String getActionName() {
        return this.actionName;
    }
}
