package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class Category implements Parcelable {
    // 分类Id
    private String id;
    // 分类名字
    private String name;
    // 分类对应的图片
    private int imgId;

    public Category() {
        id = null;
        name = null;
        imgId = 0;
    }

    public Category(String id, String name, int imgId) {
        this.id = id;
        this.name = name;
        this.imgId = imgId;
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

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
    public int getImgId() {
        return this.imgId;
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
        dest.writeInt(imgId);
    }

    // 重写CREATOR
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public Category createFromParcel(Parcel source) {
            Category category = new Category();

            category.id = source.readString();
            category.name = source.readString();
            category.imgId = source.readInt();

            return category;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
