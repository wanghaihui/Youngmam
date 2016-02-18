package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-10-7.
 */
public class StickerGroup {

    private String groupId;
    private String groupName;
    private int type;

    private boolean selected;

    public StickerGroup() {
        groupId = null;
        groupName = null;
        type = 0;
        selected = false;
    }

    public StickerGroup(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_GROUPID)) {
            groupId = jsonObject.getString(GlobalConstants.JSON_GROUPID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_GROUPNAME)) {
            groupName = jsonObject.getString(GlobalConstants.JSON_GROUPNAME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TYPE)) {
            type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);
        }
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getGroupName() {
        return this.groupName;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected() {
        return this.selected;
    }
}
