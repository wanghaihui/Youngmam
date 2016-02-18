package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.font.FontTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-1.
 */
public class LabelListView2 extends FlowLayout implements View.OnClickListener {

    private OnLabelClickListener onLabelClickListener;

    private int type;
    private OnLabelClickListener2 onLabelClickListener2;

    private final List<Label> mLabels = new ArrayList<Label>();

    public LabelListView2(Context context) {
        super(context);
        init();
    }

    public LabelListView2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public LabelListView2(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init();
    }

    private void init() {

    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            Label label = (Label) v.getTag();
            if (this.onLabelClickListener != null) {
                this.onLabelClickListener.onLabelClick((TextView) v, label);
            }

            if (this.onLabelClickListener2 != null) {
                this.onLabelClickListener2.onLabelClick2((TextView) v, label, type);
            }
        }
    }

    private void inflateLabelView(final Label label) {
        FontTextView labelView = (FontTextView) View.inflate(getContext(), R.layout.article_label, null);
        labelView.setText(label.getTitle());
        labelView.setTag(label);

        labelView.setOnClickListener(this);

        addView(labelView);
    }

    public void addLabel(Label label) {
        mLabels.add(label);
        inflateLabelView(label);
    }

    public void setLabels(List<? extends Label> lists) {
        removeAllViews();
        mLabels.clear();

        for (int i=0; i < lists.size(); i++) {
            addLabel((Label) lists.get(i));
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOnLabelClickListener(OnLabelClickListener onLabelClickListener) {
        this.onLabelClickListener = onLabelClickListener;
    }

    public void setOnLabelClickListener2(OnLabelClickListener2 onLabelClickListener2) {
        this.onLabelClickListener2 = onLabelClickListener2;
    }

    public  interface OnLabelClickListener {
        void onLabelClick(TextView labelView, Label label);
    }

    public interface OnLabelClickListener2 {
        void onLabelClick2(TextView labelView, Label label, int type);
    }
}
