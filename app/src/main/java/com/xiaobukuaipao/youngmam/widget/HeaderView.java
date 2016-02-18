package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class HeaderView extends LinearLayout {

    private Context context;

    private FrameLayout mImageLayout;

    private LinearLayout mTextLayout;

    private RelativeLayout mFloatLayout;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化ActionBar
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        // 竖直排列
        setOrientation(VERTICAL);
        // 设置
        setBackgroundResource(R.color.color_basic_bg);
        // 上
        setImageLayout();
        // 中
        setTextLayout();
        // 下
        setFloatLayout();
    }

    private void setImageLayout() {
        mImageLayout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        mImageLayout.setLayoutParams(params);

        mImageLayout.setBackgroundResource(R.color.white);

        addView(mImageLayout);
    }

    private void setTextLayout() {
        mTextLayout = new LinearLayout(context);
        mTextLayout.setPadding(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_20dp));

        mTextLayout.setBackgroundResource(R.color.white);

        mTextLayout.setOrientation(VERTICAL);

        addView(mTextLayout);
    }

    private void setFloatLayout() {
        mFloatLayout = new RelativeLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                0,
                0);
        mFloatLayout.setLayoutParams(params);

        mFloatLayout.setBackgroundResource(R.color.white);
        addView(mFloatLayout);
    }

    public FrameLayout getImageLayout() {
        return mImageLayout;
    }

    public LinearLayout getTextLayout() {
        return mTextLayout;
    }

    public RelativeLayout getFloatLayout() {
        return mFloatLayout;
    }
}
