package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.view.LabelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-7.
 * 标签列表
 */
public class LabelListView extends FlowLayout implements View.OnClickListener {

    private boolean mIsDeleteMode;
    private OnLabelCheckedChangedListener mOnLabelCheckedChangedListener;
    private OnLabelClickListener mOnLabelClickListener;
    private int mLabelViewBackgroundResId;
    private int mLabelViewTextColorResId;

    private final List<Label> mLabels = new ArrayList<Label>();

    public LabelListView(Context context) {
        super(context);
        init();
    }

    public LabelListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public LabelListView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init();
    }

    private void init() {

    }

    @Override
    public void onClick(View v) {
        if (v instanceof  LabelView) {
            Label label = (Label) v.getTag();
            if (this.mOnLabelClickListener != null) {
                this.mOnLabelClickListener.onLabelClick((LabelView) v, label);
            }
        }
    }

    private void inflateLabelView(final Label label, boolean checked) {
        LabelView labelView = (LabelView) View.inflate(getContext(), R.layout.label, null);
        labelView.setText(label.getTitle());
        labelView.setTag(label);

        // 可以自定义Text的颜色,以及背景

        // 设置是否选中
        labelView.setChecked(label.getChecked());

        labelView.setCheckEnable(checked);

        if (mIsDeleteMode) {
            int paddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getContext().getResources().getDisplayMetrics());

            labelView.setPadding(labelView.getPaddingLeft(), labelView.getPaddingTop(), paddingRight, labelView.getPaddingRight());

            labelView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_search_delete, 0);
        }

        labelView.setOnClickListener(this);

        labelView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                label.setChecked(isChecked);

                if (LabelListView.this.mOnLabelCheckedChangedListener != null) {
                    LabelListView.this.mOnLabelCheckedChangedListener.onLabelCheckedChanged((LabelView) buttonView, label);
                }
            }
        });

        addView(labelView);
    }

    public void addLabel(String id, String title) {
        addLabel(id, title, true);
    }

    public void addLabel(String id, String title, boolean checked) {
        addLabel(new Label(id, title), checked);
    }

    public void addLabel(Label label) {
        addLabel(label, true);
    }

    public void addLabel(Label label, boolean checked) {
        mLabels.add(label);
        inflateLabelView(label, checked);
    }

    public void setLabels(List<? extends Label> lists) {
        setLabels(lists, true);
    }

    public void setLabels(List<? extends Label> lists, boolean checked) {
        removeAllViews();
        mLabels.clear();

        for (int i=0; i < lists.size(); i++) {
            addLabel((Label) lists.get(i), checked);
        }
    }


    public void setDeleteMode(boolean b) {
        mIsDeleteMode = b;
    }

    public void setOnLabelCheckedChangedListener(OnLabelCheckedChangedListener onLabelCheckedChangedListener) {
        mOnLabelCheckedChangedListener = onLabelCheckedChangedListener;
    }

    public void setOnLabelClickListener(OnLabelClickListener onLabelClickListener) {
        mOnLabelClickListener = onLabelClickListener;
    }


    public static abstract interface OnLabelCheckedChangedListener {
        public abstract void onLabelCheckedChanged(LabelView labelView, Label label);
    }

    public static abstract interface OnLabelClickListener {
        public abstract void onLabelClick(LabelView labelView, Label label);
    }

}
