package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.MamMessage;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;

import java.util.List;

/**
 * Created by xiaobu1 on 15-10-24.
 */
public class LikeCommentAdapter extends YmamBaseAdapter<MamMessage> {

    public LikeCommentAdapter(Context context, List<MamMessage> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final MamMessage item, final int position) {
        final ImageView avatar = viewHolder.getView(R.id.avatar);
        Picasso.with(context)
                .load(item.getActorUserBase().getHeadUrl())
                .placeholder(R.mipmap.default_mama)
                .fit()
                .centerCrop()
                .into(avatar);

        viewHolder.setText(R.id.name, item.getActorUserBase().getName());
        viewHolder.setText(R.id.content, item.getContent());

        viewHolder.setText(R.id.time, TimeUtil.handleTime(context, item.getCreateTime()));

        final ImageView itemImage = viewHolder.getView(R.id.item_image);

        if (item.getImg() != null) {
            Glide.with(context)
                    .load(item.getImg().getUrl())
                    .placeholder(R.mipmap.default_loading)
                    .centerCrop()
                    .into(itemImage);
        }
    }

}
