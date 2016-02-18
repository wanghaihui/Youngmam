package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.UserBase;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-27.
 */
public class MemberAdapter extends YmamBaseAdapter<UserBase> {

    public MemberAdapter(Context context, List<UserBase> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

    }

    @Override
    public void convert(final ViewHolder viewHolder, final UserBase item, final int position) {

        final RoundedImageView imageView = (RoundedImageView) viewHolder.getView(R.id.avatar);
        if (!StringUtil.isEmpty(item.getHeadUrl())) {
            Picasso.with(context)
                    .load(item.getHeadUrl())
                    .fit()
                    .centerCrop()
                    .into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.default_mama);
        }

        viewHolder.setText(R.id.name, item.getName());

        switch (item.getChildStatus()) {
            case 1:
                viewHolder.setText(R.id.state, context.getResources().getString(R.string.str_prepare));
                break;
            case 2:
                viewHolder.setText(R.id.state, context.getResources().getString(R.string.str_gravida));
                break;
            case 3:
                viewHolder.setText(R.id.state, context.getResources().getString(R.string.str_hava_baby));
                break;
            default:
                viewHolder.setText(R.id.state, context.getResources().getString(R.string.str_prepare));
                break;
        }
    }

}
