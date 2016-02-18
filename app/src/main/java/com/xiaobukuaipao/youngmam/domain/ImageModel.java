package com.xiaobukuaipao.youngmam.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiaobu1 on 15-5-4.
 */
public class ImageModel implements Parcelable {
    // 图片的路径
    public String path;
    // 图片的缩略图路径
    public String thumbnail = null;
    // 此项是功能项
    public boolean isFunction = false;
    // 此图片是否被选择
    public boolean isSelected = false;

    //////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(thumbnail);
        dest.writeByte((byte) (isFunction ? 1 : 0));
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    // 重写CREATOR
    public static final Parcelable.Creator<ImageModel> CREATOR = new Parcelable.Creator<ImageModel>() {

        // 从Parcel中创建出类的实例的功能
        @Override
        public ImageModel createFromParcel(Parcel source) {
            ImageModel imageModel = new ImageModel();

            imageModel.path = source.readString();
            imageModel.thumbnail = source.readString();
            imageModel.isFunction = source.readByte() != 0;
            imageModel.isSelected = source.readByte() != 0;

            return imageModel;
        }

        // 创建一个类型为T，长度为size的数组，仅一句话(return new T[size])即可--估计本方法是供外部类反序列化本类数组使用
        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };
}
