package com.xiaobukuaipao.youngmam.filter;

import com.xiaobukuaipao.youngmam.filter.util.GPUImageFilterTools;

/**
 * Created by xiaobu1 on 15-9-1.
 * 滤镜效果
 */
public class FilterEffect {
    // 滤镜标题
    private String title;
    // 滤镜类型
    private GPUImageFilterTools.FilterType type;
    // 程度--度
    private int degree;

    private boolean selected;

    public FilterEffect(String title, GPUImageFilterTools.FilterType type, int degree) {
        this.title = title;
        this.type = type;
        this.degree = degree;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setType(GPUImageFilterTools.FilterType type) {
        this.type = type;
    }
    public GPUImageFilterTools.FilterType getType() {
        return type;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
    public int getDegree() {
        return degree;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected() {
        return this.selected;
    }

}
