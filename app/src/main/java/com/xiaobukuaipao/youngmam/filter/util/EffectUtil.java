package com.xiaobukuaipao.youngmam.filter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Sticker;
import com.xiaobukuaipao.youngmam.filter.PhotoProcessActivity;
import com.xiaobukuaipao.youngmam.filter.drawable.StickerDrawable;
import com.xiaobukuaipao.youngmam.filter.view.ImageViewTouch;
import com.xiaobukuaipao.youngmam.filter.view.StickerHighlightView;
import com.xiaobukuaipao.youngmam.filter.view.StickerImageViewDrawableOverlay;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xiaobu1 on 15-9-11.
 */
public class EffectUtil {
    public static final String TAG = EffectUtil.class.getSimpleName();

    public static final int ALIGNMENT_TOP_LEFT = 1;
    public static final int ALIGNMENT_TOP_RIGHT = 2;
    public static final int ALIGNMENT_BOTTOM_RIGHT = 3;
    public static final int ALIGNMENT_BOTTOM_LEFT = 4;
    public static final int ALIGNMENT_CENTER = 5;

    public static final int ALIGNMENT_MARGIN = 50;

    private static List<StickerHighlightView> highlightViews = new CopyOnWriteArrayList<StickerHighlightView>();

    /**
     * 清空
     */
    public static void clear() {
        highlightViews.clear();
    }

    private static boolean containsSticker(Sticker sticker) {
        for (int i = 0; i < highlightViews.size(); i++) {
            if (highlightViews.get(i).getStickerId().equals(sticker.getStickerId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 添加贴纸
     */
    public static void addStickerImage(final Context context, final ImageViewTouch processImage,
                                                       final Sticker sticker, final StickerCallback callback) {
        if (!containsSticker(sticker)) {

            // 此时, 如果已经有了水印, 则先要把之前的水印去掉
            if (sticker.getGroup().getType() == PhotoProcessActivity.TYPE_WATERMARK) {
                for (int i = 0; i < highlightViews.size(); i++) {
                    if (highlightViews.get(i).getStickerType() == PhotoProcessActivity.TYPE_WATERMARK) {
                        ((StickerImageViewDrawableOverlay) processImage).removeHightlightView(highlightViews.get(i));
                        highlightViews.remove(highlightViews.get(i));
                        ((StickerImageViewDrawableOverlay) processImage).invalidate();
                    }
                }
            }

            new AsyncTask<String, Void, Bitmap>() {

                protected Bitmap doInBackground(String... params) {
                    // 首先将图片根据url变成InputStream
                    try {
                        InputStream inputStream = new URL(sticker.getImg().getString(GlobalConstants.JSON_URL)).openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return bitmap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Bitmap bitmap) {

                    StickerDrawable drawable = new StickerDrawable(context.getResources(), bitmap);
                    drawable.setAntiAlias(true);
                    drawable.setMinSize(30, 30);

                    final StickerHighlightView highlightView = new StickerHighlightView(processImage, R.style.AppTheme, drawable,
                            sticker.getStickerId(), sticker.getGroup().getType());
                    // 设置贴纸padding
                    highlightView.setPadding(10);

                    highlightView.setOnDeleteClickListener(new StickerHighlightView.OnDeleteClickListener() {
                        @Override
                        public void onDeleteClick() {
                            ((StickerImageViewDrawableOverlay) processImage).removeHightlightView(highlightView);
                            highlightViews.remove(highlightView);
                            ((StickerImageViewDrawableOverlay) processImage).invalidate();

                            callback.onRemoveSticker(sticker);
                        }
                    });

                    Matrix mImageMatrix = processImage.getImageViewMatrix();

                    int cropWidth, cropHeight;
                    int x, y;

                    final int width = processImage.getWidth();
                    final int height = processImage.getHeight();

                    // width/height of the sticker
                    cropWidth = (int) drawable.getCurrentWidth();
                    cropHeight = (int) drawable.getCurrentHeight();

                    final int cropSize = Math.max(cropWidth, cropHeight);
                    final int screenSize = Math.min(processImage.getWidth(), processImage.getHeight());

                    RectF positionRect = null;

                    //if (cropSize > screenSize) {

                    float ratio;

                    float widthRatio = (float) processImage.getWidth() / cropWidth;
                    float heightRatio = (float) processImage.getHeight() / cropHeight;

                    if (widthRatio < heightRatio) {
                        ratio = widthRatio;
                    } else {
                        ratio = heightRatio;
                    }

                    cropWidth = (int) ((float) cropWidth * (ratio / 2));
                    cropHeight = (int) ((float) cropHeight * (ratio / 2));

                    int w = processImage.getWidth();
                    int h = processImage.getHeight();

                    Log.d(TAG, "alignment : " + sticker.getAlignment());

                    switch (sticker.getAlignment()) {
                        case ALIGNMENT_TOP_LEFT:
                            positionRect = new RectF(0 + ALIGNMENT_MARGIN, 0 + ALIGNMENT_MARGIN, cropWidth, cropHeight);
                            break;
                        case ALIGNMENT_TOP_RIGHT:
                            positionRect = new RectF(w - cropWidth - ALIGNMENT_MARGIN, 0 + ALIGNMENT_MARGIN, w, cropHeight);
                            break;
                        case ALIGNMENT_BOTTOM_LEFT:
                            positionRect = new RectF(0 + ALIGNMENT_MARGIN, h - cropHeight - ALIGNMENT_MARGIN, cropWidth, h);
                            break;
                        case ALIGNMENT_BOTTOM_RIGHT:
                            positionRect = new RectF(w - cropWidth - ALIGNMENT_MARGIN, h - cropHeight - ALIGNMENT_MARGIN, w, h);
                            break;
                        case ALIGNMENT_CENTER:
                            positionRect = new RectF(w / 2 - cropWidth / 2, h / 2 - cropHeight / 2,
                                    w / 2 + cropWidth / 2, h / 2 + cropHeight / 2);
                            break;
                    }

                        /*Log.d(TAG, "dx : " + ((positionRect.width() - cropWidth) / 2));
                        Log.d(TAG, "dy : " + ((positionRect.height() - cropHeight) / 2));
                        positionRect.inset((positionRect.width() - cropWidth) / 2,
                                (positionRect.height() - cropHeight) / 2);*/
                    //}

                    if (positionRect != null) {

                        x = (int) positionRect.left;
                        y = (int) positionRect.top;

                    } else {
                        x = (width - cropWidth) / 2;
                        y = (height - cropHeight) / 2;
                    }

                    Matrix matrix = new Matrix(mImageMatrix);
                    matrix.invert(matrix);

                    float[] pts = new float[]{ x, y, x + cropWidth, y + cropHeight };
                    MatrixUtils.mapPoints(matrix, pts);

                    RectF cropRect = new RectF(pts[0], pts[1], pts[2], pts[3]);
                    Rect imageRect = new Rect(0, 0, width, height);

                    highlightView.setup(context, mImageMatrix, imageRect, cropRect, false);

                    ((StickerImageViewDrawableOverlay) processImage).addHighlightView(highlightView);

                    ((StickerImageViewDrawableOverlay) processImage).setSelectedHighlightView(highlightView);

                    highlightViews.add(highlightView);

                }

            }.execute();
        }

    }

    /**
     * 删除贴纸的回调接口
     */
    public interface StickerCallback {
        void onRemoveSticker(Sticker sticker);
    }

    // 添加贴纸
    public static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage) {
        for (StickerHighlightView view : highlightViews) {
            applyOnSave(mCanvas, processImage, view);
        }
    }

    private static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage, StickerHighlightView view) {
        if (view != null && view.getContent() instanceof StickerDrawable) {

            final StickerDrawable stickerDrawable = ((StickerDrawable) view.getContent());
            RectF cropRect = view.getCropRectF();

            Rect rect = new Rect((int) cropRect.left, (int) cropRect.top, (int) cropRect.right,
                    (int) cropRect.bottom);

            Matrix rotateMatrix = view.getCropRotationMatrix();
            Matrix matrix = new Matrix(processImage.getImageMatrix());
            if (!matrix.invert(matrix)) {
            }
            int saveCount = mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
            mCanvas.concat(rotateMatrix);

            stickerDrawable.setDropShadow(false);
            view.getContent().setBounds(rect);
            view.getContent().draw(mCanvas);
            mCanvas.restoreToCount(saveCount);
        }
    }

    // 添加贴纸
    public static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage, float ratio) {
        for (StickerHighlightView view : highlightViews) {
            applyOnSave(mCanvas, processImage, view, ratio);
        }
    }

    private static void applyOnSave(Canvas mCanvas, ImageViewTouch processImage, StickerHighlightView view, float ratio) {

        if (view != null && view.getContent() instanceof StickerDrawable) {

            final StickerDrawable stickerDrawable = ((StickerDrawable) view.getContent());
            RectF cropRect = view.getCropRectF();
            Rect rect = new Rect((int) (cropRect.left * ratio), (int) (cropRect.top * ratio), (int) (cropRect.right * ratio),
                    (int) (cropRect.bottom * ratio));

            Matrix rotateMatrix = view.getCropRotationMatrix();
            Matrix matrix = new Matrix(processImage.getImageMatrix());
            if (!matrix.invert(matrix)) {
            }
            int saveCount = mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
            mCanvas.concat(rotateMatrix);

            stickerDrawable.setDropShadow(false);
            view.getContent().setBounds(rect);
            view.getContent().draw(mCanvas);
            mCanvas.restoreToCount(saveCount);
        }
    }
}
