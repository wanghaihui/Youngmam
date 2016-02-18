package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-7-1.
 */
public class Gift {
    private int cost;
    private int count;
    private long createTime;
    private String desc;
    private String imgIds;
    private JSONArray imgs;
    private String itemId;
    private String name;
    private String posterUrl;
    private int privilege;
    private int status;
    private int txCount;

    public Gift(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_COST)) {
            cost = jsonObject.getInteger(GlobalConstants.JSON_COST);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_COUNT)) {
            count = jsonObject.getInteger(GlobalConstants.JSON_COUNT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CREATETIME)) {
            createTime = jsonObject.getLong(GlobalConstants.JSON_CREATETIME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_DESC)) {
            desc = jsonObject.getString(GlobalConstants.JSON_DESC);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGIDS)) {
            imgIds = jsonObject.getString(GlobalConstants.JSON_IMGIDS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGS)) {
            imgs = jsonObject.getJSONArray(GlobalConstants.JSON_IMGS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ITEMID)) {
            itemId = jsonObject.getString(GlobalConstants.JSON_ITEMID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
            name = jsonObject.getString(GlobalConstants.JSON_NAME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_POSTERURL)) {
            posterUrl = jsonObject.getString(GlobalConstants.JSON_POSTERURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_PRIVILEGE)) {
            privilege = jsonObject.getInteger(GlobalConstants.JSON_PRIVILEGE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_STATUS)) {
            status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TXCOUNT)) {
            txCount = jsonObject.getInteger(GlobalConstants.JSON_TXCOUNT);
        }
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    public int getCost() {
        return this.cost;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return this.count;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public long getCreateTime() {
        return this.createTime;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return this.desc;
    }

    public void setImgIds(String imgIds) {
        this.imgIds = imgIds;
    }
    public String getImgIds() {
        return this.imgIds;
    }

    public void setImgs(JSONArray imgs) {
        this.imgs = imgs;
    }
    public JSONArray getImgs() {
        return this.imgs;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return this.itemId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    public String getPosterUrl() {
        return this.posterUrl;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }
    public int getPrivilege() {
        return this.privilege;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return this.status;
    }

    public void setTxCount(int txCount) {
        this.txCount = txCount;
    }
    public int getTxCount() {
        return this.txCount;
    }


}
