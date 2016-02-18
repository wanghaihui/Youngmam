package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Gift;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;

import java.util.List;

/**
 * Created by xiaobu1 on 15-6-30.
 */
public class GiftAdapter extends YmamBaseAdapter<Gift> {

    public GiftAdapter(Context context, List<Gift> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Gift item, final int position) {

        int width = DisplayUtil.getScreenWidth(context) - 2 * DisplayUtil.dip2px(context, 10);
        Glide.with(context)
                .load(item.getPosterUrl())
                .placeholder(R.mipmap.default_loading)
                .override(width, width * 345 / 600)
                .centerCrop()
                .dontAnimate()
                .into((ImageView) viewHolder.getView(R.id.image_gift));

        ((TextView) viewHolder.getView(R.id.gift_name)).setText(item.getName());

        ((TextView) viewHolder.getView(R.id.gift_price)).setText(String.valueOf(item.getCost()));

        final TextView giftState = (TextView) viewHolder.getView(R.id.gift_state);
        final ImageView coverView = (ImageView) viewHolder.getView(R.id.cover_view);
        if (item.getStatus() == 1) {
            // 此时,代表礼品正在销售中
            giftState.setTextColor(context.getResources().getColor(R.color.color_54af74));
            giftState.setText(context.getResources().getString(R.string.str_gift_on_market));
            coverView.setBackgroundColor(context.getResources().getColor(R.color.color_00_black));
        } else {
            // 此时,status为2,代表礼品已经下架
            giftState.setTextColor(context.getResources().getColor(R.color.color_b2b2b2));
            giftState.setText(context.getResources().getString(R.string.str_gift_off_market));
            coverView.setBackgroundColor(context.getResources().getColor(R.color.color_33_black));
        }

        final TextView giftPrivilege = (TextView) viewHolder.getView(R.id.gift_privilege);
        if (item.getPrivilege() == 1) {
            // 此时,不是达人专享,代表所有用户都可以参与
            giftPrivilege.setText(context.getResources().getString(R.string.str_gift_privilege_all_user));
        } else {
            // 此时,是达人专享
            giftPrivilege.setText(context.getResources().getString(R.string.str_gift_privilege_expert));
        }
    }
}
