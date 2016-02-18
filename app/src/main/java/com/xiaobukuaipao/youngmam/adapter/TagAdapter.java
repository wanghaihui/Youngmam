package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Tag;

import java.util.List;

/**
 * Created by xiaobu1 on 15-7-3.
 */
public class TagAdapter extends YmamBaseAdapter<Tag> {

    public TagAdapter(Context context, List<Tag> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Tag item, final int position) {
        Glide.with(context).load(item.getImgUrl())
                .placeholder(R.mipmap.default_loading)
                .into((ImageView) viewHolder.getView(R.id.image_item));

        ((TextView) viewHolder.getView(R.id.tag_name)).setText(item.getName());
    }
}
