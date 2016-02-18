package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by xiaobu1 on 15-4-27.
 */
public class ContentHeightViewPager extends ViewPager {
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

    public ContentHeightViewPager(Context context) {
        this(context, null);
    }

    public ContentHeightViewPager(Context context, AttributeSet attrs) {
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) {
                //采用最大的view的高度
                height = h;
            }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
