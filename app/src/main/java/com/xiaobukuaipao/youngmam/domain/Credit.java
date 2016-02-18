package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-7-6.
 */
public class Credit {
    private int done;
    private int limit;
    private String name;
    private int point;
    private int type;

    public Credit(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_DONE)) {
            done = jsonObject.getInteger(GlobalConstants.JSON_DONE);
        }
        if (jsonObject.containsKey(GlobalConstants.JSON_LIMIT)) {
            limit = jsonObject.getInteger(GlobalConstants.JSON_LIMIT);
        }
        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
            name = jsonObject.getString(GlobalConstants.JSON_NAME);
        }
        if (jsonObject.containsKey(GlobalConstants.JSON_POINT)) {
            point = jsonObject.getInteger(GlobalConstants.JSON_POINT);
        }
        if (jsonObject.containsKey(GlobalConstants.JSON_TYPE)) {
            type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);
        }
    }

    public void setDone(int done) {
        this.done = done;
    }
    public int getDone() {
        return this.done;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    public int getLimit() {
        return this.limit;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setPoint(int point) {
        this.point = point;
    }
    public int getPoint() {
        return this.point;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }
}
