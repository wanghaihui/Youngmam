package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-7-3.
 */
public class Tag {
    private String tagId;
    private String name;
    private String imgUrl;
    private String desc;
    private String posterUrl;
    private int type;

    public Tag(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_TAGID)) {
            tagId = jsonObject.getString(GlobalConstants.JSON_TAGID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
            name = jsonObject.getString(GlobalConstants.JSON_NAME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGURL)) {
            imgUrl = jsonObject.getString(GlobalConstants.JSON_IMGURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_DESC)) {
            desc = jsonObject.getString(GlobalConstants.JSON_DESC);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_POSTERURL)) {
            posterUrl = jsonObject.getString(GlobalConstants.JSON_POSTERURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TYPE)) {
            type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);
        }
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
    public String getTagId() {
        return this.tagId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return this.desc;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    public String getPosterUrl() {
        return this.posterUrl;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }
}
