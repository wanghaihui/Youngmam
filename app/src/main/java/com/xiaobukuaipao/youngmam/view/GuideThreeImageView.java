package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by xiaobu1 on 15-11-4.
 */
public class GuideThreeImageView extends ImageView {

    public GuideThreeImageView(Context context) {
        super(context);
    }

    public GuideThreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 设置测量的尺寸
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 120 / 380);
    }

}
