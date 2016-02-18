package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.RegisterAndLoginActivity;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.SquareLayout2;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class TopicAdapter extends YmamBaseAdapter<Topic> {

    private OnThemeLikeClickListener onThemeLikeClickListener;

    public void setOnThemeLikeClickListener(OnThemeLikeClickListener onThemeLikeClickListener) {
        this.onThemeLikeClickListener = onThemeLikeClickListener;
    }

    public TopicAdapter(Context context, List<Topic> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

    }

    @Override
    public void convert(final ViewHolder viewHolder, final Topic item, final int position) {

        final ImageView imageView = (ImageView) viewHolder.getView(R.id.image_item);

        int width = DisplayUtil.getScreenWidth(context) - 2 * DisplayUtil.dip2px(context, 10);

        /*Glide.with(context)
                .load(item.getPosterUrl())
                .override(width, width * 345 / 600)
                .centerCrop()
                .crossFade()
                .into(imageView);*/
        Picasso.with(context)
                .load(item.getPosterUrl())
                .placeholder(R.mipmap.default_loading)
                .error(R.mipmap.default_loading)
                .fit()
                .centerCrop()
                .into(imageView);

        final ImageView themeLike = (ImageView) viewHolder.getView(R.id.theme_like);
        if (item.isHasLiked()) {
            themeLike.setImageResource(R.mipmap.theme_like_press);
        } else {
            themeLike.setImageResource(R.mipmap.theme_like_normal);
        }

        viewHolder.setText(R.id.theme_like_count, String.valueOf(item.getLikeCount()));

        TextView categoryName = (TextView) viewHolder.getView(R.id.category_name);
        TextView desc = (TextView) viewHolder.getView(R.id.desc);
        if (item.getCategoryObj() != null) {
            categoryName.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
            categoryName.setText(item.getCategoryObj().getString(GlobalConstants.JSON_NAME));
            categoryName.setTextColor(Color.parseColor("#" + item.getCategoryObj().getString(GlobalConstants.JSON_RGBCOLOR)));

            GradientDrawable gradientDrawable = (GradientDrawable) categoryName.getBackground();
            gradientDrawable.setStroke(2,
                    Color.parseColor("#" + item.getCategoryObj().getString(GlobalConstants.JSON_RGBCOLOR)));

            desc.setText(item.getDesc());
        } else {
            categoryName.setVisibility(View.GONE);
            desc.setVisibility(View.GONE);
        }

        final SquareLayout2 squareLayout2 = viewHolder.getView(R.id.square_layout);

        squareLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = context.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    onThemeLikeClickListener.onThemeLikeClick(item);
                } else {
                    // 跳到登录页
                    Intent intent = new Intent(context, RegisterAndLoginActivity.class);
                    context.startActivity(intent);
                }

            }
        });

    }

    public interface OnThemeLikeClickListener {
        public void onThemeLikeClick(Topic topic);
    }
}
