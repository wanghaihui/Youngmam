package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-6-23.
 */
public class Expert {
    private int childStatus;
    private String expertDesc;
    private String expertName;
    private int expertType;
    private int flag;

    private boolean hasFollowed;

    private String headUrl;
    private String lastArticleId;

    private String lastImgIds;

    private JSONArray lastImgs;
    private String name;
    private String userId;

    public Expert(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_CHILDSTATUS)) {
            childStatus = jsonObject.getInteger(GlobalConstants.JSON_CHILDSTATUS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_EXPERTDESC)) {
            expertDesc = jsonObject.getString(GlobalConstants.JSON_EXPERTDESC);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_EXPERTNAME)) {
            expertName = jsonObject.getString(GlobalConstants.JSON_EXPERTNAME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_EXPERTTYPE)) {
            expertType = jsonObject.getInteger(GlobalConstants.JSON_EXPERTTYPE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_FLAG)) {
            flag = jsonObject.getInteger(GlobalConstants.JSON_FLAG);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_HASFOLLOWED)) {
            hasFollowed = jsonObject.getBoolean(GlobalConstants.JSON_HASFOLLOWED);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_HEADURL)) {
            headUrl = jsonObject.getString(GlobalConstants.JSON_HEADURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
            name = jsonObject.getString(GlobalConstants.JSON_NAME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_USERID)) {
            userId = jsonObject.getString(GlobalConstants.JSON_USERID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_LASTARTICLEID)) {
            lastArticleId = jsonObject.getString(GlobalConstants.JSON_LASTARTICLEID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_LASTIMGS)) {
            lastImgs = jsonObject.getJSONArray(GlobalConstants.JSON_LASTIMGS);
        }
    }

    public void setChildStatus(int childStatus) {
        this.childStatus = childStatus;
    }
    public int getChildStatus() {
        return this.childStatus;
    }

    public void setExpertDesc(String expertDesc) {
        this.expertDesc = expertDesc;
    }
    public String getExpertDesc() {
        return this.expertDesc;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }
    public String getExpertName() {
        return this.expertName;
    }

    public void setExpertType(int expertType) {
        this.expertType = expertType;
    }
    public int getExpertType() {
        return this.expertType;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
    public int getFlag() {
        return this.flag;
    }

    public void setHasFollowed(boolean hasFollowed) {
        this.hasFollowed = hasFollowed;
    }
    public boolean isHasFollowed() {
        return this.hasFollowed;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }
    public String getHeadUrl() {
        return this.headUrl;
    }

    public void setLastArticleId(String lastArticleId) {
        this.lastArticleId = lastArticleId;
    }
    public String getLastArticleId() {
        return this.lastArticleId;
    }

    public void setLastImgIds(String lastImgIds) {
        this.lastImgIds = lastImgIds;
    }
    public String getLastImgIds() {
        return this.lastImgIds;
    }

    public void setLastImgs(JSONArray lastImgs) {
        this.lastImgs = lastImgs;
    }
    public JSONArray getLastImgs() {
        return this.lastImgs;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return this.userId;
    }
}
