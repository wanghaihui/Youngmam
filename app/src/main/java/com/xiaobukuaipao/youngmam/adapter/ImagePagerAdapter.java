package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.BannerH5Activity;
import com.xiaobukuaipao.youngmam.FreshDetailsActivity;
import com.xiaobukuaipao.youngmam.GiftCenterActivity;
import com.xiaobukuaipao.youngmam.HotTopicActivity;
import com.xiaobukuaipao.youngmam.LatestActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SearchDetailActivity;
import com.xiaobukuaipao.youngmam.SpecialTopicActivity;
import com.xiaobukuaipao.youngmam.WebPageActivity;
import com.xiaobukuaipao.youngmam.domain.BannerActivity;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.utils.ListUtils;

import java.util.List;

/**
 * Created by xiaobu1 on 15-6-18.
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {
    private static final String TAG = ImagePagerAdapter.class.getSimpleName();

    public static final int BANNER_TYPE_COUNT = 12;

    // 普通活动
    public static final int BANNER_TYPE_ACTIVITY = 0;
    // 有奖活动
    public static final int BANNER_TYPE_PRIZE_ACTIVITY = 1;
    // 专题
    public static final int BANNER_TYPE_TOPIC = 2;
    // 话题
    public static final int BANNER_TYPE_THEME = 3;
    // 积分商城
    public static final int BANNER_TYPE_BONUS_MALL = 4;
    // 用户发布的普通文章
    public static final int BANNER_TYPE_ARTICLE = 5;
    // Special专题
    public static final int BANNER_TYPE_SPECIAL = 6;
    // H5有奖活动
    public static final int BANNER_TYPE_H5_ACTIVITY = 7;
    // 内部Web页面内容
    public static final int BANNER_TYPE_H5_WEBPAGE = 8;
    // 外链页面内容
    public static final int BANNER_TYPE_WEB_URL = 9;
    // H5页面扩展
    public static final int BANNER_TYPE_H5_EXTEND = 10;

    //////////////////////////////////////////////////////////////////////////
    // 实际的Banner Type
    // 普通活动
    public static final int BANNER_STANDARD_TYPE_ACTIVITY = 1;
    // 有奖活动
    public static final int BANNER_STANDARD_TYPE_PRIZE_ACTIVITY = 2;
    // 普通专题
    public static final int BANNER_STANDARD_TYPE_TOPIC = 3;
    // 话题
    public static final int BANNER_STANDARD_TYPE_THEME = 4;
    // 商城
    public static final int BANNER_STANDARD_TYPE_BONUS_MALL = 5;
    // 用户发布的普通文章
    public static final int BANNER_STANDARD_TYPE_ARTICLE = 6;
    // Special专题
    public static final int BANNER_STANDARD_TYPE_SPECIAL = 7;
    // H5有奖活动
    public static final int BANNER_STANDARD_TYPE_H5_ACTIVITY = 1001;
    // 内部Web页面内容
    public static final int BANNER_STANDARD_TYPE_H5_WEBPAGE = 1002;
    // 外链页面内容
    public static final int BANNER_STANDARD_TYPE_WEB_URL = 1003;

    //////////////////////////////////////////////////////////////////////////

    private Context context;
    private List<BannerActivity> bannerActivityList;
    private int size;
    // 是否无限循环
    private boolean isInfiniteLoop;
    private LayoutInflater inflater;

    public ImagePagerAdapter(Context context, List<BannerActivity> bannerActivityList) {
        this.context = context;
        this.bannerActivityList = bannerActivityList;
        this.size = ListUtils.getSize(bannerActivityList);
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : ListUtils.getSize(bannerActivityList);
    }

    /**
     * get really position
     *
     * @param position
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder activityViewHolder = null;
        ViewHolder prizeActivityViewHolder = null;
        ViewHolder topicViewHolder = null;
        ViewHolder themeViewHolder = null;
        ViewHolder bonusMallViewHolder = null;
        ViewHolder articleViewHolder = null;
        ViewHolder specialViewHolder = null;
        ViewHolder h5ActivityViewHolder = null;
        ViewHolder h5WebpageViewHolder = null;
        ViewHolder webUrlViewHolder = null;
        ViewHolder h5ExtendViewHolder = null;

        ViewHolder ignoreViewHolder = null;

        int type = getItemViewType(position);

        if (view == null) {
            inflater = LayoutInflater.from(context);

            switch (type) {
                case BANNER_TYPE_ACTIVITY:
                    view = inflater.inflate(R.layout.item_banner_activity, parent, false);
                    activityViewHolder = new ViewHolder();
                    activityViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(activityViewHolder);
                    break;
                case BANNER_TYPE_PRIZE_ACTIVITY:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    prizeActivityViewHolder = new ViewHolder();
                    prizeActivityViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(prizeActivityViewHolder);
                    break;
                case BANNER_TYPE_TOPIC:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    topicViewHolder = new ViewHolder();
                    topicViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(topicViewHolder);
                    break;
                case BANNER_TYPE_THEME:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    themeViewHolder = new ViewHolder();
                    themeViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(themeViewHolder);
                    break;
                case BANNER_TYPE_BONUS_MALL:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    bonusMallViewHolder = new ViewHolder();
                    bonusMallViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(bonusMallViewHolder);
                    break;
                case BANNER_TYPE_ARTICLE:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    articleViewHolder = new ViewHolder();
                    articleViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(articleViewHolder);
                    break;
                case BANNER_TYPE_SPECIAL:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    specialViewHolder = new ViewHolder();
                    specialViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(specialViewHolder);
                    break;
                case BANNER_TYPE_H5_ACTIVITY:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    h5ActivityViewHolder = new ViewHolder();
                    h5ActivityViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(h5ActivityViewHolder);
                    break;
                case BANNER_TYPE_H5_WEBPAGE:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    h5WebpageViewHolder = new ViewHolder();
                    h5WebpageViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(h5WebpageViewHolder);
                    break;
                case BANNER_TYPE_WEB_URL:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    webUrlViewHolder = new ViewHolder();
                    webUrlViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(webUrlViewHolder);
                    break;
                case BANNER_TYPE_H5_EXTEND:
                    view = inflater.inflate(R.layout.item_banner, parent, false);
                    h5ExtendViewHolder = new ViewHolder();
                    h5ExtendViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(h5ExtendViewHolder);
                    break;
                case IGNORE_ITEM_VIEW_TYPE:
                    view = inflater.inflate(R.layout.item_banner, parent ,false);
                    ignoreViewHolder = new ViewHolder();
                    ignoreViewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
                    view.setTag(ignoreViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case BANNER_TYPE_ACTIVITY:
                    activityViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_PRIZE_ACTIVITY:
                    prizeActivityViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_TOPIC:
                    topicViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_THEME:
                    themeViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_BONUS_MALL:
                    bonusMallViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_ARTICLE:
                    articleViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_SPECIAL:
                    specialViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_H5_ACTIVITY:
                    h5ActivityViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_H5_WEBPAGE:
                    h5WebpageViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_WEB_URL:
                    webUrlViewHolder = (ViewHolder) view.getTag();
                    break;
                case BANNER_TYPE_H5_EXTEND:
                    h5ExtendViewHolder = (ViewHolder) view.getTag();
                    break;
                case IGNORE_ITEM_VIEW_TYPE:
                    ignoreViewHolder = (ViewHolder) view.getTag();
                    break;
            }
        }

        switch (type) {
            case BANNER_TYPE_ACTIVITY:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(activityViewHolder.imageView);
                // 设置监听器
                activityViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, LatestActivity.class);
                        intent.putExtra("normal_activity", bannerActivityList.get(getPosition(position)));
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_PRIZE_ACTIVITY:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(prizeActivityViewHolder.imageView);
                break;
            case BANNER_TYPE_TOPIC:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(topicViewHolder.imageView);
                topicViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, HotTopicActivity.class);
                        intent.putExtra("normal_topic", true);
                        intent.putExtra("topic_id", bannerActivityList.get(getPosition(position)).getBusinessId());
                        intent.putExtra("topic_title", bannerActivityList.get(getPosition(position)).getTitle());
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_THEME:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(themeViewHolder.imageView);
                themeViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Theme theme = new Theme(null,
                                bannerActivityList.get(getPosition(position)).getPosterUrl(),
                                new Label(bannerActivityList.get(getPosition(position)).getBusinessId(),
                                        bannerActivityList.get(getPosition(position)).getTitle()));

                        Intent intent = new Intent(context, SearchDetailActivity.class);
                        intent.putExtra("tag_id", theme.getTag().getId());
                        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_BONUS_MALL:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(bonusMallViewHolder.imageView);
                bonusMallViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GiftCenterActivity.class);
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_ARTICLE:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(articleViewHolder.imageView);

                articleViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FreshDetailsActivity.class);
                        intent.putExtra("article_id", bannerActivityList.get(getPosition(position)).getBusinessId());
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_SPECIAL:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(specialViewHolder.imageView);
                specialViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Intent intent = new Intent(context, SpecialTopicActivity.class);
                        intent.putExtra("special_topic_id", bannerActivityList.get(getPosition(position)).getBusinessId());
                        context.startActivity(intent);*/
                        Topic topic = new Topic();
                        topic.setBusinessId(bannerActivityList.get(getPosition(position)).getBusinessId());
                        topic.setBusinessType(bannerActivityList.get(getPosition(position)).getBusinessType());
                        Intent intent = new Intent(context, SpecialTopicActivity.class);
                        intent.putExtra("topic", topic);
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_H5_ACTIVITY:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(h5ActivityViewHolder.imageView);

                h5ActivityViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BannerH5Activity.class);
                        intent.putExtra("title", bannerActivityList.get(getPosition(position)).getTitle());
                        intent.putExtra("target_url", bannerActivityList.get(getPosition(position)).getTargetUrl());
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_H5_WEBPAGE:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(h5WebpageViewHolder.imageView);
                h5WebpageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, WebPageActivity.class);
                        intent.putExtra("banner_webpage", bannerActivityList.get(getPosition(position)));
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_WEB_URL:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(webUrlViewHolder.imageView);
                webUrlViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, WebPageActivity.class);
                        intent.putExtra("banner_webpage", bannerActivityList.get(getPosition(position)));
                        context.startActivity(intent);
                    }
                });
                break;
            case BANNER_TYPE_H5_EXTEND:
                Glide.with(context)
                        .load(bannerActivityList.get(getPosition(position)).getPosterUrl())
                        .centerCrop()
                        .into(h5ExtendViewHolder.imageView);
                h5ExtendViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, WebPageActivity.class);
                        intent.putExtra("banner_webpage", bannerActivityList.get(getPosition(position)));
                        context.startActivity(intent);
                    }
                });
                break;
            case IGNORE_ITEM_VIEW_TYPE:
                break;
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        // 目前是3种
        return BANNER_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (size != 0) {
            position = isInfiniteLoop ? position % size : position;

            if (bannerActivityList.get(position).getBusinessType() > 1003 && bannerActivityList.get(position).getBusinessType() < 2000) {
                return BANNER_TYPE_H5_EXTEND;
            } else {
                switch (bannerActivityList.get(position).getBusinessType()) {
                    case BANNER_STANDARD_TYPE_ACTIVITY:
                        return BANNER_TYPE_ACTIVITY;
                    case BANNER_STANDARD_TYPE_PRIZE_ACTIVITY:
                        return BANNER_TYPE_PRIZE_ACTIVITY;
                    case BANNER_STANDARD_TYPE_TOPIC:
                        return BANNER_TYPE_TOPIC;
                    case BANNER_STANDARD_TYPE_THEME:
                        return BANNER_TYPE_THEME;
                    case BANNER_STANDARD_TYPE_BONUS_MALL:
                        return BANNER_TYPE_BONUS_MALL;
                    case BANNER_STANDARD_TYPE_ARTICLE:
                        return BANNER_TYPE_ARTICLE;
                    case BANNER_STANDARD_TYPE_SPECIAL:
                        return BANNER_TYPE_SPECIAL;
                    case BANNER_STANDARD_TYPE_H5_ACTIVITY:
                        return BANNER_TYPE_H5_ACTIVITY;
                    case BANNER_STANDARD_TYPE_H5_WEBPAGE:
                        return BANNER_TYPE_H5_WEBPAGE;
                    case BANNER_STANDARD_TYPE_WEB_URL:
                        return BANNER_TYPE_WEB_URL;
                }
            }
        }

        return IGNORE_ITEM_VIEW_TYPE;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}
