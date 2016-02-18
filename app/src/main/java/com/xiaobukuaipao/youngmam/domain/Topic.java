package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class Topic implements Parcelable {

    private String id;
    private String businessId;
    private int businessType;
    private String title;
    private String desc;
    private String posterUrl;
    private int posterMarker;
    private String targetUrl;
    private int displayTitle;
    private int category;
    private JSONObject categoryObj;
    private int likeCount;
    private boolean hasLiked;

    public Topic() {
        id = null;
        businessId = null;
        businessType = 0;
        title = null;
        desc = null;
        posterUrl = null;
        posterMarker = 0;
        targetUrl = null;
        displayTitle = 0;
        category = 0;
        categoryObj = null;
        likeCount = 0;
        hasLiked = false;
    }

    public Topic(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_ID)) {
            id = jsonObject.getString(GlobalConstants.JSON_ID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSID)) {
            businessId = jsonObject.getString(GlobalConstants.JSON_BUSINESSID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSTYPE)) {
            businessType = jsonObject.getInteger(GlobalConstants.JSON_BUSINESSTYPE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TITLE)) {
            title = jsonObject.getString(GlobalConstants.JSON_TITLE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_DESC)) {
            desc = jsonObject.getString(GlobalConstants.JSON_DESC);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_POSTERURL)) {
            posterUrl = jsonObject.getString(GlobalConstants.JSON_POSTERURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_POSTERMARKER)) {
            posterMarker = jsonObject.getInteger(GlobalConstants.JSON_POSTERMARKER);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TARGETURL)) {
            targetUrl = jsonObject.getString(GlobalConstants.JSON_TARGETURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_DISPLAYTITLE)) {
            displayTitle = jsonObject.getInteger(GlobalConstants.JSON_DISPLAYTITLE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CATEGORY)) {
            category = jsonObject.getInteger(GlobalConstants.JSON_CATEGORY);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_CATEGORYOBJ)) {
            categoryObj = jsonObject.getJSONObject(GlobalConstants.JSON_CATEGORYOBJ);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_LIKECOUNT)) {
            likeCount = jsonObject.getInteger(GlobalConstants.JSON_LIKECOUNT);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_HASLIKED)) {
            hasLiked = jsonObject.getBoolean(GlobalConstants.JSON_HASLIKED);
        }
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    public String getBusinessId() {
        return this.businessId;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }
    public int getBusinessType() {
        return this.businessType;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
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

    public void setPosterMarker(int posterMarker) {
        this.posterMarker = posterMarker;
    }
    public int getPosterMarker() {
        return this.posterMarker;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    public String getTargetUrl() {
        return this.targetUrl;
    }

    public void setDisplayTitle(int displayTitle) {
        this.displayTitle = displayTitle;
    }
    public int getDisplayTitle() {
        return this.displayTitle;
    }

    public void setCategory(int category) {
        this.category = category;
    }
    public int getCategory() {
        return this.category;
    }

    public void setCategoryObj(JSONObject categoryObj) {
        this.categoryObj = categoryObj;
    }
    public JSONObject getCategoryObj() {
        return this.categoryObj;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public int getLikeCount() {
        return this.likeCount;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }
    public boolean isHasLiked () {
        return this.hasLiked;
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(businessId);
        dest.writeInt(businessType);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(posterUrl);
        dest.writeInt(posterMarker);
        dest.writeString(targetUrl);
        dest.writeInt(displayTitle);
        dest.writeInt(category);
        dest.writeSerializable(categoryObj);
        dest.writeInt(likeCount);
        dest.writeInt(hasLiked ? 1 : 0);
    }

    // 重写CREATOR
    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public Topic createFromParcel(Parcel source) {
            Topic topic = new Topic();

            topic.id = source.readString();
            topic.businessId = source.readString();
            topic.businessType = source.readInt();
            topic.title = source.readString();
            topic.desc = source.readString();
            topic.posterUrl = source.readString();
            topic.posterMarker = source.readInt();
            topic.targetUrl = source.readString();
            topic.displayTitle = source.readInt();
            topic.category = source.readInt();
            topic.categoryObj = (JSONObject) source.readSerializable();
            topic.likeCount = source.readInt();
            topic.hasLiked = (source.readInt() == 1) ? true : false;

            return topic;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
