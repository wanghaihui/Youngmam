package com.xiaobukuaipao.youngmam.filter.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

/**
 * Created by xiaobu1 on 15-9-11.
 * 羽状Drawable
 */
public interface FeatherDrawable {

    // 设置最小的Size
    public void setMinSize(float width, float height);
    public float getMinWidth();
    public float getMinHeight();

    // 验证Size, 是否合法
    public boolean validateSize(RectF rectF);

    // 绘制
    public void draw(Canvas canvas);

    // 设置边界
    public void setBounds(int left, int top, int right, int bottom);
    public void setBounds(Rect rect);
    public void copyBounds(Rect rect);
    public Rect copyBounds();
    public Rect getBounds();

    // 设置改变配置
    public void setChangingConfigurations(int paramInt);
    public int getChangingConfigurations();

    // 设置抖动
    public void setDither(boolean paramBoolean);

    // 设置滤镜Bitmap
    public void setFilterBitmap(boolean paramBoolean);

    // 设置回调
    public void setCallback(Drawable.Callback callback);

    // 刷新自己
    public void invalidateSelf();

    // 调度自己
    public void scheduleSelf(Runnable runnable, long paramLong);

    // 取消调度自己
    public void unscheduleSelf(Runnable runnable);

    // 设置透明度
    public void setAlpha(int alpha);

    // 设置颜色过滤器
    public void setColorFilter(ColorFilter colorFilter);

    /**
     * Porter-Duff 操作是1组12项用于描述数字图像合成的基本手法
     * 包括Clear, Source Only, Destination Only, Source Over, Source In,
     * Source Out, Source Atop, Destination Over, Destination In,
     * Destination Out, Destination Atop, XOR
     * 通过组合使用 Porter-Duff 操作, 可完成任意2D图像的合成
     */
    public void setColorFilter(int paramInt, PorterDuff.Mode mode);

    // 清除颜色过滤器
    public void clearColorFilter();

    /**
     * 是否处于状态
     */
    public boolean isStateful();
    public boolean setState(int[] state);
    public int[] getState();

    // 得到当前的Drawable
    public Drawable getCurrent();

    // 设置级别
    public boolean setLevel(int level);
    public int getLevel();

    // 设置可见性
    public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2);
    public boolean isVisible();

    // 得到不透明性
    public int getOpacity();

    // 得到转换区域
    public Region getTransparentRegion();

    // 得到当前的宽度和高度
    public float getCurrentWidth();
    public float getCurrentHeight();

    // 得到最小的宽度和高度
    public int getMinimumWidth();
    public int getMinimumHeight();

    // 得到Padding
    public boolean getPadding(Rect rect);

    // 变换
    public Drawable mutate();

}
