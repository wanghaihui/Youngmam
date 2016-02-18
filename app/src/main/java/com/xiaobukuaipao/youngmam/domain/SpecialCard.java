package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-7-8.
 */
public class SpecialCard {

    private String headImg;
    private int headImgHeight;
    private int headImgWidth;

    private JSONArray items;

    private String tag;

    public SpecialCard(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_HEADIMG)) {
            headImg = jsonObject.getString(GlobalConstants.JSON_HEADIMG);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_HEADIMGHEIGHT)) {
            headImgHeight = jsonObject.getInteger(GlobalConstants.JSON_HEADIMGHEIGHT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_HEADIMGWIDTH)) {
            headImgWidth = jsonObject.getInteger(GlobalConstants.JSON_HEADIMGWIDTH);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ITEMS)) {
            items = jsonObject.getJSONArray(GlobalConstants.JSON_ITEMS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TAG)) {
            tag = jsonObject.getString(GlobalConstants.JSON_TAG);
        }
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
    public String getHeadImg() {
        return this.headImg;
    }

    public void setHeadImgHeight(int headImgHeight) {
        this.headImgHeight = headImgHeight;
    }
    public int getHeadImgHeight() {
        return this.headImgHeight;
    }

    public void setHeadImgWidth(int headImgWidth) {
        this.headImgWidth = headImgWidth;
    }
    public int getHeadImgWidth() {
        return this.headImgWidth;
    }

    public void setItems(JSONArray items) {
        this.items = items;
    }
    public JSONArray getItems() {
        return this.items;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return this.tag;
    }
}
