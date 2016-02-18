package com.xiaobukuaipao.youngmam.filter.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;

/**
 * Created by xiaobu1 on 15-9-16.
 */
public class ImageViewTouch extends ImageViewTouchBase {

    // 包内可用
    static final float SCROLL_DELTA_THRESHOLD = 1.0f;

    // 放大缩小手势检测器
    protected ScaleGestureDetector mScaleDetector;
    // 手势检测器
    // Detects various gestures and events using the supplied MotionEvents
    // The GestureDetector.OnGestureListener callback will notify users when a particular motion event has occurred
    // This class should only be used with MotionEvents reported via touch (don't use for trackball events)
    protected GestureDetector mGestureDetector;

    protected int mTouchSlop;
    // 缩放因子
    protected float mScaleFactor;
    // 双击方向
    protected int mDoubleTapDirection;

    // 手势监听接口
    protected OnGestureListener mGestureListener;
    // 缩放手势监听接口
    protected OnScaleGestureListener mScaleListener;

    // 是否能够双击
    protected boolean mDoubleTapEnabled = true;
    // 是否能够缩放
    protected boolean mScaleEnabled = true;
    // 是否能够滚动
    protected boolean mScrollEnabled = true;

    // 双击接口
    private OnImageViewTouchDoubleTapListener mDoubleTapListener;
    // 单击接口
    private OnImageViewTouchSingleTapListener mSingleTapListener;
    // 放大/缩小--动画
    private OnZoomAnimationListener           onZoomAnimationListener;

    public ImageViewTouch(Context context) {
        super(context);
    }

    public ImageViewTouch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewTouch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);

        // Touch距离--触发Scale的最小距离
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mGestureListener = getGestureListener();
        mScaleListener = getScaleListener();

        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);

        mDoubleTapDirection = 1;
    }

    public void setDoubleTapListener(OnImageViewTouchDoubleTapListener listener) {
        mDoubleTapListener = listener;
    }

    public void setSingleTapListener(OnImageViewTouchSingleTapListener listener) {
        mSingleTapListener = listener;
    }

    public void setDoubleTapEnabled(boolean value) {
        mDoubleTapEnabled = value;
    }

    public void setScaleEnabled(boolean value) {
        mScaleEnabled = value;
    }

    public void setScrollEnabled(boolean value) {
        mScrollEnabled = value;
    }

    public boolean getDoubleTapEnabled() {
        return mDoubleTapEnabled;
    }

    /**
     * 得到手势监听器
     */
    protected OnGestureListener getGestureListener() {
        return new GestureListener();
    }

    /**
     * 得到缩放监听器
     */
    protected OnScaleGestureListener getScaleListener() {
        return new ScaleListener();
    }

    @Override
    protected void _setImageDrawable(final Drawable drawable, final Matrix initialMatrix,
                                     float minZoom, float maxZoom) {
        super._setImageDrawable(drawable, initialMatrix, minZoom, maxZoom);
        mScaleFactor = getMaxScale() / 3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                return onUp(event);
        }
        return true;
    }

    public boolean onUp(MotionEvent e) {
        if (getScale() < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
        return true;
    }

    @Override
    protected void onZoomAnimationCompleted(float scale) {
        if (scale < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
        if (onZoomAnimationListener != null) {
            onZoomAnimationListener.onZoomAnimEnd(scale);
        }
    }

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     *
     * @param direction
     *            - positive direction value means scroll from right to left,
     *            negative value means scroll from left to right
     *
     * @return true if there is some more place to scroll, false - otherwise.
     */
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollRect);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);

        if (null == bitmapRect) {
            return false;
        }

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
            }
        }

        double bitmapScrollRectDelta = Math.abs(bitmapRect.left - mScrollRect.left);
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD;
    }

    /**
     * 设置缩放动画监听
     * @param onZoomAnimationListener
     */
    public void setOnZoomAnimationListener(OnZoomAnimationListener onZoomAnimationListener) {
        this.onZoomAnimationListener = onZoomAnimationListener;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (getScale() == 1f)
            return false;
        mUserScaled = true;
        scrollBy(-distanceX, -distanceY);
        invalidate();
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
            mUserScaled = true;
            scrollBy(diffX / 2, diffY / 2, 300);
            invalidate();
            return true;
        }
        return false;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    public boolean onDown(MotionEvent e) {
        return true;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        // Notified when a single-tap occurs
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (null != mSingleTapListener) {
                mSingleTapListener.onSingleTapConfirmed();
            }

            // 单击有效
            // true if the event is consumed, else false
            // 事件被次View消费
            return ImageViewTouch.this.onSingleTapConfirmed(e);
        }

        /**
         * 双击手势
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 首先判断是否允许双击
            if (mDoubleTapEnabled) {
                // 此时, 用户在缩放
                mUserScaled = true;

                float scale = getScale();
                float targetScale = scale;
                targetScale = onDoubleTapPost(scale, getMaxScale());
                targetScale = Math.min(getMaxScale(), Math.max(targetScale, getMinScale()));
                zoomTo(targetScale, e.getX(), e.getY(), DEFAULT_ANIMATION_DURATION);
                // 刷新
                invalidate();
            }

            if (null != mDoubleTapListener) {
                mDoubleTapListener.onDoubleTap();
            }

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isLongClickable()) {
                if (!mScaleDetector.isInProgress()) {
                    setPressed(true);
                    performLongClick();
                }
            }
        }

        // 滑动
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mScrollEnabled)
                return false;
            if (e1 == null || e2 == null)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            return ImageViewTouch.this.onScroll(e1, e2, distanceX, distanceY);
        }

        // 拖动
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!mScrollEnabled)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            if (getScale() == 1f)
                return false;
            return ImageViewTouch.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return ImageViewTouch.this.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return ImageViewTouch.this.onDown(e);
        }
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        protected boolean mScaled = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = getScale() * detector.getScaleFactor();

            if (mScaleEnabled) {
                if (mScaled && span != 0) {
                    mUserScaled = true;
                    targetScale = Math.min(getMaxScale(),
                            Math.max(targetScale, getMinScale() - 0.1f));
                    zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                    mDoubleTapDirection = 1;
                    invalidate();
                    return true;
                }

                // This is to prevent a glitch(过失) the first time image is scaled.
                if (!mScaled)
                    mScaled = true;
            }
            return true;
        }

    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if ((scale + (mScaleFactor * 2)) <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {

        return true;
    }


    public interface OnImageViewTouchDoubleTapListener {
        // 双击
        void onDoubleTap();
    }

    public interface OnImageViewTouchSingleTapListener {
        // 单击确认
        void onSingleTapConfirmed();
    }

    /**
     * 缩放动画监听
     */
    public interface OnZoomAnimationListener {
        // 放大动画结束
        void onZoomAnimEnd(float scale);
    }
}
