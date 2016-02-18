package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.ChildStatus;

import java.util.List;

/**
 * Created by xiaobu1 on 15-5-19.
 */
public class ChildStatusAdapter extends YmamBaseAdapter<ChildStatus> {

    public ChildStatusAdapter(Context context, List<ChildStatus> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

    }

    @Override
    public void convert(final ViewHolder viewHolder, final ChildStatus item, final int position) {
        viewHolder.setText(R.id.str_item, item.getName());
    }
}
