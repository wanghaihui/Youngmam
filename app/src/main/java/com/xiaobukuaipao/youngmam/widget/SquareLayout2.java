package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by xiaobu1 on 15-7-28.
 */
public class SquareLayout2 extends LinearLayout {
    private static final String TAG = SquareLayout2.class.getSimpleName();

    public SquareLayout2(Context context) {
        super(context);
    }

    public SquareLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec), height = MeasureSpec.getSize(heightMeasureSpec);
        final ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT && lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(height, height);
        } else if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT && lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(width, width);
        } else {
            if (width > height) {
                super.onMeasure(heightMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(height, height);
            } else {
                super.onMeasure(widthMeasureSpec, widthMeasureSpec);
                setMeasuredDimension(width, width);
            }
        }
    }

    /**
     * 保留
     */
    /*public void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;

        if (width >= height) {
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.height = width;
            this.setLayoutParams(params);
            this.setMeasuredDimension(width, width);
            super.onLayout(changed, l, t, r, t + width);
        } else {
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.width = height;
            this.setLayoutParams(params);
            this.setMeasuredDimension(height, height);
            super.onLayout(changed, l, t, l + height, b);
        }
    }*/
}
