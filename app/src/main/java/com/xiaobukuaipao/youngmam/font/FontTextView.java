package com.xiaobukuaipao.youngmam.font;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.font.TypefaceManager.DrawCallback;

public class FontTextView extends TextView {

    public FontTextView(Context context)
    {
        this(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            TypefaceManager.applyFont(this, attrs, defStyle);
        }
    }

    private final DrawCallback drawCallback = new DrawCallback() {
        @SuppressLint("WrongCall")
        @Override public void onDraw(Canvas canvas) {
            FontTextView.super.onDraw(canvas);
        }
    };

    @Override
    protected void onDraw(Canvas canvas)
    {
        TypefaceManager.onDrawHelper(canvas, this, drawCallback);
        super.onDraw(canvas);
    }

}
