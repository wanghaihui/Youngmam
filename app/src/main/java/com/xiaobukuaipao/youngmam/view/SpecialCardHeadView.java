package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-7-8.
 */
public class SpecialCardHeadView extends ImageView {

    public SpecialCardHeadView(Context context) {
        this(context, null);
    }

    public SpecialCardHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialCardHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化ActionBar
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, context.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp), 0, 0);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);
    }
}
