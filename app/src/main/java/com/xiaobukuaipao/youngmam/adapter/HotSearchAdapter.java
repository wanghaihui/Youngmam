package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Label;

import java.util.List;

/**
 * Created by xiaobu1 on 15-5-7.
 */
public class HotSearchAdapter extends YmamBaseAdapter<Label> {

    public HotSearchAdapter(Context context, List<Label> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Label item, final int position) {
        viewHolder.setText(R.id.search, item.getTitle());
    }
}
