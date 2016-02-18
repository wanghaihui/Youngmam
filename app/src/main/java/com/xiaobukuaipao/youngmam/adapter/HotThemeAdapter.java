package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Theme;

import java.util.List;

/**
 * Created by xiaobu1 on 15-10-13.
 */
public class HotThemeAdapter extends YmamBaseAdapter<Theme> {

    public HotThemeAdapter(Context context, List<Theme> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Theme item, final int position) {
        final ImageView avatar = viewHolder.getView(R.id.image_item);

        Picasso.with(context)
                .load(item.getSmallPosterUrl())
                .placeholder(R.mipmap.default_loading)
                .fit()
                .centerCrop()
                .into(avatar);
    }
}
