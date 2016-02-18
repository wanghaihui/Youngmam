package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-4-27.
 */
public class Comment {
    private String articleId;
    // 业务类型
    private int businessType;
    private JSONArray childrenCommnets;
    private String commentId;
    private String content;
    private long createTime;

    private boolean hasLiked;
    private int likeCount;

    private String imgIds;
    private JSONArray imgs;

    private String originCommentId;
    private JSONObject originUserBase;
    private String rootCommentId;
    private int status;
    private UserBase userBase;

    public Comment() {
        articleId = null;
        businessType = 0;
        childrenCommnets = null;
        commentId = null;
        content = null;
        createTime = 0;

        hasLiked = false;
        likeCount = 0;

        imgIds = null;
        imgs = null;

        originCommentId = null;
        originUserBase = null;
        rootCommentId = null;
        status = 0;
        userBase = null;
    }

    public Comment(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_ARTICLEID)) {
            articleId = jsonObject.getString(GlobalConstants.JSON_ARTICLEID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSTYPE)) {
            businessType = jsonObject.getInteger(GlobalConstants.JSON_BUSINESSTYPE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CHILDRENCOMMENTS)) {
            childrenCommnets = jsonObject.getJSONArray(GlobalConstants.JSON_CHILDRENCOMMENTS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_COMMENTID)) {
            commentId = jsonObject.getString(GlobalConstants.JSON_COMMENTID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CONTENT)) {
            content = jsonObject.getString(GlobalConstants.JSON_CONTENT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CREATETIME)) {
            createTime = jsonObject.getLong(GlobalConstants.JSON_CREATETIME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_HASLIKED)) {
            hasLiked = jsonObject.getBoolean(GlobalConstants.JSON_HASLIKED);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_LIKECOUNT)) {
            likeCount = jsonObject.getInteger(GlobalConstants.JSON_LIKECOUNT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGIDS)) {
            imgIds = jsonObject.getString(GlobalConstants.JSON_IMGIDS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGS)) {
            imgs = jsonObject.getJSONArray(GlobalConstants.JSON_IMGS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ORIGINCOMMENTID)) {
            originCommentId = jsonObject.getString(GlobalConstants.JSON_ORIGINCOMMENTID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ORIGINUSERBASE)) {
            originUserBase = jsonObject.getJSONObject(GlobalConstants.JSON_ORIGINUSERBASE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ROOTCOMMENTID)) {
            rootCommentId = jsonObject.getString(GlobalConstants.JSON_ROOTCOMMENTID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_STATUS)) {
            status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_USERBASE)) {
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

    public void setChildrenCommnets(JSONArray childrenCommnets) {
        this.childrenCommnets = childrenCommnets;
    }
    public JSONArray getChildrenCommnets() {
        return this.childrenCommnets;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    public String getCommentId() {
        return this.commentId;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return this.content;
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
    public boolean isHasLiked() {
        return this.hasLiked;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public int getLikeCount() {
        return this.likeCount;
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

    public void setOriginCommentId(String originCommentId) {
        this.originCommentId = originCommentId;
    }
    public String getOriginCommentId() {
        return this.originCommentId;
    }

    public void setOriginUserBase(JSONObject originUserBase) {
        this.originUserBase = originUserBase;
    }
    public JSONObject getOriginUserBase() {
        return this.originUserBase;
    }

    public void setRootCommentId(String rootCommentId) {
        this.rootCommentId = rootCommentId;
    }
    public String getRootCommentId() {
        return this.rootCommentId;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return this.status;
    }

    public void setUserBase(UserBase userBase) {
        this.userBase = userBase;
    }
    public UserBase getUserBase() {
        return this.userBase;
    }
}
