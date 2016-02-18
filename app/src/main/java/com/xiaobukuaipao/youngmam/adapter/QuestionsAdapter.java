package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Article;

import java.util.List;

/**
 * Created by xiaobu1 on 15-9-24.
 */
public class QuestionsAdapter extends YmamBaseAdapter<Article> {

    public static final int TYPE_QUESTION_NEW = 1;
    public static final int TYPE_QUESTION_SELECT = 2;

    private int type = 1;

    public QuestionsAdapter(Context context, List<Article> datas, int mItemLayoutId, int type) {
        super(context, datas, mItemLayoutId);
        this.type = type;
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Article item, final int position) {
        if (type == TYPE_QUESTION_NEW) {
            ((ImageView) viewHolder.getView(R.id.indicator_new_question)).setVisibility(View.VISIBLE);
            ((LinearLayout) viewHolder.getView(R.id.answer_layout)).setVisibility(View.GONE);
        } else if (type == TYPE_QUESTION_SELECT) {
            ((ImageView) viewHolder.getView(R.id.indicator_new_question)).setVisibility(View.GONE);
            ((LinearLayout) viewHolder.getView(R.id.answer_layout)).setVisibility(View.VISIBLE);

        }

        viewHolder.setText(R.id.txt_question, item.getContent());

        viewHolder.setText(R.id.count_answer, String.valueOf(item.getCommentCount()));
    }
}
