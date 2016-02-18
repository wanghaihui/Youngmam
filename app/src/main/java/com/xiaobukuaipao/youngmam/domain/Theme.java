package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-6-24.
 */
public class Theme implements Parcelable {
    private String desc;

    private transient JSONArray imgs;

    private String posterUrl;

    private String smallPosterUrl;

    private Label tag;

    public Theme() {
        this.desc = null;
        imgs = null;
        this.posterUrl = null;
        smallPosterUrl = null;
        this.tag = null;
    }

    public Theme(String desc, String posterUrl, Label tag) {
        this.desc = desc;
        this.posterUrl = posterUrl;
        this.tag = tag;
    }

    public Theme(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_DESC)) {
            desc = jsonObject.getString(GlobalConstants.JSON_DESC);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_IMGS)) {
            imgs = jsonObject.getJSONArray(GlobalConstants.JSON_IMGS);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_POSTERURL)) {
            posterUrl = jsonObject.getString(GlobalConstants.JSON_POSTERURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_SMALLPOSTERURL)) {
            smallPosterUrl = jsonObject.getString(GlobalConstants.JSON_SMALLPOSTERURL);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TAG)) {
            tag = new Label(jsonObject.getJSONObject(GlobalConstants.JSON_TAG));
        }
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return this.desc;
    }

    public void setImgs(JSONArray imgs) {
        this.imgs = imgs;
    }
    public JSONArray getImgs() {
        return this.imgs;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    public String getPosterUrl() {
        return this.posterUrl;
    }

    public void setSmallPosterUrl(String smallPosterUrl) {
        this.smallPosterUrl = smallPosterUrl;
    }
    public String getSmallPosterUrl() {
        return this.smallPosterUrl;
    }

    public void setTag(Label tag) {
        this.tag = tag;
    }
    public Label getTag() {
        return this.tag;
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(desc);
        dest.writeString(posterUrl);
        dest.writeString(smallPosterUrl);
        dest.writeParcelable(tag, PARCELABLE_WRITE_RETURN_VALUE);
    }

    // 重写CREATOR
    public static final Parcelable.Creator<Theme> CREATOR = new Parcelable.Creator<Theme>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public Theme createFromParcel(Parcel source) {
            Theme theme = new Theme();

            theme.desc = source.readString();
            theme.posterUrl = source.readString();
            theme.smallPosterUrl = source.readString();
            theme.tag = source.readParcelable(Label.class.getClassLoader());

            return theme;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };
}
