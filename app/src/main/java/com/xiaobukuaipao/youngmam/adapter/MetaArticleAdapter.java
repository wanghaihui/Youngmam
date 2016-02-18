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
import com.xiaobukuaipao.youngmam.domain.MetaArticle;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener;
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
public class MetaArticleAdapter extends YmamBaseAdapter<MetaArticle> implements LabelListView2.OnLabelClickListener {

    public OnActionClickListener onActionClickListener;

    public void setOnActionClickListener(OnActionClickListener onActionClickListener) {
        this.onActionClickListener = onActionClickListener;
    }

    public MetaArticleAdapter(Context context, List<MetaArticle> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

    }

    @Override
    public void convert(final ViewHolder viewHolder, final MetaArticle item, final int position) {
        if (item.getArticle() != null) {
            final Article article = item.getArticle();

            if (article.getImgs() != null && article.getImgs().size() > 0) {

                Glide.with(context).load(article.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                        .placeholder(R.mipmap.default_loading)
                        .into((ImageView) viewHolder.getView(R.id.img_fresh));
            }

            final RoundedImageView imageView = (RoundedImageView) viewHolder.getView(R.id.avatar);
            if (article.getUserBase() != null) {
                Picasso.with(context).load(article.getUserBase().getHeadUrl())
                        .placeholder(R.mipmap.default_article_mama)
                        .into(imageView);

                viewHolder.setText(R.id.name, article.getUserBase().getName());
            }

            viewHolder.setText(R.id.time, TimeUtil.handleTime(context, article.getCreateTime()));

            /**
             * 文字内容
             */
            if (article.getContent() != null) {
                ((TextView) viewHolder.getView(R.id.introduce)).setVisibility(View.VISIBLE);
                viewHolder.setText(R.id.introduce, article.getContent());
            } else {
                ((TextView) viewHolder.getView(R.id.introduce)).setVisibility(View.GONE);
            }

            /**
             * 添加标签
             */
            if (article.getTags() != null && article.getTags().size() > 0) {
                // 此时,显示标签
                ((RelativeLayout) viewHolder.getView(R.id.label_layout)).setVisibility(View.VISIBLE);

                final LabelListView2 labelListView2 = (LabelListView2) viewHolder.getView(R.id.article_label_list);

                ArrayList<Label> labels = new ArrayList<Label>();
                for(int i = 0; i < article.getTags().size(); i++) {
                    labels.add(new Label(article.getTags().getJSONObject(i)));
                }

                labelListView2.setLabels(labels);
                labelListView2.setOnLabelClickListener(this);
            } else {
                ((RelativeLayout) viewHolder.getView(R.id.label_layout)).setVisibility(View.GONE);
            }

            final TextView mLikeView = (TextView) viewHolder.getView(R.id.like);

            if (article.getHasLiked()) {
                mLikeView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_press), null, null, null);
            } else {
                mLikeView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_normal), null, null, null);
            }

            if (article.getLikeCount() > 0) {
                viewHolder.setText(R.id.like, context.getResources().getString(R.string.str_like_num) + " " + article.getLikeCount());
            } else {
                viewHolder.setText(R.id.like, context.getResources().getString(R.string.str_like_num));
            }

            if (article.getCommentCount() > 0) {
                viewHolder.setText(R.id.comment, context.getResources().getString(R.string.str_comment_num) + " " + article.getCommentCount());
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
                    onActionClickListener.onActionClick(OnActionClickListener.ACTION_LIKE, position, article);
                }
            });

            commentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(context, "doCommentBtnClicked");
                    onActionClickListener.onActionClick(OnActionClickListener.ACTION_COMMENT, position, article);
                }
            });

            shareView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(context, "docShareBtnClicked");
                    onActionClickListener.onActionClick(OnActionClickListener.ACTION_SHARE, position, article);
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherActivity.class);
                    intent.putExtra("userId", article.getUserBase().getUserId());
                    intent.putExtra("userName", article.getUserBase().getName());
                    context.startActivity(intent);
                }
            });
        }

        final TextView title = (TextView) viewHolder.getView(R.id.title);
        final TextView desc = (TextView) viewHolder.getView(R.id.desc);

        if (!StringUtil.isEmpty(item.getTitle())) {
            title.setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.title, item.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        if (!StringUtil.isEmpty(item.getItemDesc())) {
            desc.setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.desc, item.getItemDesc());
        } else {
            desc.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLabelClick(TextView labelView, Label label) {
        Theme theme = new Theme(null, null, new Label(label.getId(), label.getTitle()));

        Intent intent = new Intent(context, SearchDetailActivity.class);
        intent.putExtra("tag_id", theme.getTag().getId());
        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
        context.startActivity(intent);
    }
}
