package com.xiaobukuaipao.youngmam.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.font.TypefaceManager;

public class DrawableCenterTextView extends TextView {

	public DrawableCenterTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

        if (!isInEditMode())
            TypefaceManager.applyFont(this, attrs, defStyle);
	}
	
	public DrawableCenterTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}
	
	public DrawableCenterTextView(Context context) {
		this(context, null);
	}

    private final TypefaceManager.DrawCallback drawCallback = new TypefaceManager.DrawCallback() {
        @SuppressLint("WrongCall")
        @Override public void onDraw(Canvas canvas) {
            DrawableCenterTextView.super.onDraw(canvas);
        }
    };

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable leftDrawable = drawables[0];
			if (leftDrawable != null) {
				float textWidth = getPaint().measureText(getText().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = leftDrawable.getIntrinsicWidth();
				float bodyWidth = textWidth + drawablePadding + drawableWidth;
				canvas.translate((getWidth() - bodyWidth) / 2, 0);
			}
		}

        TypefaceManager.onDrawHelper(canvas, this, drawCallback);
		super.onDraw(canvas);
	}
	
	

}
