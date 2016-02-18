package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-7-8.
 */
public class SpecialCardView extends LinearLayout {

    public SpecialCardView(Context context) {
        this(context, null);
    }

    public SpecialCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化ActionBar
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // 竖直排列
        setOrientation(VERTICAL);
        // 设置
        setBackgroundResource(R.color.white);

        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, context.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp), 0, 0);
        setLayoutParams(params);

        setPadding(0, 0, 0, context.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp));
    }
}
