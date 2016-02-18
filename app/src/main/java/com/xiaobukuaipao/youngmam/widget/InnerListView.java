package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by xiaobu1 on 15-6-25.
 */
public class InnerListView extends ListView {
    private static final String TAG = InnerListView.class.getSimpleName();

    private ScrollView parentScrollView;
    private int maxHeight;

    private View placeholderView;

    private int stickyScrollY;

    private boolean isTop = false;

    // 是否向上滚动
    private boolean mIsScrollToUp = false;

    boolean mDisallow;
    private float mLastMotionY;
    private float mStartMotionY = 0;
    private int mTouchSlop;
    private boolean mDragging = false;

    /**
     * 当前的Y值
     */
    private int currentY;

    public ScrollView getParentScrollView() {
        return parentScrollView;
    }

    public void setParentScrollView(ScrollView parentScrollView) {
        this.parentScrollView = parentScrollView;
        init();
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

    public InnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {

        /*setOnScrollListener(new OnScrollListener() {

            private int oldTop;
            private int oldFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    isTop = true;
                }

                if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    // 滑动底部
                }

                int top = (view.getChildAt(0) == null) ? 0 : view.getChildAt(0).getTop();

                if (firstVisibleItem == oldFirstVisibleItem) {
                    if (top > oldTop) {
                        mIsScrollToUp = true;
                    } else if (top < oldTop) {
                        mIsScrollToUp = false;
                    }
                } else {
                    if (firstVisibleItem < oldFirstVisibleItem) {
                        mIsScrollToUp = true;
                    } else {
                        mIsScrollToUp = false;
                    }
                }

                oldTop = top;
                oldFirstVisibleItem = firstVisibleItem;
            }

        });*/

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

            /*int first = getFirstVisiblePosition();
            View firstView = null;

            if (isTop && mIsScrollToUp && (first == 0) && ((firstView = getChildAt(0)) != null) && firstView.getTop() == 0) {
                setParentScrollAble(true);
                mIsScrollToUp = false;
            } else {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setParentScrollAble(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setParentScrollAble(false);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        setParentScrollAble(true);
                        break;

                    default:
                        break;
                }
            }*/

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

        } else {
            setParentScrollAble(true);
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
        return -top + firstVisiblePosition * c.getHeight();
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (stickyScrollY >= placeholderView.getTop()) {

            mDisallow = true;
            if (mDragging) {
                float slop = mStartMotionY - mLastMotionY;
                if (Math.abs(slop) > mTouchSlop) {
                    if (slop < 0) {
                        int first = getFirstVisiblePosition();
                        View firstView = null;
                        if ((first == 0) && ((firstView = getChildAt(0)) != null)) {
                            if (firstView.getTop() == 0) {
                                mDisallow = false;
                            }
                        }
                    }
                }
            }

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionY = ev.getY();
                    mStartMotionY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mLastMotionY = ev.getY();
                    if (!mDragging) {
                        mDragging = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDragging = false;
                    break;
            }

            if (parentScrollView != null) {
                parentScrollView.requestDisallowInterceptTouchEvent(mDisallow);
            }

        }

        return super.dispatchTouchEvent(ev);
    }*/


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (stickyScrollY >= placeholderView.getTop()) {

            if (parentScrollView != null) {

                if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                    int itemCount = getAdapter().getCount();

                    View child = getChildAt(0);
                    int height = child.getMeasuredHeight() * itemCount;
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
