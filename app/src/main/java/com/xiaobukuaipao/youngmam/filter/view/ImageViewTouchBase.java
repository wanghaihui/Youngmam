package com.xiaobukuaipao.youngmam.filter.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.xiaobukuaipao.youngmam.filter.easing.Cubic;
import com.xiaobukuaipao.youngmam.filter.easing.Easing;
import com.xiaobukuaipao.youngmam.filter.graphics.FastBitmapDrawable;

/**
 * Created by xiaobu1 on 15-9-12.
 * Base View to manage image zoom(放大/缩小)/scroll(滚动)/pinch(挤/捏) operations
 */
public abstract class ImageViewTouchBase extends ImageView implements IDisposable {

    public static final String TAG = ImageViewTouchBase.class.getSimpleName();

    public interface OnDrawableChangeListener {
        /**
         * Callback invoked when a new drawable has been assigned to the view
         * @param drawable
         */
        void onDrawableChanged(Drawable drawable);
    }

    public interface OnLayoutChangeListener {
        /**
         * Callback invoked when the layout bounds(界限) changed
         * @param changed
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        void onLayoutChanged(boolean changed, int left, int top, int right, int bottom);
    }

    /**
     * Use this to change the ImageViewTouchBase#setDisplayType(DisplayType) of this view
     */
    public enum DisplayType {
        // Image is not scaled by default
        NONE,
        // Image will be always presented using this view's bounds
        FIT_TO_SCREEN,
        // Image will be scaled only if bigger than the bounds of this view
        FIT_IF_BIGGER
    }

    // Zoom不合法--Zoom(放大/缩小)
    public static final float ZOOM_INVALID = -1f;

    protected Easing mEasing = new Cubic();
    // 基本的Matrix
    protected Matrix mBaseMatrix = new Matrix();
    // Support矩阵--支持矩阵
    protected Matrix mSuppMatrix = new Matrix();
    // 下个矩阵
    protected Matrix mNextMatrix;

    protected Handler mHandler = new Handler();
    // 布局线程
    protected Runnable mLayoutRunnable = null;
    // 用户是否放大或者缩小
    protected boolean mUserScaled = false;

    private float mMaxZoom = ZOOM_INVALID;
    private float mMinZoom = ZOOM_INVALID;

    // true when min and max zoom are explicitly(明确的) defined
    private boolean mMaxZoomDefined;
    private boolean mMinZoomDefined;

    // 显示矩阵
    protected final Matrix mDisplayMatrix = new Matrix();
    // 矩阵赋值
    protected final float[] mMatrixValues = new float[9];

    private int mThisWidth = -1;
    private int mThisHeight = -1;
    // 中心
    private PointF mCenter = new PointF();

    // 显示--放大--类型
    protected DisplayType mScaleType = DisplayType.NONE;

    // Scale类型是否改变
    private boolean mScaleTypeChanged;
    // Bitmap是否改变
    private boolean mBitmapChanged;

    // 缺省的动画持续时间
    final protected int DEFAULT_ANIMATION_DURATION = 200;

    protected RectF mBitmapRect = new RectF();
    protected RectF mCenterRect = new RectF();
    protected RectF mScrollRect = new RectF();

    private OnDrawableChangeListener mDrawableChangeListener;

    public void setOnDrawableChangedListener(OnDrawableChangeListener listener) {
        mDrawableChangeListener = listener;
    }

    private OnLayoutChangeListener   mOnLayoutChangeListener;

    public void setOnLayoutChangeListener(OnLayoutChangeListener listener) {
        mOnLayoutChangeListener = listener;
    }

    public ImageViewTouchBase(Context context) {
        this(context, null);
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    protected void init(Context context, AttributeSet attrs, int defStyle) {
        setScaleType(ScaleType.MATRIX);
    }

    /**
     * 设置缩放类型--只能通过矩阵来缩放
     * @param scaleType
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.d(TAG, "Unsupported ScaleType. Only Matrix can be used");
        }
    }

    /**
     * Clear the current drawable -- 清楚当前的drawable
     */
    public void clear() {
        setImageBitmap(null);
    }

    /**
     * 设置显示类型
     * @param type
     */
    public void setDisplayType(DisplayType type) {
        if (type != mScaleType) {
            mUserScaled = false;
            mScaleType = type;
            mScaleTypeChanged = true;
            // 此时, 请求重新布局
            requestLayout();
        }
    }

    /**
     * 得到显示类型
     */
    public DisplayType getDisplayType() {
        return mScaleType;
    }

    /**
     * 设置缩小值
     * @param value
     */
    protected void setMinScale(float value) {
        mMinZoom = value;
    }

    /**
     * 设置放大值
     * @param value
     */
    protected void setMaxScale(float value) {
        mMaxZoom = value;
    }

    /**
     * 最关键的点--布局--研究如何真正的布局
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int deltaX = 0;
        int deltaY = 0;

        /**
         * 基本数据
         */
        if (changed) {
            int oldw = mThisWidth;
            int oldh = mThisHeight;

            mThisWidth = right - left;
            mThisHeight = bottom - top;

            deltaX = mThisWidth - oldw;
            deltaY = mThisHeight - oldh;

            // update center point
            mCenter.x = mThisWidth / 2f;
            mCenter.y = mThisHeight / 2f;
        }

        Runnable r = mLayoutRunnable;
        if (r != null) {
            mLayoutRunnable = null;
            // 执行布局优先
            r.run();
        }

        final Drawable drawable = getDrawable();

        if (drawable != null) {

            if (changed || mScaleTypeChanged || mBitmapChanged) {
                // 比例为1
                float scale = 1;
                // retrieve the old values
                float oldDefaultScale = getDefaultScale(mScaleType);
                float oldMatrixScale = getScale(mBaseMatrix);
                float oldScale = getScale();
                float oldMinScale = Math.min(1f, 1f / oldMatrixScale);

                // 得到合适的基础矩阵
                getProperBaseMatrix(drawable, mBaseMatrix);
                // 得到新的矩阵Scale
                float newMatrixScale = getScale(mBaseMatrix);

                // 1. bitmap changed or scaletype changed
                if (mBitmapChanged || mScaleTypeChanged) {

                    if (mNextMatrix != null) {
                        mSuppMatrix.set(mNextMatrix);
                        mNextMatrix = null;
                        // 得到新矩阵的scale
                        scale = getScale();
                    } else {
                        mSuppMatrix.reset();
                        scale = getDefaultScale(mScaleType);
                    }

                    // 设置Image的操作矩阵
                    setImageMatrix(getImageViewMatrix());

                    if (scale != getScale()) {
                        // 放大或者缩小
                        zoomTo(scale);
                    }

                } else if (changed) {
                    // 2. layout size changed
                    if (!mMinZoomDefined) {
                        mMinZoom = ZOOM_INVALID;
                    }

                    if (!mMaxZoomDefined)
                        mMaxZoom = ZOOM_INVALID;

                    setImageMatrix(getImageViewMatrix());
                    postTranslate(-deltaX, -deltaY);

                    if (!mUserScaled) {
                        scale = getDefaultScale(mScaleType);
                        zoomTo(scale);
                    } else {
                        if (Math.abs(oldScale - oldMinScale) > 0.001) {
                            scale = (oldMatrixScale / newMatrixScale) * oldScale;
                        }
                        zoomTo(scale);
                    }
                }

                mUserScaled = false;

                if (scale > getMaxScale() || scale < getMinScale()) {
                    // if current scale if outside the min/max bounds
                    // then restore the correct scale
                    zoomTo(scale);
                }

                center(true, true);

                if (mBitmapChanged) {
                    onDrawableChanged(drawable);
                }

                if (changed || mBitmapChanged || mScaleTypeChanged) {
                    onLayoutChanged(left, top, right, bottom);
                }

                if (mScaleTypeChanged) {
                    mScaleTypeChanged = false;
                }

                if (mBitmapChanged) {
                    mBitmapChanged = false;
                }

            }
        } else {
            // drawable is null
            if (mBitmapChanged) {
                onDrawableChanged(drawable);
            }

            if (changed || mBitmapChanged || mScaleTypeChanged) {
                onLayoutChanged(left, top, right, bottom);
            }

            if (mBitmapChanged) {
                mBitmapChanged = false;
            }

            if (mScaleTypeChanged) {
                mScaleTypeChanged = false;
            }
        }
    }

    /**
     * 得到缺省的Scale
     */
    protected float getDefaultScale(DisplayType type) {
        if (type == DisplayType.FIT_TO_SCREEN) {
            // Always fit to screen
            return 1f;
        } else if (type == DisplayType.FIT_IF_BIGGER) {
            // Normal scale if smaller, fit to screen otherwise
            return Math.min(1f, 1f / getScale(mBaseMatrix));
        } else {
            // No scale
            return 1f / getScale(mBaseMatrix);
        }
    }

    /**
     * Set the new image to display and reset the internal matrix(重置内部矩阵).
     *
     * @param bitmap the Bitmap to display
     *
     * @see {@link ImageView#setImageBitmap(Bitmap)}
     */
    @Override
    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, null, ZOOM_INVALID, ZOOM_INVALID);
    }

    /**
     * @see #setImageDrawable(Drawable, Matrix, float, float)
     * @param bitmap
     * @param matrix
     * @param minZoom
     * @param maxZoom
     */
    public void setImageBitmap(final Bitmap bitmap, Matrix matrix, float minZoom, float maxZoom) {
        if (bitmap != null) {
            setImageDrawable(new FastBitmapDrawable(bitmap), matrix, minZoom, maxZoom);
        } else {
            setImageDrawable(null, matrix, minZoom, maxZoom);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setImageDrawable(drawable, null, ZOOM_INVALID, ZOOM_INVALID);
    }

    /**
     * Note: if the scaleType is FitToScreen then minZoom must be <= 1 and maxZoom must be >= 1
     *
     * @param drawable the new drawable
     * @param initialMatrix the optional initial display matrix
     * @param minZoom the optional minimum scale, pass ZOOM_INVALID to use the default minZoom
     * @param maxZoom the optional maximum scale, pass ZOOM_INVALID to use the default maxZoom
     */
    public void setImageDrawable(final Drawable drawable, final Matrix initialMatrix,
                                 final float minZoom, final float maxZoom) {
        final int viewWidth = getWidth();

        if (viewWidth < 0) {
            /**
             * 也就是说, 此时没有布局成功
             */
            mLayoutRunnable = new Runnable() {
                @Override
                public void run() {
                    setImageDrawable(drawable, initialMatrix, minZoom, maxZoom);
                }
            };
            return;
        }

        _setImageDrawable(drawable, initialMatrix, minZoom, maxZoom);
    }

    protected void _setImageDrawable(final Drawable drawable, final Matrix initialMatrix,
                                     float minZoom, float maxZoom) {

        if (drawable != null) {
            super.setImageDrawable(drawable);
        } else {
            // 复位
            mBaseMatrix.reset();
            // 清空
            super.setImageDrawable(null);
        }

        if (minZoom != ZOOM_INVALID && maxZoom != ZOOM_INVALID) {

            minZoom = Math.min(minZoom, maxZoom);
            maxZoom = Math.max(minZoom, maxZoom);

            // 缩小倍数
            mMinZoom = minZoom;
            // 放大倍数
            mMaxZoom = maxZoom;

            mMinZoomDefined = true;
            mMaxZoomDefined = true;

            if (mScaleType == DisplayType.FIT_TO_SCREEN || mScaleType == DisplayType.FIT_IF_BIGGER) {

                if (mMinZoom >= 1) {
                    mMinZoomDefined = false;
                    mMinZoom = ZOOM_INVALID;
                }

                if (mMaxZoom <= 1) {
                    mMaxZoomDefined = true;
                    mMaxZoom = ZOOM_INVALID;
                }
            }
        } else {
            mMinZoom = ZOOM_INVALID;
            mMaxZoom = ZOOM_INVALID;

            mMinZoomDefined = false;
            mMaxZoomDefined = false;
        }

        if (initialMatrix != null) {
            mNextMatrix = new Matrix(initialMatrix);
        }

        mBitmapChanged = true;

        // 重新布局
        requestLayout();
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    /**
     * Returns the current image scale
     */
    public float getScale() {
        return getScale(mSuppMatrix);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * Setup the base matrix, so that the image is centered and scaled properly.
     *
     * @param drawable
     * @param matrix
     */
    protected void getProperBaseMatrix(Drawable drawable, Matrix matrix) {
        float viewWidth = mThisWidth;
        float viewHeight = mThisHeight;

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();

        float widthScale, heightScale;

        matrix.reset();

        widthScale = viewWidth / w;
        heightScale = viewHeight / h;
        float scale = Math.min(widthScale, heightScale);
        // setScale(sx,sy), 首先会将该Matrix设置为对角矩阵, 即相当于调用reset()方法, 然后在设置该Matrix的MSCALE_X和MSCALE_Y直接设置为sx,sy的值
        // preScale(sx,sy), 不会重置Matrix, 而是直接与Matrix之前的MSCALE_X和MSCALE_Y值结合起来(相乘), M' = M * S(sx, sy)
        // postScale(sx,sy), 不会重置Matrix, 而是直接与Matrix之前的MSCALE_X和MSCALE_Y值结合起来(相乘), M' = S(sx, sy) * M
        // 缩放
        matrix.postScale(scale, scale);

        float tw = (viewWidth - w * scale) / 2.0f;
        float th = (viewHeight = h * scale) / 2.0f;

        // 平移
        matrix.postTranslate(tw, th);
    }

    /**
     * Returns the current view matrix
     */
    public Matrix getImageViewMatrix() {
        return getImageViewMatrix(mSuppMatrix);
    }

    public Matrix getImageViewMatrix(Matrix supportMatrix) {
        mDisplayMatrix.set(mBaseMatrix);
        // 增补矩阵
        mDisplayMatrix.postConcat(supportMatrix);
        return mDisplayMatrix;
    }

    /**
     * 执行Zoom
     * @param scale
     */
    protected void zoomTo(float scale) {

        if (scale > getMaxScale())
            scale = getMaxScale();
        if (scale < getMinScale())
            scale = getMinScale();

        PointF center = getCenter();
        zoomTo(scale, center.x, center.y);
    }

    /**
     * Scale to the target scale
     *
     * @param scale the target zoom
     * @param durationMs the animation duration
     */
    public void zoomTo(float scale, float durationMs) {
        PointF center = getCenter();
        zoomTo(scale, center.x, center.y, durationMs);
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > getMaxScale()) {
            scale = getMaxScale();
        }

        float oldScale = getScale();
        // Scale变量
        float deltaScale = scale / oldScale;

        postScale(deltaScale, centerX, centerY);
        onZoom(getScale());
        center(true, true);
    }

    /**
     * Returns the current maximum allowed image scale
     */
    public float getMaxScale() {
        if (mMaxZoom == ZOOM_INVALID) {
            mMaxZoom = computeMaxZoom();
        }
        return mMaxZoom;
    }

    /**
     * 计算最大的Zoom比例
     */
    protected float computeMaxZoom() {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return 1f;
        }

        float fw = (float) drawable.getIntrinsicWidth() / (float) mThisWidth;
        float fh = (float) drawable.getIntrinsicHeight() / (float) mThisHeight;
        float scale = Math.max(fw, fh) * 8;

        return scale;
    }

    /**
     * Returns the current minimum allowed image scale
     */
    public float getMinScale() {
        if (mMinZoom == ZOOM_INVALID) {
            mMinZoom = computeMinZoom();
        }
        return mMinZoom;
    }

    protected float computeMinZoom() {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return 1f;
        }

        float scale = getScale(mBaseMatrix);
        scale = Math.min(1f, 1f / scale);

        return scale;
    }

    /**
     * 得到中心
     */
    public PointF getCenter() {
        return mCenter;
    }

    protected void postScale(float scale, float centerX, float centerY) {
        mSuppMatrix.postScale(scale, scale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
    }

    /**
     *  用于子类扩展
     * @param scale
     */
    protected void onZoom(float scale) {

    }

    /**
     * 中心
     * @param horizontal
     * @param vertical
     */
    public void center(boolean horizontal, boolean vertical) {
        final Drawable drawable = getDrawable();
        if (drawable == null)
            return;

        RectF rect = getCenter(mSuppMatrix, horizontal, vertical);

        if (rect.left != 0 || rect.top != 0) {
            postTranslate(rect.left, rect.top);
        }
    }

    protected RectF getCenter(Matrix supportMatrix, boolean horizontal, boolean vertical) {

        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return new RectF(0, 0, 0, 0);
        }

        mCenterRect.set(0, 0, 0, 0);

        RectF rect = getBitmapRect(supportMatrix);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int viewHeight = mThisHeight;

            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = mThisHeight - rect.bottom;
            }

        }

        if (horizontal) {
            int viewWidth = mThisWidth;
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }
        mCenterRect.set(deltaX, deltaY, 0, 0);

        return mCenterRect;
    }

    protected RectF getBitmapRect(Matrix supportMatrix) {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return null;
        }
        Matrix m = getImageViewMatrix(supportMatrix);
        mBitmapRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        m.mapRect(mBitmapRect);

        return mBitmapRect;
    }

    public void postTranslate(float deltaX, float deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            mSuppMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(getImageViewMatrix());
        }
    }

    protected void zoomTo(float scale, float centerX, float centerY, final float durationMs) {
        if (scale > getMaxScale())
            scale = getMaxScale();

        final long startTime = System.currentTimeMillis();
        final float oldScale = getScale();

        final float deltaScale = scale - oldScale;

        Matrix m = new Matrix(mSuppMatrix);
        m.postScale(scale, scale, centerX, centerY);
        RectF rect = getCenter(m, true, true);

        final float destX = centerX + rect.left * scale;
        final float destY = centerY + rect.top * scale;

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float newScale = (float) mEasing.easeInOut(currentMs, 0, deltaScale, durationMs);
                zoomTo(oldScale + newScale, destX, destY);
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    center(true, true);
                    onZoomAnimationCompleted(getScale());
                }
            }
        });
    }

    // 子类扩展
    protected void onZoomAnimationCompleted(float scale) {

    }

    /**
     * Fired as soon as a new Bitmap has been set
     *
     * @param drawable
     */
    protected void onDrawableChanged(final Drawable drawable) {
        fireOnDrawableChangeListener(drawable);
    }

    protected void fireOnLayoutChangeListener(int left, int top, int right, int bottom) {
        if (null != mOnLayoutChangeListener) {
            mOnLayoutChangeListener.onLayoutChanged(true, left, top, right, bottom);
        }
    }

    protected void fireOnDrawableChangeListener(Drawable drawable) {
        if (null != mDrawableChangeListener) {
            mDrawableChangeListener.onDrawableChanged(drawable);
        }
    }

    /**
     * Called just after {@link #onLayout(boolean, int, int, int, int)}
     * if the view's bounds has changed or a new Drawable has been set
     * or the {@link DisplayType} has been modified
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected void onLayoutChanged(int left, int top, int right, int bottom) {
        fireOnLayoutChangeListener(left, top, right, bottom);
    }

    @Override
    public void dispose() {
        clear();
    }

    public void resetDisplay() {
        mBitmapChanged = true;
        requestLayout();
    }

    public void resetMatrix() {
        mSuppMatrix = new Matrix();

        float scale = getDefaultScale(mScaleType);
        setImageMatrix(getImageViewMatrix());

        if (scale != getScale()) {
            zoomTo(scale);
        }

        postInvalidate();
    }

    @Override
    public void setImageResource(int resId) {
        setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    @Override
    public void setImageMatrix(Matrix matrix) {

        Matrix current = getImageMatrix();
        boolean needUpdate = false;

        if (matrix == null && !current.isIdentity() || matrix != null && !current.equals(matrix)) {
            needUpdate = true;
        }

        super.setImageMatrix(matrix);

        if (needUpdate) {
            onImageMatrixChanged();
        }
    }

    /**
     * Called just after a new Matrix has been assigned.
     *
     * @see {@link #setImageMatrix(Matrix)}
     */
    protected void onImageMatrixChanged() {
    }

    /**
     * Returns the current image display matrix.<br />
     * This matrix can be used in the next call to the {@link #setImageDrawable(Drawable, Matrix, float, float)} to restore the same
     * view state of the previous {@link Bitmap}.<br />
     * Example:
     *
     * <pre>
     * Matrix currentMatrix = mImageView.getDisplayMatrix();
     * mImageView.setImageBitmap( newBitmap, currentMatrix, ZOOM_INVALID, ZOOM_INVALID );
     * </pre>
     *
     * @return the current support matrix
     */
    public Matrix getDisplayMatrix() {
        return new Matrix(mSuppMatrix);
    }

    public RectF getBitmapRect() {
        return getBitmapRect(mSuppMatrix);
    }

    @SuppressLint("Override")
    public float getRotation() {
        return 0;
    }

    public float getBaseScale() {
        return getScale(mBaseMatrix);
    }

    /**
     * Scrolls the view by the x and y amount
     *
     * @param x
     * @param y
     */
    public void scrollBy(float x, float y) {
        panBy(x, y);
    }

    protected void panBy(double dx, double dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set((float) dx, (float) dy, 0, 0);
        updateRect(rect, mScrollRect);
        // FIXME 贴纸移动到边缘次数多了以后会爆, 原因不明朗, 后续需要好好重写ImageViewTouch
        postTranslate(mScrollRect.left, mScrollRect.top);
        center(true, true);
    }

    protected void updateRect(RectF bitmapRect, RectF scrollRect) {
        if (bitmapRect == null)
            return;

        if (bitmapRect.top >= 0 && bitmapRect.bottom <= mThisHeight)
            scrollRect.top = 0;
        if (bitmapRect.left >= 0 && bitmapRect.right <= mThisWidth)
            scrollRect.left = 0;
        if (bitmapRect.top + scrollRect.top >= 0 && bitmapRect.bottom > mThisHeight)
            scrollRect.top = (int) (0 - bitmapRect.top);
        if (bitmapRect.bottom + scrollRect.top <= (mThisHeight - 0) && bitmapRect.top < 0)
            scrollRect.top = (int) ((mThisHeight - 0) - bitmapRect.bottom);
        if (bitmapRect.left + scrollRect.left >= 0)
            scrollRect.left = (int) (0 - bitmapRect.left);
        if (bitmapRect.right + scrollRect.left <= (mThisWidth - 0))
            scrollRect.left = (int) ((mThisWidth - 0) - bitmapRect.right);
    }

    public void scrollBy(float distanceX, float distanceY, final double durationMs) {
        final double dx = distanceX;
        final double dy = distanceY;
        final long startTime = System.currentTimeMillis();
        mHandler.post(new Runnable() {

            double old_x = 0;
            double old_y = 0;

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double currentMs = Math.min(durationMs, now - startTime);
                double x = mEasing.easeOut(currentMs, 0, dx, durationMs);
                double y = mEasing.easeOut(currentMs, 0, dy, durationMs);
                panBy((x - old_x), (y - old_y));
                old_x = x;
                old_y = y;
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    RectF centerRect = getCenter(mSuppMatrix, true, true);
                    if (centerRect.left != 0 || centerRect.top != 0)
                        scrollBy(centerRect.left, centerRect.top);
                }
            }
        });
    }
}
