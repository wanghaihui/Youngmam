package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by xiaobu1 on 15-6-18.
 */
public class BannerActivity implements Parcelable {

    private String businessId;
    private int businessType;
    private int position;
    private String posterUrl;
    private String targetUrl;
    private String title;

    public BannerActivity() {
        businessId = null;
        businessType = 0;
        position = 0;
        posterUrl = null;
        targetUrl = null;
        title = null;
    }

    public BannerActivity(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSID)) {
            businessId = jsonObject.getString(GlobalConstants.JSON_BUSINESSID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSTYPE)) {
            businessType = jsonObject.getInteger(GlobalConstants.JSON_BUSINESSTYPE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_POSITION)) {
            position = jsonObject.getInteger(GlobalConstants.JSON_POSITION);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_POSTERURL)) {
            posterUrl = jsonObject.getString(GlobalConstants.JSON_POSTERURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TARGETURL)) {
            targetUrl = jsonObject.getString(GlobalConstants.JSON_TARGETURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TITLE)) {
            title = jsonObject.getString(GlobalConstants.JSON_TITLE);
        }
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

    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return this.position;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    public String getPosterUrl() {
        return this.posterUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    public String getTargetUrl() {
        return this.targetUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(businessId);
        dest.writeInt(businessType);
        dest.writeInt(position);
        dest.writeString(posterUrl);
        dest.writeString(targetUrl);
        dest.writeString(title);
    }

    // 重写CREATOR
    public static final Parcelable.Creator<BannerActivity> CREATOR = new Parcelable.Creator<BannerActivity>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public BannerActivity createFromParcel(Parcel source) {
            BannerActivity bannerActivity = new BannerActivity();

            bannerActivity.businessId = source.readString();
            bannerActivity.businessType = source.readInt();
            bannerActivity.position = source.readInt();
            bannerActivity.posterUrl = source.readString();
            bannerActivity.targetUrl = source.readString();
            bannerActivity.title = source.readString();

            return bannerActivity;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public BannerActivity[] newArray(int size) {
            return new BannerActivity[size];
        }
    };
}
