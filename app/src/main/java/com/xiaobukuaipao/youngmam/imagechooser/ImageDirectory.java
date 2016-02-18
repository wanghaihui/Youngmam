package com.xiaobukuaipao.youngmam.imagechooser;

import com.xiaobukuaipao.youngmam.domain.ImageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-13.
 */

/**
 * 图片目录
 */
public class ImageDirectory {
    /**
     * 文件夹名
      */
    private String dirName = "";
    /**
     * 文件夹下的所有图片
     */
    private List<ImageModel> images = new ArrayList<ImageModel>();

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return this.dirName;
    }

    /**
     * 获取第一张图片(作为封面)
     */
    public ImageModel getFirstImagePath() {
        if (images.size() > 0) {
            return images.get(0);
        }
        return new ImageModel();
    }

    public List<ImageModel> getImages() {
        return images;
    }

    /**
     * 获取图片的数量
     */
    public int getImagesCount() {
        return images.size();
    }

    /**
     * 添加一张图片
     * @param image
     */
    public void addImage(ImageModel image) {
        if (images == null) {
            images = new ArrayList<ImageModel>();
        }

        images.add(image);
    }

    /**
     * 重写toString函数
     * @return 提示信息
     */
    @Override
    public String toString() {
        return "ImageDirectory [first image path=" + getFirstImagePath() + ", dirName=" + dirName
                + ", imageCount=" + getImagesCount() + "]";
    }

    /**
     * 重写该方法, 使只要图片所在的文件夹名称(dirName)相同就属于同一个图片组
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  ImageDirectory)) {
            return false;
        }

        return dirName.equals(((ImageDirectory) o).dirName);
    }

}
