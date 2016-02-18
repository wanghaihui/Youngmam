package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.xiaobukuaipao.youngmam.font.FontTextView;

/**
 * Created by xiaobu1 on 15-7-1.
 */
public class MarqueeTextView extends FontTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
