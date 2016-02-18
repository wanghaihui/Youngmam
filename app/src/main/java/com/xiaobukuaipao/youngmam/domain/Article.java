package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-5-16.
 */
public class Article {

    private String articleId;
    // 区分类型
    private int businessType;
    private String content;
    private int commentCount;
    private long createTime;
    private boolean hasLiked;
    private String imgIds;
    private JSONArray imgs;
    private int likeCount;
    private int status;
    private JSONArray tags;
    private UserBase userBase;


    public Article() {
        articleId = null;
        // 默认是6, 图片+文章
        businessType = 6;
        content = null;
        commentCount = 0;
        createTime = 0;
        hasLiked = false;
        imgIds = null;
        imgs = null;
        likeCount = 0;
        status = 0;
        tags = null;
        userBase = null;
    }

    public Article(JSONObject jsonObject) {

        if (jsonObject.getString(GlobalConstants.JSON_ARTICLEID) != null) {
            articleId = jsonObject.getString(GlobalConstants.JSON_ARTICLEID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSTYPE)) {
            businessType = jsonObject.getInteger(GlobalConstants.JSON_BUSINESSTYPE);
        }

        if (jsonObject.getString(GlobalConstants.JSON_CONTENT) != null) {
            content = jsonObject.getString(GlobalConstants.JSON_CONTENT);
        }

        if (jsonObject.getInteger(GlobalConstants.JSON_COMMENTCOUNT) != null) {
            commentCount = jsonObject.getInteger(GlobalConstants.JSON_COMMENTCOUNT);
        }

        if (jsonObject.getLong(GlobalConstants.JSON_CREATETIME) != null) {
            createTime = jsonObject.getLong(GlobalConstants.JSON_CREATETIME);
        }

        if (jsonObject.getBoolean(GlobalConstants.JSON_HASLIKED) != null) {
            hasLiked = jsonObject.getBoolean(GlobalConstants.JSON_HASLIKED);
        }

        if (jsonObject.getString(GlobalConstants.JSON_IMGIDS) != null) {
            imgIds = jsonObject.getString(GlobalConstants.JSON_IMGIDS);
        }

        if (jsonObject.getJSONArray(GlobalConstants.JSON_IMGS) != null) {
            imgs = jsonObject.getJSONArray(GlobalConstants.JSON_IMGS);
        }

        if (jsonObject.getInteger(GlobalConstants.JSON_LIKECOUNT) != null) {
            likeCount = jsonObject.getInteger(GlobalConstants.JSON_LIKECOUNT);
        }

        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null) {
            status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
        }

        if (jsonObject.getJSONArray(GlobalConstants.JSON_TAGS) != null) {
            tags = jsonObject.getJSONArray(GlobalConstants.JSON_TAGS);
        }

        if (jsonObject.getJSONObject(GlobalConstants.JSON_USERBASE) != null) {
            userBase = new UserBase(jsonObject.getJSONObject(GlobalConstants.JSON_USERBASE));
        }
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getArticleId() {
        return this.articleId;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }
    public int getBusinessType() {
        return this.businessType;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public int getCommentCount() {
        return this.commentCount;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public long getCreateTime() {
        return this.createTime;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }
    public boolean getHasLiked() {
        return this.hasLiked;
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

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public int getLikeCount() {
        return this.likeCount;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return this.status;
    }

    public void setTags(JSONArray tags) {
        this.tags = tags;
    }
    public JSONArray getTags() {
        return this.tags;
    }

    public void setUserBase(UserBase userBase) {
        this.userBase = userBase;
    }
    public UserBase getUserBase() {
        return this.userBase;
    }

}
