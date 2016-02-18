package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Comment;
import com.xiaobukuaipao.youngmam.font.FontTextView;
import com.xiaobukuaipao.youngmam.font.TypefaceManager;

import java.util.List;

/**
 * Created by xiaobu1 on 15-9-30.
 */
public class FloorCommentView extends LinearLayout {

    private List<Comment> datas;

    public FloorCommentView(Context context) {
        super(context);
        init(context);
    }

    public FloorCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloorCommentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setComments(List<Comment> comments) {
        datas = comments;
    }

    private void init(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
    }

    public void init() {
        // 先移除所有的View
        removeAllViews();

        for (int i=0; i < datas.size(); i++) {

            Comment comment = datas.get(i);

            FontTextView fontTextView = new FontTextView(getContext());
            TypefaceManager.getInstance().setTypeface(fontTextView, "fzltxihk");
            fontTextView.setTextColor(getResources().getColor(R.color.color_505050));
            fontTextView.setText("haha");

            addView(fontTextView);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.getChildCount() <= 0) {
            setMeasuredDimension(0, 0);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface OnCommentClickListner {
        public void onCommentClick(Comment comment);
    }
}
