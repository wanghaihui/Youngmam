package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.CommonAction;

import java.util.List;

/**
 * Created by xiaobu1 on 15-11-3.
 */
public class CommonActionAdapter extends YmamBaseAdapter<CommonAction> {

    public CommonActionAdapter(Context context, List<CommonAction> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

    }

    @Override
    public void convert(final ViewHolder viewHolder, final CommonAction item, final int position) {
        viewHolder.setText(R.id.str_item, item.getActionName());
    }
}
