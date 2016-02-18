package com.xiaobukuaipao.youngmam.font;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.EditText;

import com.xiaobukuaipao.youngmam.font.TypefaceManager.DrawCallback;

/**
 * Created by xiaobu1 on 15-6-1.
 */
public class FontEditText extends EditText {

    public FontEditText(Context context) {
        this(context, null);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode())
            TypefaceManager.applyFont(this, attrs, defStyle);
    }

    private final DrawCallback drawCallback = new DrawCallback() {
        @SuppressLint("WrongCall")
        @Override public void onDraw(Canvas canvas) {
            FontEditText.super.onDraw(canvas);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        TypefaceManager.onDrawHelper(canvas, this, drawCallback);
        super.onDraw(canvas);
    }
}