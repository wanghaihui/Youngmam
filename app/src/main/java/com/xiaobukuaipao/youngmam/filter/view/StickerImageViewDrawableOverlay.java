package com.xiaobukuaipao.youngmam.filter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.util.Log;

import com.xiaobukuaipao.youngmam.filter.drawable.EditableDrawable;
import com.xiaobukuaipao.youngmam.filter.drawable.FeatherDrawable;
import com.xiaobukuaipao.youngmam.view.LabelView;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xiaobu1 on 15-9-16.
 */
public class StickerImageViewDrawableOverlay extends ImageViewTouch {
    private static final String TAG = StickerImageViewDrawableOverlay.class.getSimpleName();

    public static interface OnDrawableEventListener {

        void onFocusChange(StickerHighlightView newFocus, StickerHighlightView oldFocus);

        void onDown(StickerHighlightView view);

        void onMove(StickerHighlightView view);

        void onClick(StickerHighlightView view);

        //标签的点击事件处理
        void onClick(LabelView label);
    }

    //删除的时候会出错
    private List<StickerHighlightView> mOverlayViews = new CopyOnWriteArrayList<StickerHighlightView>();

    private StickerHighlightView mOverlayView;

    private OnDrawableEventListener mDrawableListener;

    private boolean mForceSingleSelection = true;

    private Paint mDropPaint;

    private Rect mTempRect = new Rect();

    private boolean mScaleWithContent     = false;

    public StickerImageViewDrawableOverlay(Context context) {
        super(context);
        // setScrollEnabled(false);
    }

    public StickerImageViewDrawableOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        // setScrollEnabled(false);
    }

    public StickerImageViewDrawableOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // setScrollEnabled(false);
    }

    protected void panBy(double dx, double dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set((float) dx, (float) dy, 0, 0);
        updateRect(rect, mScrollRect);
        // FIXME 贴纸移动到边缘次数多了以后会爆, 原因不明朗, 后续需要好好重写ImageViewTouch
        // postTranslate(mScrollRect.left, mScrollRect.top);
        center(true, true);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop();
        mGestureDetector.setIsLongpressEnabled(false);
    }

    /**
     * How overlay content will be scaled/moved
     * when zomming/panning the base image
     *
     * @param value true if content will scale according to the image
     */
    public void setScaleWithContent(boolean value) {
        mScaleWithContent = value;
    }

    public boolean getScaleWithContent() {
        return mScaleWithContent;
    }

    /**
     * If true, when the user tap outside the drawable overlay and
     * there is only one active overlay selection is not changed.
     *
     * @param value
     *            the new force single selection
     */
    public void setForceSingleSelection(boolean value) {
        mForceSingleSelection = value;
    }

    public void setOnDrawableEventListener(OnDrawableEventListener listener) {
        mDrawableListener = listener;
    }

    @Override
    public void setImageDrawable(android.graphics.drawable.Drawable drawable,
                                 Matrix initial_matrix, float min_zoom, float max_zoom) {
        super.setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
    }

    @Override
    protected void onLayoutChanged(int left, int top, int right, int bottom) {
        super.onLayoutChanged(left, top, right, bottom);

        if (getDrawable() != null) {

            Iterator<StickerHighlightView> iterator = mOverlayViews.iterator();
            while (iterator.hasNext()) {
                StickerHighlightView view = iterator.next();
                view.getMatrix().set(getImageMatrix());
                view.invalidate();
            }
        }
    }

    @Override
    public void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);

        Iterator<StickerHighlightView> iterator = mOverlayViews.iterator();
        while (iterator.hasNext()) {
            StickerHighlightView view = iterator.next();
            if (getScale() != 1) {
                float[] mvalues = new float[9];
                getImageMatrix().getValues(mvalues);
                final float scale = mvalues[Matrix.MSCALE_X];

                if (!mScaleWithContent)
                    view.getCropRectF().offset(-deltaX / scale, -deltaY / scale);
            }

            view.getMatrix().set(getImageMatrix());
            view.invalidate();
        }
    }

    @Override
    public void postScale(float scale, float centerX, float centerY) {

        if (mOverlayViews.size() > 0) {
            Iterator<StickerHighlightView> iterator = mOverlayViews.iterator();

            Matrix oldMatrix = new Matrix(getImageViewMatrix());
            super.postScale(scale, centerX, centerY);

            while (iterator.hasNext()) {
                StickerHighlightView view = iterator.next();

                if (!mScaleWithContent) {
                    RectF cropRect = view.getCropRectF();
                    RectF rect1 = view.getDisplayRect(oldMatrix, view.getCropRectF());
                    RectF rect2 = view.getDisplayRect(getImageViewMatrix(), view.getCropRectF());

                    float[] mvalues = new float[9];
                    getImageViewMatrix().getValues(mvalues);
                    final float currentScale = mvalues[Matrix.MSCALE_X];

                    cropRect.offset((rect1.left - rect2.left) / currentScale,
                            (rect1.top - rect2.top) / currentScale);
                    cropRect.right += -(rect2.width() - rect1.width()) / currentScale;
                    cropRect.bottom += -(rect2.height() - rect1.height()) / currentScale;

                    view.getMatrix().set(getImageMatrix());
                    view.getCropRectF().set(cropRect);
                } else {
                    view.getMatrix().set(getImageMatrix());
                }
                view.invalidate();
            }
        } else {
            super.postScale(scale, centerX, centerY);
        }
    }

    private void ensureVisible(StickerHighlightView hv, float deltaX, float deltaY) {
        RectF r = hv.getDrawRect();
        int panDeltaX1 = 0, panDeltaX2 = 0;
        int panDeltaY1 = 0, panDeltaY2 = 0;

        if (deltaX > 0)
            panDeltaX1 = (int) Math.max(0, getLeft() - r.left);
        if (deltaX < 0)
            panDeltaX2 = (int) Math.min(0, getRight() - r.right);

        if (deltaY > 0)
            panDeltaY1 = (int) Math.max(0, getTop() - r.top);

        if (deltaY < 0)
            panDeltaY2 = (int) Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        // iterate the items and post a single tap event to the selected item
        Iterator<StickerHighlightView> iterator = mOverlayViews.iterator();
        while (iterator.hasNext()) {
            StickerHighlightView view = iterator.next();
            if (view.isSelected()) {
                view.onSingleTapConfirmed(e.getX(), e.getY());
                postInvalidate();
            }
        }
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown");

        mScrollStarted = false;
        mLastMotionScrollX = e.getX();
        mLastMotionScrollY = e.getY();

        // return the item being clicked
        StickerHighlightView newSelection = checkSelection(e);
        StickerHighlightView realNewSelection = newSelection;

        if (newSelection == null && mOverlayViews.size() == 1 && mForceSingleSelection) {
            // force a selection if none is selected, when force single selection is
            // turned on
            newSelection = mOverlayViews.get(0);
        }

        setSelectedHighlightView(newSelection);

        if (realNewSelection != null && mScaleWithContent) {
            RectF displayRect = realNewSelection.getDisplayRect(realNewSelection.getMatrix(),
                    realNewSelection.getCropRectF());
            boolean invalidSize = realNewSelection.getContent().validateSize(displayRect);

            Log.d(TAG, "invalidSize: " + invalidSize);

            if (!invalidSize) {
                Log.d(TAG, "drawable too small!!!");

                float minW = realNewSelection.getContent().getMinWidth();
                float minH = realNewSelection.getContent().getMinHeight();

                Log.d(TAG, "minW: " + minW);
                Log.d(TAG, "minH: " + minH);

                float minSize = Math.min(minW, minH) * 1.1f;

                Log.d(TAG, "minSize: " + minSize);

                float minRectSize = Math.min(displayRect.width(), displayRect.height());

                Log.d(TAG, "minRectSize: " + minRectSize);

                float diff = minSize / minRectSize;

                Log.d(TAG, "diff: " + diff);

                Log.d(TAG, "min.size: " + minW + "x" + minH);
                Log.d(TAG, "cur.size: " + displayRect.width() + "x" + displayRect.height());
                Log.d(TAG, "zooming to: " + (getScale() * diff));

                zoomTo(getScale() * diff, displayRect.centerX(), displayRect.centerY(),
                        DEFAULT_ANIMATION_DURATION * 1.5f);
                return true;
            }
        }

        if (mOverlayView != null) {
            //通过触摸区域得到Mode
            int edge = mOverlayView.getHit(e.getX(), e.getY());
            if (edge != StickerHighlightView.NONE) {
                mOverlayView.setMode((edge == StickerHighlightView.MOVE) ? StickerHighlightView.MOVE
                        : (edge == StickerHighlightView.ROTATE ? StickerHighlightView.ROTATE
                        : StickerHighlightView.GROW));
                postInvalidate();
                if (mDrawableListener != null) {
                    mDrawableListener.onDown(mOverlayView);
                }
            }
        }

        return super.onDown(e);
    }

    public float getmLastMotionScrollX() {
        return mLastMotionScrollX;
    }

    public float getmLastMotionScrollY() {
        return mLastMotionScrollY;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        Log.d(TAG, "onUp");

        if (mOverlayView != null) {
            mOverlayView.setMode(StickerHighlightView.NONE);
            postInvalidate();
        }
        return super.onUp(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");

        if (mOverlayView != null) {

            int edge = mOverlayView.getHit(e.getX(), e.getY());
            if ((edge & StickerHighlightView.MOVE) == StickerHighlightView.MOVE) {
                if (mDrawableListener != null) {
                    mDrawableListener.onClick(mOverlayView);
                }
                return true;
            }

            mOverlayView.setMode(StickerHighlightView.NONE);
            postInvalidate();

            Log.d(TAG, "selected items: " + mOverlayViews.size());

            /**
             * 此时, 隐藏周围虚线--不需要第一个不能隐藏虚线
             */
            /*if (mOverlayViews.size() != 1) {
                setSelectedHighlightView(null);
            }*/
            setSelectedHighlightView(null);
        }

        return super.onSingleTapUp(e);
    }

    boolean mScrollStarted;
    float   mLastMotionScrollX, mLastMotionScrollY;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll");

        float dx, dy;

        float x = e2.getX();
        float y = e2.getY();

        if (!mScrollStarted) {
            dx = 0;
            dy = 0;
            mScrollStarted = true;
        } else {
            dx = mLastMotionScrollX - x;
            dy = mLastMotionScrollY - y;
        }

        mLastMotionScrollX = x;
        mLastMotionScrollY = y;

        if (mOverlayView != null && mOverlayView.getMode() != StickerHighlightView.NONE) {
            mOverlayView.onMouseMove(mOverlayView.getMode(), e2, -dx, -dy);
            postInvalidate();

            if (mDrawableListener != null) {
                mDrawableListener.onMove(mOverlayView);
            }

            if (mOverlayView.getMode() == StickerHighlightView.MOVE) {
                if (!mScaleWithContent) {
                    ensureVisible(mOverlayView, distanceX, distanceY);
                }
            }
            return true;
        } else {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling");

        if (mOverlayView != null && mOverlayView.getMode() != StickerHighlightView.NONE)
            return false;
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean shouldInvalidateAfter = false;

        for (int i = 0; i < mOverlayViews.size(); i++) {
            canvas.save(Canvas.MATRIX_SAVE_FLAG);

            StickerHighlightView current = mOverlayViews.get(i);
            current.draw(canvas);

            // check if we should invalidate again the canvas
            if (!shouldInvalidateAfter) {
                FeatherDrawable content = current.getContent();
                if (content instanceof EditableDrawable) {
                    if (((EditableDrawable) content).isEditing()) {
                        shouldInvalidateAfter = true;
                    }
                }
            }

            canvas.restore();
        }

        if (null != mDropPaint) {
            getDrawingRect(mTempRect);
            canvas.drawRect(mTempRect, mDropPaint);
        }

        if (shouldInvalidateAfter) {
            postInvalidateDelayed(EditableDrawable.CURSOR_BLINK_TIME);
        }
    }

    public void clearOverlays() {
        Log.d(TAG, "clearOverlays");
        setSelectedHighlightView(null);
        while (mOverlayViews.size() > 0) {
            StickerHighlightView hv = mOverlayViews.remove(0);
            hv.dispose();
        }
        mOverlayView = null;
    }

    public boolean addHighlightView(StickerHighlightView hv) {
        for (int i = 0; i < mOverlayViews.size(); i++) {
            if (mOverlayViews.get(i).equals(hv))
                return false;
        }
        mOverlayViews.add(hv);
        postInvalidate();

        if (mOverlayViews.size() == 1) {
            setSelectedHighlightView(hv);
        }

        return true;
    }

    public int getHighlightCount() {
        return mOverlayViews.size();
    }

    public StickerHighlightView getHighlightViewAt(int index) {
        return mOverlayViews.get(index);
    }

    public boolean removeHightlightView(StickerHighlightView view) {
        Log.d(TAG, "removeHightlightView");
        for (int i = 0; i < mOverlayViews.size(); i++) {
            if (mOverlayViews.get(i).equals(view)) {
                StickerHighlightView hv = mOverlayViews.remove(i);
                if (hv.equals(mOverlayView)) {
                    setSelectedHighlightView(null);
                }
                hv.dispose();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onZoomAnimationCompleted(float scale) {
        Log.d(TAG, "onZoomAnimationCompleted: " + scale);
        super.onZoomAnimationCompleted(scale);

        if (mOverlayView != null) {
            mOverlayView.setMode(StickerHighlightView.MOVE);
            postInvalidate();
        }
    }

    public StickerHighlightView getSelectedHighlightView() {
        return mOverlayView;
    }

    public void commit(Canvas canvas) {

        StickerHighlightView hv;
        for (int i = 0; i < getHighlightCount(); i++) {
            hv = getHighlightViewAt(i);
            FeatherDrawable content = hv.getContent();
            if (content instanceof EditableDrawable) {
                ((EditableDrawable) content).endEdit();
            }

            Matrix rotateMatrix = hv.getCropRotationMatrix();
            Rect rect = hv.getCropRect();

            int saveCount = canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.concat(rotateMatrix);
            content.setBounds(rect);
            content.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    private StickerHighlightView checkSelection(MotionEvent e) {
        Iterator<StickerHighlightView> iterator = mOverlayViews.iterator();
        StickerHighlightView selection = null;
        while (iterator.hasNext()) {
            StickerHighlightView view = iterator.next();
            int edge = view.getHit(e.getX(), e.getY());
            if (edge != StickerHighlightView.NONE) {
                selection = view;
            }
        }
        return selection;
    }

    public void setSelectedHighlightView(StickerHighlightView newView) {

        final StickerHighlightView oldView = mOverlayView;

        if (mOverlayView != null && !mOverlayView.equals(newView)) {
            mOverlayView.setSelected(false);
        }

        if (newView != null) {
            newView.setSelected(true);
        }

        postInvalidate();

        mOverlayView = newView;

        if (mDrawableListener != null) {
            mDrawableListener.onFocusChange(newView, oldView);
        }
    }

}
