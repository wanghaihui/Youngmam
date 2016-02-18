package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Expert;

import java.util.List;

/**
 * Created by xiaobu1 on 15-6-23.
 */
public class SelectGeekAdapter extends YmamBaseAdapter<Expert> {
    private static final String TAG = SelectGeekAdapter.class.getSimpleName();

    public SelectGeekAdapter(Context context, List<Expert> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Expert item, final int position) {
        Log.d(TAG, "headUrl : " + item.getHeadUrl());
        final ImageView avatar = (ImageView) viewHolder.getView(R.id.avatar);

        Glide.with(context)
                .load(item.getHeadUrl())
                .asBitmap()
                .centerCrop()
                .placeholder(R.mipmap.default_mama)
                .into(new BitmapImageViewTarget(avatar));
        /*Picasso.with(context)
                .load(item.getHeadUrl())
                .placeholder(R.mipmap.default_mama)
                .fit()
                .centerCrop()
                .into(avatar);*/

                ((TextView) viewHolder.getView(R.id.name)).setText(item.getName());

        ((TextView) viewHolder.getView(R.id.title)).setText(item.getExpertName());
    }
}
