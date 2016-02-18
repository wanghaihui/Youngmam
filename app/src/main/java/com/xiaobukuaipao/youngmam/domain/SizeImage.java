package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-7-2.
 */
public class SizeImage {
    private String imgId;
    private int imgWidth;
    private int imgHeight;
    private String url;

    public SizeImage(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_IMGID)) {
            imgId = jsonObject.getString(GlobalConstants.JSON_IMGID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGWIDTH)) {
            imgWidth = jsonObject.getInteger(GlobalConstants.JSON_IMGWIDTH);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGHEIGHT)) {
            imgHeight = jsonObject.getInteger(GlobalConstants.JSON_IMGHEIGHT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_URL)) {
            url = jsonObject.getString(GlobalConstants.JSON_URL);
        }
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }
    public String getImgId() {
        return this.imgId;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }
    public int getImgWidth() {
        return this.imgWidth;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }
    public int getImgHeight() {
        return this.imgHeight;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return this.url;
    }
}
