package com.xiaobukuaipao.youngmam.domain;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-10-7.
 */
public class Sticker {
    // 对齐方式
    private int alignment;
    private StickerGroup group;
    private String groupId;
    private JSONObject img;
    private String imgId;
    private int marginX;
    private int marginY;
    private JSONObject smallImg;
    private String smallImgId;
    private String stickerId;
    private String stickerName;

    private boolean selected;

    public Sticker() {
        alignment = 0;
        group = null;
        groupId = null;
        img = null;
        imgId = null;
        marginX = 0;
        marginY = 0;
        smallImg = null;
        smallImgId = null;
        stickerId = null;
        stickerName = null;
        selected = false;
    }

    public Sticker(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_ALIGNMENT)) {
            alignment = jsonObject.getInteger(GlobalConstants.JSON_ALIGNMENT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_GROUP)) {
            group = new StickerGroup(jsonObject.getJSONObject(GlobalConstants.JSON_GROUP));
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_GROUPID)) {
            groupId = jsonObject.getString(GlobalConstants.JSON_GROUPID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMG)) {
            img = jsonObject.getJSONObject(GlobalConstants.JSON_IMG);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGID)) {
            imgId = jsonObject.getString(GlobalConstants.JSON_IMGID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_MARGINX)) {
            marginX = jsonObject.getInteger(GlobalConstants.JSON_MARGINX);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_MARGINY)) {
            marginY = jsonObject.getInteger(GlobalConstants.JSON_MARGINY);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_SMALLIMG)) {
            smallImg = jsonObject.getJSONObject(GlobalConstants.JSON_SMALLIMG);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_SMALLIMGID)) {
            smallImgId = jsonObject.getString(GlobalConstants.JSON_SMALLIMGID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_STICKERID)) {
            stickerId = jsonObject.getString(GlobalConstants.JSON_STICKERID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_STICKERNAME)) {
            stickerName = jsonObject.getString(GlobalConstants.JSON_STICKERNAME);
        }
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }
    public int getAlignment() {
        return this.alignment;
    }

    public void setGroup(StickerGroup group) {
        this.group = group;
    }
    public StickerGroup getGroup() {
        return this.group;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupId() {
        return this.groupId;
    }

    public void setImg(JSONObject img) {
        this.img = img;
    }
    public JSONObject getImg() {
        return this.img;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }
    public String getImgId() {
        return this.imgId;
    }

    public void setMarginX(int marginX) {
        this.marginX = marginX;
    }
    public int getMarginX() {
        return this.marginX;
    }

    public void setMarginY(int marginY) {
        this.marginY = marginY;
    }
    public int getMarginY() {
        return this.marginY;
    }

    public void setSmallImg(JSONObject smallImg) {
        this.smallImg = smallImg;
    }
    public JSONObject getSmallImg() {
        return this.smallImg;
    }

    public void setSmallImgId(String smallImgId) {
        this.smallImgId = smallImgId;
    }
    public String getSmallImgId() {
        return this.smallImgId;
    }

    public void setStickerId(String stickerId) {
        this.stickerId = stickerId;
    }
    public String getStickerId() {
        return this.stickerId;
    }

    public void setStickerName(String stickerName) {
        this.stickerName = stickerName;
    }
    public String getStickerName() {
        return this.stickerName;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected() {
        return this.selected;
    }
}
