package com.xiaobukuaipao.youngmam.filter.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

/**
 * Created by xiaobu1 on 15-9-16.
 * Fast bitmap drawable
 * Dose not support states
 * It only support alpha and ColorMatrix
 */
public class FastBitmapDrawable extends Drawable implements IBitmapDrawable {

    protected Bitmap mBitmap;

    // The Paint class holds the style and color information about how to draw geometries(几何形状), text(文本) and bitmaps(图像)
    protected Paint mPaint;

    public FastBitmapDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        mPaint = new Paint();
        // Dithering affects how colors that are higher precision than the device are down-sampled
        mPaint.setDither(true);
        // Filtering affects the sampling of bitmaps when they are transformed
        mPaint.setFilterBitmap(true);
    }

    public FastBitmapDrawable(Resources res, InputStream inputStream) {
        this(BitmapFactory.decodeStream(inputStream));
    }

    @Override
    public void draw(Canvas canvas) {
        // 绘制Bitmap
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    /**
     * 得到不透明性
     */
    @Override
    public int getOpacity() {
        // 半透明
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public int getMinimumWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getMinimumHeight() {
        return mBitmap.getHeight();
    }

    public void setAntiAlias(boolean value) {
        mPaint.setAntiAlias(value);
        // 重新刷新自己
        invalidateSelf();
    }

    @Override
    public Bitmap getBitmap() {
        return mBitmap;
    }
}
