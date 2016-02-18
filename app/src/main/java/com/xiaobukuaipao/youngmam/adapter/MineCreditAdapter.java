package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Credit;

import java.util.List;

/**
 * Created by xiaobu1 on 15-7-23.
 */
public class MineCreditAdapter extends YmamBaseAdapter<Credit> {

    public MineCreditAdapter(Context context, List<Credit> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Credit item, final int position) {

        ((TextView) viewHolder.getView(R.id.title)).setText(item.getName());

        ((TextView) viewHolder.getView(R.id.credit)).setText(String.valueOf(item.getPoint()));

        if (item.getLimit() > 0) {
            ((TextView) viewHolder.getView(R.id.count)).setVisibility(View.VISIBLE);

            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(item.getDone()));
            sb.append("/");
            sb.append(String.valueOf(item.getLimit()));

            ((TextView) viewHolder.getView(R.id.count)).setText(sb.toString());
        } else {
            ((TextView) viewHolder.getView(R.id.count)).setVisibility(View.GONE);
        }
    }

}
