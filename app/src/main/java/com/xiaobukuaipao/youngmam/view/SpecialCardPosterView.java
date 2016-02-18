package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-7-8.
 */
public class SpecialCardPosterView extends FrameLayout {

    private ImageView posterView;
    private ImageView markerView;

    public SpecialCardPosterView(Context context) {
        this(context, null);
    }

    public SpecialCardPosterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialCardPosterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化ActionBar
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        posterView = new ImageView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        posterView.setLayoutParams(params);

        addView(posterView);

        markerView = new ImageView(context);
        FrameLayout.LayoutParams paramsMarker = new FrameLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.poster_marker_width),
                context.getResources().getDimensionPixelSize(R.dimen.poster_marker_width));
        markerView.setLayoutParams(paramsMarker);
        addView(markerView);
    }

    public ImageView getPosterView() {
        return this.posterView;
    }

    public ImageView getMarkerView() {
        return this.markerView;
    }
}
