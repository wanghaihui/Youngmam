package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 正方形的View
 */
final public class SquaredImageView extends ImageView {

	public SquaredImageView(Context context) {
	    super(context);
	}

	public SquaredImageView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 设置测量的尺寸
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

}
