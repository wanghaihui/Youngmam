package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-5-7.
 */
public class Label implements Parcelable {

    private int bgResId;
    private boolean isChecked;
    private int leftDrawableResId;
    private int rightDrawableResId;

    private String id;
    private String title;

    public Label() {

    }

    public Label(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Label(JSONObject jsonObject) {
        if (jsonObject.containsKey(GlobalConstants.JSON_TAGID)) {
            id = jsonObject.getString(GlobalConstants.JSON_TAGID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
            title = jsonObject.getString(GlobalConstants.JSON_NAME);
        }
    }

    public void setBgResId(int bgResId) {
        this.bgResId = bgResId;
    }
    public int getBgResId() {
        return this.bgResId;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public boolean getChecked() {
        return this.isChecked;
    }

    public void setLeftDrawableResId(int leftDrawableResId) {
        this.leftDrawableResId = leftDrawableResId;
    }
    public int getLeftDrawableResId() {
        return this.leftDrawableResId;
    }

    public void setRightDrawableResId(int rightDrawableResId) {
        this.rightDrawableResId = rightDrawableResId;
    }
    public int getRightDrawableResId() {
        return this.rightDrawableResId;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
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
        dest.writeInt(bgResId);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(leftDrawableResId);
        dest.writeInt(rightDrawableResId);
        dest.writeString(id);
        dest.writeString(title);
    }

    // 重写CREATOR
    public static final Parcelable.Creator<Label> CREATOR = new Parcelable.Creator<Label>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public Label createFromParcel(Parcel source) {
            Label label = new Label();

            label.bgResId = source.readInt();
            label.isChecked = source.readByte() != 0;
            label.leftDrawableResId = source.readInt();
            label.rightDrawableResId = source.readInt();
            label.id = source.readString();
            label.title = source.readString();

            return label;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };

}
