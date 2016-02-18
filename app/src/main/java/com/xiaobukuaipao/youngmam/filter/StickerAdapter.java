package com.xiaobukuaipao.youngmam.filter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.ViewHolder;
import com.xiaobukuaipao.youngmam.adapter.YmamBaseAdapter;
import com.xiaobukuaipao.youngmam.domain.Sticker;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

import java.util.List;

/**
 * Created by xiaobu1 on 15-9-17.
 */
public class StickerAdapter extends YmamBaseAdapter<Sticker> {

    public StickerAdapter(Context context, List<Sticker> stickers, int mItemLayoutId) {
        super(context, stickers, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Sticker item, final int position) {
        final ImageView name = viewHolder.getView(R.id.image_item);
        Glide.with(context)
                .load(datas.get(position).getSmallImg().getString(GlobalConstants.JSON_URL))
                .into(name);

        if (item.isSelected()) {
            ((View) viewHolder.getView(R.id.small_filter_container)).setBackgroundColor(context.getResources().getColor(R.color.color_ff4c51));
        } else {
            ((View) viewHolder.getView(R.id.small_filter_container)).setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
