package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.youngmam.OtherActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SearchDetailActivity;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;
import com.xiaobukuaipao.youngmam.widget.LabelListView2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-16.
 */
public class ArticleAdapter extends YmamBaseAdapter<Article> implements LabelListView2.OnLabelClickListener {

    public OnActionClickListener onActionClickListener;

    private static final int MAX_SIZE = 2048;

    public void setOnActionClickListener(OnActionClickListener onActionClickListener) {
        this.onActionClickListener = onActionClickListener;
    }

    public ArticleAdapter(Context context, List<Article> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Article item, final int position) {
        if (item.getImgs() != null && item.getImgs().size() > 0) {
            int width = DisplayUtil.getScreenWidth(context) - 2 * DisplayUtil.dip2px(context, 10);

            if (width > MAX_SIZE) {
                width = MAX_SIZE;
            }

            if (item.getImgs().getJSONObject(0).getInteger(GlobalConstants.JSON_IMGHEIGHT) > 0 &&
                    item.getImgs().getJSONObject(0).getInteger(GlobalConstants.JSON_IMGWIDTH) > 0) {

                int height = width * item.getImgs().getJSONObject(0).getInteger(GlobalConstants.JSON_IMGHEIGHT) /
                        item.getImgs().getJSONObject(0).getInteger(GlobalConstants.JSON_IMGWIDTH);

                if (height > MAX_SIZE) {
                    height = MAX_SIZE;
                }

                Glide.with(context)
                        .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                        .placeholder(R.mipmap.default_loading)
                        .error(R.mipmap.default_loading)
                        .override(width, height)
                        .centerCrop()
                        .dontAnimate()
                        .into((ImageView) viewHolder.getView(R.id.img_fresh));
            } else {

                Glide.with(context)
                        .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                        .placeholder(R.mipmap.default_loading)
                        .error(R.mipmap.default_loading)
                        .override(width, width)
                        .centerCrop()
                        .dontAnimate()
                        .into((ImageView) viewHolder.getView(R.id.img_fresh));
            }
        }

        final RoundedImageView imageView = (RoundedImageView) viewHolder.getView(R.id.avatar);
        if (item.getUserBase() != null) {
            Picasso.with(context).
                    load(item.getUserBase().getHeadUrl())
                    .placeholder(R.mipmap.default_article_mama)
                    .fit()
                    .centerCrop()
                    .into(imageView);

            viewHolder.setText(R.id.name, item.getUserBase().getName());

            if (item.getUserBase().getExpertType() != 0) {
                ((ImageView) viewHolder.getView(R.id.geek)).setVisibility(View.VISIBLE);
            } else {
                ((ImageView) viewHolder.getView(R.id.geek)).setVisibility(View.GONE);
            }
        }

        viewHolder.setText(R.id.time, TimeUtil.handleTime(context, item.getCreateTime()));

        /**
         * 文字内容
         */
        if (!StringUtil.isEmpty(item.getContent())) {
            ((TextView) viewHolder.getView(R.id.introduce)).setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.introduce, item.getContent());
        } else {
            ((TextView) viewHolder.getView(R.id.introduce)).setVisibility(View.GONE);
        }

        /**
         * 添加标签
         */
        if (item.getTags() != null && item.getTags().size() > 0) {
            // 此时,显示标签
            ((RelativeLayout) viewHolder.getView(R.id.label_layout)).setVisibility(View.VISIBLE);

            final LabelListView2 labelListView2 = (LabelListView2) viewHolder.getView(R.id.article_label_list);

            ArrayList<Label> labels = new ArrayList<Label>();
            for(int i = 0; i < item.getTags().size(); i++) {
                labels.add(new Label(item.getTags().getJSONObject(i)));
            }

            labelListView2.setLabels(labels);
            labelListView2.setOnLabelClickListener(this);
        } else {
            ((RelativeLayout) viewHolder.getView(R.id.label_layout)).setVisibility(View.GONE);
        }

        final TextView mLikeView = (TextView) viewHolder.getView(R.id.like);

        if (item.getHasLiked()) {
            mLikeView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_press), null, null, null);
        } else {
            mLikeView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_normal), null, null, null);
        }

        if (item.getLikeCount() > 0) {
            viewHolder.setText(R.id.like, context.getResources().getString(R.string.str_like_num) + " " + item.getLikeCount());
        } else {
            viewHolder.setText(R.id.like, context.getResources().getString(R.string.str_like_num));
        }

        if (item.getCommentCount() > 0) {
            viewHolder.setText(R.id.comment, context.getResources().getString(R.string.str_comment_num) + " " + item.getCommentCount());
        } else {
            viewHolder.setText(R.id.comment, context.getResources().getString(R.string.str_comment_num));
        }

        final TextView likeView = (TextView) viewHolder.getView(R.id.like);
        final TextView commentView = (TextView) viewHolder.getView(R.id.comment);
        final TextView shareView = (TextView) viewHolder.getView(R.id.share);

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

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(context, "docShareBtnClicked");
                onActionClickListener.onActionClick(OnActionClickListener.ACTION_SHARE, position, item);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OtherActivity.class);
                intent.putExtra("userId", item.getUserBase().getUserId());
                intent.putExtra("userName", item.getUserBase().getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public void onLabelClick(TextView labelView, Label label) {

        Theme theme = new Theme(null, null, new Label(label.getId(), label.getTitle()));

        Intent intent = new Intent(context, SearchDetailActivity.class);
        intent.putExtra("tag_id", label.getId());
        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
        context.startActivity(intent);
    }
}
