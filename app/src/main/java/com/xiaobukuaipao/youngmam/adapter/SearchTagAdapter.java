package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Tag;

import java.util.List;

/**
 * Created by xiaobu1 on 15-7-3.
 */
public class SearchTagAdapter extends YmamBaseAdapter<Tag> {

    // 普通tag
    public static final int TYPE_SEARCH_TAG = 1;
    // 专题
    public static final int TYPE_SEARCH_THEME = 4;

    public SearchTagAdapter(Context context, List<Tag> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Tag item, final int position) {
        Picasso.with(context).load(item.getImgUrl())
                .placeholder(R.mipmap.default_loading)
                .fit()
                .centerCrop()
                .into((ImageView) viewHolder.getView(R.id.avatar));

        ((TextView) viewHolder.getView(R.id.title)).setText(item.getName());

        if (item.getType() == TYPE_SEARCH_TAG) {
            ((TextView) viewHolder.getView(R.id.desc)).setText(context.getResources().getString(R.string.str_iamatag));
        } else {
            ((TextView) viewHolder.getView(R.id.desc)).setText(item.getDesc());
        }
    }
}
