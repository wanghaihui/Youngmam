package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.OtherActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.UserBase;

import java.util.List;

/**
 * Created by xiaobu1 on 15-8-4.
 */
public class HorizontalImageAdapter extends RecyclerView.Adapter<HorizontalImageAdapter.ViewHolder> {

    private Context context;
    private List<UserBase> mDatas;

    public HorizontalImageAdapter(Context context, List<UserBase> members) {
        this.context = context;
        this.mDatas = members;
    }

    @Override
    public HorizontalImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_32dp_image, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.imageView = (ImageView) v.findViewById(R.id.image_item);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context)
                .load(mDatas.get(position).getHeadUrl())
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OtherActivity.class);
                intent.putExtra("userId", mDatas.get(position).getUserId());
                intent.putExtra("userName", mDatas.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        ImageView imageView;
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }
}
