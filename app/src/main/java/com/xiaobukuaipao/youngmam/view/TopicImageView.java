package com.xiaobukuaipao.youngmam.view;

/**
 * Created by xiaobu1 on 15-6-3.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by xiaobu1 on 15-5-30.
 */
final public class TopicImageView extends ImageView {

    public TopicImageView(Context context) {
        super(context);
    }

    public TopicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 设置测量的尺寸
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 234 / 600);
    }

}
