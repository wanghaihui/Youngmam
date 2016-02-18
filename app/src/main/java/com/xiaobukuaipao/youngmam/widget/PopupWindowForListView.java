package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-16.
 */
public abstract class PopupWindowForListView<T> extends PopupWindow {

    protected Context context;
    protected View mContentView;

    /**
     * ListView的数据集
     */
    protected List<T> mDatas;

    public PopupWindowForListView(View contentView, int width, int height, boolean focusable) {
        this(contentView, width, height, focusable, null);
    }

    public PopupWindowForListView(View contentView, int width, int height, boolean focusable, List<T> mDatas) {
        this(contentView, width, height, focusable, mDatas, new Object[0]);
    }

    public PopupWindowForListView(View contentView, int width, int height, boolean focusable,
                                  List<T> mDatas, Object... params) {
        super(contentView, width, height, focusable);

        this.mContentView = contentView;
        context = contentView.getContext();

        if (mDatas != null) {
            this.mDatas = mDatas;
        }

        if (params != null && params.length > 0) {
            beforeInitWeNeedSomeParams(params);
        }

        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initViews();
        initEvents();
        init();
    }

    protected abstract void beforeInitWeNeedSomeParams(Object... params);
    public abstract void initViews();
    public abstract void initEvents();
    public abstract void init();

    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }
}
