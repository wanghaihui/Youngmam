package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.font.FontToggleButton;

/**
 * Created by xiaobu1 on 15-5-7.
 * 触发器按钮--标签按钮本质
 */
public class LabelView extends FontToggleButton {
    private boolean mCheckEnable = true;

    public LabelView(Context context) {
        super(context);
        init();
    }

    public LabelView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public LabelView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, 0);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setTextOn(null);
        setTextOff(null);
        setText("");
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        setBackgroundResource(R.drawable.label_bg);
    }

    public void setCheckEnable(boolean mCheckEnable) {
        this.mCheckEnable = mCheckEnable;
        if (!this.mCheckEnable) {
            super.setChecked(false);
        }
    }

    public void setChecked(boolean checked) {
        if (this.mCheckEnable) {
            super.setChecked(checked);
        }
    }
}
