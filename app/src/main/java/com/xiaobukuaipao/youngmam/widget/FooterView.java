package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-5-16.
 */
public class FooterView extends LinearLayout {

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setPadding(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp));
    }

    public void setContentView(View view) {
        addView(view);
    }
}
