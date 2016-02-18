package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.MamMessage;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;

import java.util.List;

/**
 * Created by xiaobu1 on 15-10-24.
 */
public class NewFriendsAdapter extends YmamBaseAdapter<MamMessage> {

    public NewFriendsAdapter(Context context, List<MamMessage> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final MamMessage item, final int position) {
        final ImageView avatar = viewHolder.getView(R.id.avatar);
        Picasso.with(context)
                .load(item.getActorUserBase().getHeadUrl())
                .fit()
                .centerCrop()
                .into(avatar);

        viewHolder.setText(R.id.name, item.getActorUserBase().getName());
        viewHolder.setText(R.id.content, item.getContent());

        viewHolder.setText(R.id.time, TimeUtil.handleTime(context, item.getCreateTime()));
    }
}
