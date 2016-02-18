package com.xiaobukuaipao.youngmam.filter.drawable;

import android.graphics.Paint;

/**
 * Created by xiaobu1 on 15-9-11.
 * 可编辑的Drawable
 */
public interface EditableDrawable {
    // blink--闪烁
    public static final int CURSOR_BLINK_TIME = 400;

    // 设置大小改变监听器
    public void setOnSizeChangeListener(OnSizeChange paramOnSizeChange);

    // 编辑
    public void beginEdit();
    public void endEdit();
    public boolean isEditing();

    // 得到Text
    public CharSequence getText();
    // 设置Text
    public void setText(CharSequence paramCharSequence);
    public void setText(String paramString);

    // hint--暗示的意思
    public void setTextHint(CharSequence paramCharSequence);
    public void setTextHint(String paramString);
    // 是否是hint
    public boolean isTextHint();

    // 设置边界
    public void setBounds(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    // 颜色
    public void setTextColor(int paramInt);
    public int getTextColor();

    // 大小
    public float getTextSize();

    // 字体矩阵
    public float getFontMetrics(Paint.FontMetrics paramFontMetrics);

    // stroke--文本描边颜色
    public void setTextStrokeColor(int paramInt);
    public int getTextStrokeColor();
    public void setStrokeEnabled(boolean paramBoolean);
    public boolean getStrokeEnabled();

    // 行数
    public int getNumLines();

    /**
     * 大小改变
     */
    public static interface OnSizeChange {
        public void onSizeChanged(EditableDrawable content, float left,
                                  float top, float right, float bottom);
    }
}
