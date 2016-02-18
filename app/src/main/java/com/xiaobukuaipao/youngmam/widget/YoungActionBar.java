package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.font.FontTextView;

/**
 * Created by xiaobu1 on 15-4-23.
 * 通用的ActionBar
 */
public class YoungActionBar extends RelativeLayout {
    private static final String TAG = YoungActionBar.class.getSimpleName();

    private static final int LEFT_FRAME_ID = 1;
    private static final int RIGHT_FRAME_ID = 2;

    private Context context;

    private FrameLayout mLeftFrame;
    private LinearLayout mMiddleFrame;
    private FrameLayout mRightFrame;

    public YoungActionBar(Context context) {
        this(context, null);
    }

    public YoungActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YoungActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化ActionBar
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;

        // 首先,可以设置自定义参数
        // 其次,设置基本布局--目前先分成三块
        // Left
        setLeftLayout();
        // Right
        setRightLayout();

        // Middle
        setMiddleLayout();
    }

    /**
     * 设置左边的Layout
     */
    private void setLeftLayout() {
        mLeftFrame = new FrameLayout(context);

        LayoutParams params = new LayoutParams((int) context.getResources().getDimension(R.dimen.action_bar_height),
                (int) context.getResources().getDimension(R.dimen.action_bar_height));

        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mLeftFrame.setLayoutParams(params);

        mLeftFrame.setBackgroundResource(R.drawable.action_bar_bg_selector);

        addView(mLeftFrame);

    }

    /**
     * 设置中间的Layout
     */
    private void setMiddleLayout() {
        mMiddleFrame = new LinearLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mMiddleFrame.setLayoutParams(params);

        addView(mMiddleFrame);
    }

    /**
     * 设置右边的Layout
     */
    private void setRightLayout() {
        Log.d(TAG, "set right layout");
        mRightFrame = new FrameLayout(context);

        LayoutParams params = new LayoutParams(
                (int) context.getResources().getDimension(R.dimen.action_bar_height),
                (int) context.getResources().getDimension(R.dimen.action_bar_height));

        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mRightFrame.setLayoutParams(params);

        mRightFrame.setBackgroundResource(R.drawable.action_bar_bg_selector);

        addView(mRightFrame);
    }

    /**
     * 设置左边的图标
     */
    public void setLeftAction(Type type, int resourceId, String text) {
        if (type == Type.IMAGE) {
            ImageView imageView = new ImageView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setImageResource(resourceId);

            mLeftFrame.addView(imageView);
        } else if (type == Type.TEXT) {

        }
    }

    /**
     * 设置中间的标题--跑马灯
     */
    public void setMiddleAction(Type type, int resourceId, String text) {
        // 首先将里面的View全部移除
        mMiddleFrame.removeAllViews();

        if (type == Type.IMAGE) {
            // 如果是图片
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setImageResource(resourceId);

            mMiddleFrame.addView(imageView);
        } else if (type == Type.TEXT) {
            // 如果是文字
            MarqueeTextView textView = (MarqueeTextView) View.inflate(this.getContext(), R.layout.action_bar_text, null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);

            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(context.getResources().getColor(R.color.white));
            /*textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setMarqueeRepeatLimit(-1);*/

            mMiddleFrame.addView(textView);
        } else if (type == Type.TEXT_IMAGE) {
            // 文字加图片
            MarqueeTextView textView = (MarqueeTextView) View.inflate(this.getContext(), R.layout.action_bar_text, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);

            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(resourceId), null);

            // 设置字体
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setTextColor(context.getResources().getColor(R.color.white));

            textView.isDuplicateParentStateEnabled();

            mMiddleFrame.addView(textView);
        }
    }

    /**
     * 设置中间的标题--省略号
     */
    public void setMiddleAction2(Type type, int resourceId, String text) {
        // 首先将里面的View全部移除
        mMiddleFrame.removeAllViews();

        if (type == Type.IMAGE) {
            // 如果是图片
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setImageResource(resourceId);

            mMiddleFrame.addView(imageView);
        } else if (type == Type.TEXT) {
            // 如果是文字
            FontTextView textView = (FontTextView) View.inflate(this.getContext(), R.layout.action_bar_text2, null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);

            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(context.getResources().getColor(R.color.white));

            mMiddleFrame.addView(textView);
        } else if (type == Type.TEXT_IMAGE) {
            // 文字加图片
            FontTextView textView = (FontTextView) View.inflate(this.getContext(), R.layout.action_bar_text2, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);

            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(resourceId), null);

            // 设置字体
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setTextColor(context.getResources().getColor(R.color.white));

            textView.isDuplicateParentStateEnabled();

            mMiddleFrame.addView(textView);
        }
    }

    /**
     * 设置中间Title的颜色
     * @param colorId
     */
    public void setMiddleTextColor(int colorId) {
        TextView textView = (TextView) mMiddleFrame.getChildAt(0);
        textView.setTextColor(colorId);
    }

    /**
     * 设置右边的图标
     */
    public void setRightAction(Type type, int resourceId, String text) {
        if (type == Type.IMAGE) {
            // 如果是照片
            ImageView imageView = new ImageView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setImageResource(resourceId);

            mRightFrame.addView(imageView);
        } else if (type == Type.TEXT) {
            LayoutParams paramsChange = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    (int) context.getResources().getDimension(R.dimen.action_bar_height));

            paramsChange.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mRightFrame.setLayoutParams(paramsChange);

            // 如果是文字
            FontTextView textView = (FontTextView) View.inflate(this.getContext(), R.layout.action_bar_text_right, null);
            LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);

            textView.setLayoutParams(params);

            textView.setGravity(Gravity.CENTER_VERTICAL);

            if (resourceId > 0) {
                textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(resourceId), null, null, null);
                textView.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.activity_basic_margin_2dp));
            }
            // 设置字体
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(context.getResources().getColor(R.color.white));

            if (resourceId > 0) {
                mRightFrame.setPadding((int) context.getResources().getDimension(R.dimen.activity_basic_margin_10dp), 0,
                        (int) context.getResources().getDimension(R.dimen.action_bar_right_padding), 0);
            } else {
                mRightFrame.setPadding((int) context.getResources().getDimension(R.dimen.action_bar_right_padding), 0,
                        (int) context.getResources().getDimension(R.dimen.action_bar_right_padding), 0);
            }
            mRightFrame.addView(textView);
        } else if (type == Type.TEXT_IMAGE) {
            LayoutParams paramsChange = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    (int) context.getResources().getDimension(R.dimen.action_bar_height));

            paramsChange.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mRightFrame.setLayoutParams(paramsChange);

            // 如果是文字
            FontTextView textView = (FontTextView) View.inflate(this.getContext(), R.layout.action_bar_text_right, null);
            LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);

            textView.setLayoutParams(params);

            textView.setGravity(Gravity.CENTER_VERTICAL);

            if (resourceId > 0) {
                textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(resourceId), null, null, null);
                textView.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.activity_basic_margin_5dp));
            }
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(context.getResources().getColor(R.color.white));

            mRightFrame.setPadding((int) context.getResources().getDimension(R.dimen.action_bar_right_padding), 0,
                    (int) context.getResources().getDimension(R.dimen.action_bar_right_padding), 0);
            mRightFrame.addView(textView);
        }
    }

    public void setBackground(int colorId) {
        setBackgroundColor(colorId);
    }

    /**
     * 可以给其中的控件设置是Image还是Text
     */
    public enum Type {
        IMAGE, TEXT, TEXT_IMAGE
    }

    public FrameLayout getLeftFrame() {
        return this.mLeftFrame;
    }

    public LinearLayout getMiddleFrame() {
        return this.mMiddleFrame;
    }

    public FrameLayout getRightFrame() {
        return this.mRightFrame;
    }
}
