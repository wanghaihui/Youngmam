package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-10-10.
 */
public class NavigationCategory implements Parcelable {
    private String id;
    private String name;
    private String rgbColor;

    public NavigationCategory() {
        id = null;
        name = null;
        rgbColor = null;
    }

    public NavigationCategory(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_ID)) {
            id = jsonObject.getString(GlobalConstants.JSON_ID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
            name = jsonObject.getString(GlobalConstants.JSON_NAME);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_RGBCOLOR)) {
            rgbColor = jsonObject.getString(GlobalConstants.JSON_RGBCOLOR);
        }
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public void setRgbColor(String rgbColor) {
        this.rgbColor = rgbColor;
    }
    public String getRgbColor() {
        return this.rgbColor;
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(rgbColor);
    }

    // 重写CREATOR
    public static final Parcelable.Creator<NavigationCategory> CREATOR = new Parcelable.Creator<NavigationCategory>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public NavigationCategory createFromParcel(Parcel source) {
            NavigationCategory category = new NavigationCategory();

            category.id = source.readString();
            category.name = source.readString();
            category.rgbColor = source.readString();

            return category;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public NavigationCategory[] newArray(int size) {
            return new NavigationCategory[size];
        }
    };
}
