package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.font.FontEditText;

/**
 * Created by xiaobu1 on 15-5-6.
 */
public class SearchView1 extends FontEditText implements View.OnFocusChangeListener {

    private Context context;
    private Drawable searchImg;
    private Drawable ableImg;

    public SearchView1(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SearchView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SearchView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        searchImg = context.getResources().getDrawable(R.mipmap.general_search);
        ableImg = context.getResources().getDrawable(R.drawable.btn_search_delete);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });

        setDrawable();
    }

    private void setDrawable() {
        if (length() < 1) {
            setCompoundDrawablesWithIntrinsicBounds(searchImg, null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(searchImg, null, ableImg, null);
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ableImg != null && event.getAction() == MotionEvent.ACTION_UP) {

            int x = (int) event.getX();
            // 判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) && (x < (getWidth() - getPaddingRight()));

            // 获取删除图标的边界,返回一个Rect对象
            Rect rect = ableImg.getBounds();
            // 获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            // 计算图标底部到控件底部的距离
            int distance = (getHeight() - height) / 2;

            // 判断触摸点是否在竖直范围内(可能会有点误差)
            // 触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));

            if (isInnerWidth && isInnerHeight) {
                setText("");
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setDrawable();
        } else {
            setCompoundDrawablesWithIntrinsicBounds(searchImg, null, null, null);
        }
    }
}
