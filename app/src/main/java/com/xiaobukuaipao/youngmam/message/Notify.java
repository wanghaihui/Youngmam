package com.xiaobukuaipao.youngmam.message;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-5-21.
 * 通知
 */
public class Notify {
    private JSONObject actorUserBase;
    private String actorId;
    private String articleId;
    private String content;
    private long createTime;
    private String msgId;
    private String objectId;
    private int readStatus;
    private String receiverId;
    private int type;
    private String title;

    private String targetUrl;

    public Notify() {
        actorUserBase = null;
        actorId = null;
        articleId = null;
        content = null;
        createTime = 0;
        msgId = null;
        objectId = null;
        readStatus = 0;
        receiverId = null;
        type = 0;
        targetUrl = null;
    }

    public Notify(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_ACTORUSERBASE)) {
            actorUserBase = jsonObject.getJSONObject(GlobalConstants.JSON_ACTORUSERBASE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ACTORID)) {
            actorId = jsonObject.getString(GlobalConstants.JSON_ACTORID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_ARTICLEID)) {
            articleId = jsonObject.getString(GlobalConstants.JSON_ARTICLEID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CONTENT)) {
            content = jsonObject.getString(GlobalConstants.JSON_CONTENT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CREATETIME)) {
            createTime = jsonObject.getLong(GlobalConstants.JSON_CREATETIME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_MSGID)) {
            msgId = jsonObject.getString(GlobalConstants.JSON_MSGID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_OBJECTID)) {
            objectId = jsonObject.getString(GlobalConstants.JSON_OBJECTID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_READSTATUS)) {
            readStatus = jsonObject.getInteger(GlobalConstants.JSON_READSTATUS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_RECEIVERID)) {
            receiverId = jsonObject.getString(GlobalConstants.JSON_RECEIVERID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TYPE)) {
            type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TITLE)) {
            title = jsonObject.getString(GlobalConstants.JSON_TITLE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TARGETURL)) {
            targetUrl = jsonObject.getString(GlobalConstants.JSON_TARGETURL);
        }
    }

    public void setActorUserBase(JSONObject actorUserBase) {
        this.actorUserBase = actorUserBase;
    }
    public JSONObject getActorUserBase() {
        return this.actorUserBase;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }
    public String getActorId() {
        return this.actorId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getArticleId() {
        return this.articleId;
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

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getMsgId() {
        return this.msgId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    public String getObjectId() {
        return this.objectId;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }
    public int getReadStatus() {
        return this.readStatus;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public String getReceiverId() {
        return this.receiverId;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    public String getTargetUrl() {
        return this.targetUrl;
    }
}
