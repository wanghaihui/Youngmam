package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-10-17.
 */
public class UserBase implements Parcelable {
    private int childStatus;
    private int expertType;
    private int flag;
    private boolean hasFollowed;
    private String headUrl;
    private String lastArticleId;
    private String name;
    private String userId;

    public UserBase() {
        childStatus = 0;
        expertType = 0;
        flag = 0;
        hasFollowed = false;
        headUrl = null;
        lastArticleId = null;
        name = null;
        userId = null;
    }

    public UserBase(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_CHILDSTATUS)) {
            childStatus = jsonObject.getInteger(GlobalConstants.JSON_CHILDSTATUS);
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

        if (jsonObject.containsKey(GlobalConstants.JSON_LASTARTICLEID)) {
            lastArticleId = jsonObject.getString(GlobalConstants.JSON_LASTARTICLEID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
            name = jsonObject.getString(GlobalConstants.JSON_NAME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_USERID)) {
            userId = jsonObject.getString(GlobalConstants.JSON_USERID);
        }
    }

    public void setChildStatus(int childStatus) {
        this.childStatus = childStatus;
    }
    public int getChildStatus() {
        return this.childStatus;
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

    //////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(childStatus);
        dest.writeInt(expertType);
        dest.writeInt(flag);
        dest.writeInt(hasFollowed ? 1 : 0);
        dest.writeString(headUrl);
        dest.writeString(lastArticleId);
        dest.writeString(name);
        dest.writeString(userId);
    }

    // 重写CREATOR
    public static final Parcelable.Creator<UserBase> CREATOR = new Parcelable.Creator<UserBase>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public UserBase createFromParcel(Parcel source) {
            UserBase userBase = new UserBase();
            userBase.childStatus = source.readInt();
            userBase.expertType = source.readInt();
            userBase.flag = source.readInt();
            userBase.hasFollowed = (source.readInt() == 1) ? true : false;
            userBase.headUrl = source.readString();
            userBase.lastArticleId = source.readString();
            userBase.name = source.readString();
            userBase.userId = source.readString();

            return userBase;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public UserBase[] newArray(int size) {
            return new UserBase[size];
        }
    };
}
