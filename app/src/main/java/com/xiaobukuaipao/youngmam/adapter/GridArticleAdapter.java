package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.youngmam.FreshDetailsActivity;
import com.xiaobukuaipao.youngmam.OtherActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;

import java.util.List;

/**
 * Created by xiaobu1 on 15-6-30.
 */
public class GridArticleAdapter extends YmamBaseAdapter<Article> {
    private static final String TAG = GridArticleAdapter.class.getSimpleName();

    public OnActionClickListener onActionClickListener;

    public void setOnActionClickListener(OnActionClickListener onActionClickListener) {
        this.onActionClickListener = onActionClickListener;
    }

    public GridArticleAdapter(Context context, List<Article> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

    }

    @Override
    public void convert(final ViewHolder viewHolder, final Article item, final int position) {

        final LinearLayout gridItem = (LinearLayout) viewHolder.getView(R.id.grid_item);

        Log.d(TAG, "position : " + position);

        final RoundedImageView avatar = (RoundedImageView) viewHolder.getView(R.id.avatar);
        if (item.getUserBase() != null) {
            Picasso.with(context)
                    .load(item.getUserBase().getHeadUrl())
                    .placeholder(R.mipmap.default_article_mama)
                    .fit()
                    .centerCrop()
                    .into(avatar);

            viewHolder.setText(R.id.name, item.getUserBase().getName());

            if (item.getUserBase().getExpertType() != 0) {
                ((ImageView) viewHolder.getView(R.id.geek)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) viewHolder.getView(R.id.geek)).setVisibility(View.GONE);
            }
        }

        if (item.getImgs() != null && item.getImgs().size() > 0) {
            Glide.with(context)
                    .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                    .placeholder(R.mipmap.default_loading)
                    .centerCrop()
                    .dontAnimate()
                    .into((ImageView) viewHolder.getView(R.id.img_fresh));
        }

        if (item.getLikeCount() > 0) {
            viewHolder.setText(R.id.like, String.valueOf(item.getLikeCount()));
        } else {
            viewHolder.setText(R.id.like, context.getResources().getString(R.string.str_like_num));
        }

        if (item.getCommentCount() > 0) {
            viewHolder.setText(R.id.comment, String.valueOf(item.getCommentCount()));
        } else {
            viewHolder.setText(R.id.comment, context.getResources().getString(R.string.str_comment_num));
        }

        final TextView likeView = (TextView) viewHolder.getView(R.id.like);
        final TextView commentView = (TextView) viewHolder.getView(R.id.comment);

        if (item.getHasLiked()) {
            likeView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_press), null, null, null);
        } else {
            likeView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_normal), null, null, null);
        }

        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(context, "doLikeBtnClicked");
                onActionClickListener.onActionClick(OnActionClickListener.ACTION_LIKE, position, item);
            }
        });

        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(context, "doCommentBtnClicked");
                onActionClickListener.onActionClick(OnActionClickListener.ACTION_COMMENT, position, item);
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OtherActivity.class);
                intent.putExtra("userId", item.getUserBase().getUserId());
                intent.putExtra("userName", item.getUserBase().getName());
                context.startActivity(intent);
            }
        });


        gridItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    Intent intent = new Intent(context, FreshDetailsActivity.class);
                    intent.putExtra("article_id", item.getArticleId());
                    context.startActivity(intent);
                }
            }
        });

    }
}
