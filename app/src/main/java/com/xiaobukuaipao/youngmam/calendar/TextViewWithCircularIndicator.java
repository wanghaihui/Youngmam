package com.xiaobukuaipao.youngmam.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-5-8.
 */
public class TextViewWithCircularIndicator extends TextView {
    // 圆圈的颜色
    private final int mCircleColor;
    private Paint mCirclePaint = new Paint();
    private boolean mDrawCircle;
    private final String mItemIsSelectedText;
    private final int mRadius;

    public TextViewWithCircularIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        Resources res = context.getResources();
        mCircleColor = res.getColor(R.color.color_ff4c51);
        mRadius = res.getDimensionPixelOffset(R.dimen.month_select_circle_radius);
        mItemIsSelectedText = context.getResources().getString(R.string.item_is_selected);

        init();
    }

    private void init() {
        // 使用TextPaint的仿“粗体”设置setFakeBoldText为true
        mCirclePaint.setFakeBoldText(true);
        // 抗锯齿
        mCirclePaint.setAntiAlias(true);
        // 设置颜色
        mCirclePaint.setColor(mCircleColor);

        // 设置文本对齐--居中对齐
        mCirclePaint.setTextAlign(Paint.Align.CENTER);
        // 实心还是空心--设置实心
        mCirclePaint.setStyle(Paint.Style.FILL);
        // 设置透明度
        mCirclePaint.setAlpha(80);
    }

    public void drawIndicator(boolean drawIndicator) {
        mDrawCircle = drawIndicator;
    }

    /**
     * 得到内容描述
     */
    public CharSequence getContentDescription() {
        CharSequence text = getText();

        if (mDrawCircle) {
            // 格式化
            text = String.format(mItemIsSelectedText, text);
        }

        return text;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawCircle) {
            int width = getWidth();
            int height = getHeight();
            // 半径
            int radius = Math.min(width, height) / 2;
            // 画圆
            canvas.drawCircle(width/2, height/2, radius, mCirclePaint);
        }
    }

}
