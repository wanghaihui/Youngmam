package com.xiaobukuaipao.youngmam.filter.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class StickerDrawable extends BitmapDrawable implements FeatherDrawable {

    // 最小宽度和高度
    private float  minWidth = 0.0F;
    private float  minHeight = 0.0F;

    // 模糊滤镜
    BlurMaskFilter mBlurFilter;
    // 阴影Paint--阴影画笔
    Paint mShadowPaint;
    // 阴影Bitmap
    Bitmap mShadowBitmap;
    boolean mDrawShadow = true;
    
    Rect mTempRect = new Rect();

    public StickerDrawable(Resources resources, Bitmap bitmap) {
        // 设置Bitmap
        super(resources, bitmap);

        // BlurMaskFilter.Blur.OUTER--在目标外显示面具, 从边缘向目标外到离边缘radius宽的地方, 并且该部分会显示出从目标边缘获得的颜色, 不显示目标
        this.mBlurFilter = new BlurMaskFilter(5.0F, BlurMaskFilter.Blur.OUTER);
        
        this.mShadowPaint = new Paint(1);
        this.mShadowPaint.setMaskFilter(this.mBlurFilter);

        int[] offsetXY = new int[2];
        this.mShadowBitmap = getBitmap().extractAlpha(this.mShadowPaint, offsetXY);
    }

    public int getBitmapWidth() {
        return getBitmap().getWidth();
    }

    public int getBitmapHeight() {
        return getBitmap().getHeight();
    }

    public void draw(Canvas canvas) {
        if (this.mDrawShadow) {
            copyBounds(this.mTempRect);
            canvas.drawBitmap(this.mShadowBitmap, null, this.mTempRect, null);
        }
        super.draw(canvas);
    }

    public void setDropShadow(boolean value) {
        this.mDrawShadow = value;
        invalidateSelf();
    }

    /**
     * 是否合法的大小
     * @param rect
     */
    public boolean validateSize(RectF rect) {
        return (rect.width() >= this.minWidth) && (rect.height() >= this.minHeight);
    }

    public void setMinSize(float width, float height) {
        this.minWidth = width;
        this.minHeight = height;
    }

    public float getMinWidth() {
        return this.minWidth;
    }

    public float getMinHeight() {
        return this.minHeight;
    }

    public float getCurrentWidth() {
        return getIntrinsicWidth();
    }

    public float getCurrentHeight() {
        return getIntrinsicHeight();
    }

}
