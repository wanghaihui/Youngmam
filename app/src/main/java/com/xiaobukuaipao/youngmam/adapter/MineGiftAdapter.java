package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.GiftAddressActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.MineGift;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;
import com.xiaobukuaipao.youngmam.view.CreditImageView;

import java.util.List;

/**
 * Created by xiaobu1 on 15-7-10.
 */
public class MineGiftAdapter extends YmamBaseAdapter<MineGift> {

    public static final int STATUS_ADDRESS_NOT_CONFIRMED = 2;
    public static final int STATUS_NOT_DISPATCHED = 3;
    public static final int STATUS_DISPATCHED = 4;
    public static final int STATUS_CONFIRMED = 5;
    public static final int STATUS_CANCELED = 6;

    public MineGiftAdapter(Context context, List<MineGift> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final MineGift item, final int position) {
        if (item.getGift().getImgs() != null && item.getGift().getImgs().size() > 0) {
            Glide.with(context)
                    .load(item.getGift().getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                    .placeholder(R.mipmap.default_loading)
                    .into((CreditImageView) viewHolder.getView(R.id.image_item));
        }

        ((TextView) viewHolder.getView(R.id.gift_name)).setText(item.getGift().getName());

        final TextView giftStatus = (TextView) viewHolder.getView(R.id.gift_status);

        switch (item.getGiftMeta().getStatus()) {
            case STATUS_ADDRESS_NOT_CONFIRMED:
                giftStatus.setVisibility(View.VISIBLE);
                giftStatus.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.warning), null, null, null);
                giftStatus.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_5dp));
                giftStatus.setText(context.getResources().getString(R.string.str_addr_not_confirmed));
                giftStatus.setTextColor(context.getResources().getColor(R.color.color_ff4c51));

                giftStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GiftAddressActivity.class);
                        intent.putExtra("mine_gift_addr", true);
                        intent.putExtra("tx_id", item.getGiftMeta().getTxId());
                        context.startActivity(intent);
                    }
                });
                break;
            case STATUS_NOT_DISPATCHED:
                giftStatus.setVisibility(View.GONE);
                break;
            case STATUS_DISPATCHED:
                giftStatus.setVisibility(View.GONE);
                break;
            case STATUS_CONFIRMED:
                giftStatus.setVisibility(View.GONE);
                break;
            case STATUS_CANCELED:
                giftStatus.setVisibility(View.VISIBLE);
                giftStatus.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.warning), null, null, null);
                giftStatus.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_5dp));
                giftStatus.setText(context.getResources().getString(R.string.str_mine_gift_canceled));
                giftStatus.setTextColor(context.getResources().getColor(R.color.color_505050));
                break;
        }

        ((TextView) viewHolder.getView(R.id.gift_cost)).setText(
                context.getResources().getString(R.string.str_mine_gift_cost, item.getGift().getCost()));

        viewHolder.setText(R.id.gift_time, TimeUtil.handleTime(context, item.getGift().getCreateTime()));

        if (!StringUtil.isEmpty(item.getGift().getDesc())) {
            viewHolder.setText(R.id.gift_desc, item.getGift().getDesc());
        } else {
            viewHolder.setText(R.id.gift_desc, "暂无");
        }

        final RelativeLayout expandDetailLayout = (RelativeLayout) viewHolder.getView(R.id.expand_detail_layout);
        final LinearLayout expandLayout = (LinearLayout) viewHolder.getView(R.id.expand_layout);
        final RelativeLayout backDetailLayout = (RelativeLayout) viewHolder.getView(R.id.back_detail_layout);

        expandDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandDetailLayout.setVisibility(View.GONE);
                expandLayout.setVisibility(View.VISIBLE);
            }
        });

        backDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandLayout.setVisibility(View.GONE);
                expandDetailLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
