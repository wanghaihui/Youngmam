package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by xiaobu1 on 15-4-17.
 */

/**
 * ViewPager是一个Layout manager, allows the user to flip left and right through pages of data
 */
public class JazzyViewPager extends ViewPager {
    public static final String TAG = "JazzyViewPager";

    private boolean mEnabled = true;
    private boolean mFadeEnabled = false;
    private boolean mOutlineEnabled = false;

    public static int sOutlineColor = Color.WHITE;

    // 转换效果
    private TransitionEffect mEffect = TransitionEffect.Standard;

    private HashMap<Integer, Object> mObjs = new LinkedHashMap<Integer, Object>();

    private static final float SCALE_MAX = 0.5f;
    private static final float ZOOM_MAX = 0.5f;
    private static final float ROT_MAX = 15.0f;

    public enum TransitionEffect {
        Standard,
        Tablet,
        CubeIn,
        CubeOut,
        FlipVertical,
        FlipHorizontal,
        Stack,
        ZoomIn,
        ZoomOut,
        RotateUp,
        RotateDown,
        Accordion
    }

    private static final boolean API_11;

    static {
        API_11 = Build.VERSION.SDK_INT >= 11;
    }

    public JazzyViewPager(Context context) {
        this(context, null);
    }

    public JazzyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setObjectForPosition(Object obj, int position) {
        mObjs.put(Integer.valueOf(position), obj);
    }

    public View findViewFromObject(int position) {
        Object o = mObjs.get(Integer.valueOf(position));
        if (o == null) {
            return null;
        }

        PagerAdapter a = getAdapter();
        View v;
        for (int i=0; i < getChildCount(); i++) {
            v = getChildAt(i);
            if (a.isViewFromObject(v, 0)) {
                return v;
            }
        }

        return null;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
