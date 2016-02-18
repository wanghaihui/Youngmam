package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.xiaobukuaipao.youngmam.loadmore.GridViewWithHeaderAndFooter;

/**
 * Created by xiaobu1 on 15-6-29.
 */
public class InnerGridView extends GridViewWithHeaderAndFooter {
    private static final String TAG = InnerGridView.class.getSimpleName();

    private ScrollView parentScrollView;
    private int maxHeight;

    private View placeholderView;

    private int stickyScrollY;

    /**
     * 当前的Y值
     */
    private int currentY;

    public ScrollView getParentScrollView() {
        return parentScrollView;
    }

    public void setParentScrollView(ScrollView parentScrollView) {
        this.parentScrollView = parentScrollView;
    }

    public View getPlaceholderView() {
        return placeholderView;
    }

    public void setPlaceholderView(View placeholderView) {
        this.placeholderView = placeholderView;
    }

    public void setStickyScrollY(int stickyScrollY) {
        this.stickyScrollY = stickyScrollY;
    }

    public int getStickyScrollY() {
        return this.stickyScrollY;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public InnerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight > -1) {
            // 先这样
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (stickyScrollY >= placeholderView.getTop()) {
            if (parentScrollView == null) {
                return super.onInterceptTouchEvent(ev);
            } else {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentY = (int) ev.getY();
                        setParentScrollAble(false);
                        return super.onInterceptTouchEvent(ev);
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_CANCEL:
                        setParentScrollAble(true);
                        break;
                }

            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 获取listView的滚动位置，如果直接用getScrollY();一直是0。所以要自己算下<br>
     *
     * @return
     */
    private int getTrueScrollY() {
        View c = getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = getFirstVisiblePosition(); // listView中 当前可视的 第一个item的序号
        int top = c.getTop();
        return -top + ((int) Math.ceil(firstVisiblePosition / 2)) * c.getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (stickyScrollY >= placeholderView.getTop()) {

            if (parentScrollView != null) {

                if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                    int itemCount = getAdapter().getCount();

                    View child = getChildAt(0);
                    int height = child.getMeasuredHeight() * ((int) Math.ceil(itemCount / 2));
                    height = height - getMeasuredHeight();

                    int scrollY = getTrueScrollY();
                    int y = (int) ev.getY();

                    // 手指向下滑动
                    if (currentY < y) {
                        if (scrollY <= 0) {
                            // 如果向下滑动到头，就把滚动交给父Scrollview
                            setParentScrollAble(true);
                            return false;
                        } else {
                            setParentScrollAble(false);

                        }
                    } else if (currentY > y) {
                        if (scrollY >= height) {
                            // 如果向上滑动到头，就把滚动交给父Scrollview
                            setParentScrollAble(true);
                            return false;
                        } else {
                            setParentScrollAble(false);

                        }

                    }
                    currentY = y;
                }
            }

        }

        return super.onTouchEvent(ev);
    }

    private void setParentScrollAble(boolean flag) {
        parentScrollView.requestDisallowInterceptTouchEvent(!flag);
    }
}
