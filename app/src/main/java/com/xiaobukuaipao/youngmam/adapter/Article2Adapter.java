package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.youngmam.FreshDetailsActivity;
import com.xiaobukuaipao.youngmam.OtherActivity;
import com.xiaobukuaipao.youngmam.QuestionDetailsActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SearchDetailActivity;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.SizeImage;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener2;
import com.xiaobukuaipao.youngmam.listener.OnFollowClickListener;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;
import com.xiaobukuaipao.youngmam.widget.LabelListView2;
import com.xiaobukuaipao.youngmam.widget.NineGridLayout;
import com.xiaobukuaipao.youngmam.widget.SquareLayout2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-9-25.
 */
public class Article2Adapter extends BaseAdapter implements LabelListView2.OnLabelClickListener2 {

    public static final int TYPE_COUNT = 2;

    // 正常的图片
    public static final int TYPE_ARTICLE_PHOTO = 0;
    // 问答
    public static final int TYPE_ARTICLE_QUESTION = 1;

    public static final int TYPE_ARTICLE_PHOTO_SERVER = 6;
    public static final int TYPE_ARTICLE_QUESTION_SERVER = 8;

    private Context context;
    private List<Article> data;

    private LayoutInflater inflater;

    private static final int MAX_SIZE = 2048;

    public OnActionClickListener onActionClickListener;

    private FragmentManager fragmentManager;

    private boolean showPublisher = true;

    public OnFollowClickListener onFollowClickListener;

    private SharedPreferences sp;

    private OnActionClickListener2 onActionClickListener2;

    public void setOnActionClickListener(OnActionClickListener onActionClickListener) {
        this.onActionClickListener = onActionClickListener;
    }

    public void setOnFollowClickListener(OnFollowClickListener onFollowClickListener) {
        this.onFollowClickListener = onFollowClickListener;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setOnActionClickListener2(OnActionClickListener2 onActionClickListener2) {
        this.onActionClickListener2 = onActionClickListener2;
    }

    public Article2Adapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
        sp = context.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
    }

    public Article2Adapter(Context context, List<Article> data, boolean showPublisher) {
        this(context, data);
        this.showPublisher = showPublisher;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).getBusinessType();

        if (type == TYPE_ARTICLE_PHOTO_SERVER) {
            return TYPE_ARTICLE_PHOTO;
        } else if (type == TYPE_ARTICLE_QUESTION_SERVER) {
            return TYPE_ARTICLE_QUESTION;
        } else {
            return TYPE_ARTICLE_PHOTO;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ArticlePhotoHolder photoViewHolder = null;
        ArticleQuestionHolder questionViewHolder = null;

        int type = getItemViewType(position);

        if (convertView == null) {
            inflater = LayoutInflater.from(context);

            switch (type) {

                case TYPE_ARTICLE_PHOTO:
                    convertView = inflater.inflate(R.layout.item_fresh, parent, false);
                    photoViewHolder = new ArticlePhotoHolder();

                    photoViewHolder.parent = (View) convertView.findViewById(R.id.parent);
                    photoViewHolder.avatar = (RoundedImageView) convertView.findViewById(R.id.avatar);
                    photoViewHolder.name = (TextView) convertView.findViewById(R.id.name);
                    photoViewHolder.geek = (ImageView) convertView.findViewById(R.id.geek);

                    photoViewHolder.follow = (TextView) convertView.findViewById(R.id.follow);

                    photoViewHolder.time = (TextView) convertView.findViewById(R.id.time);
                    photoViewHolder.introduce = (TextView) convertView.findViewById(R.id.introduce);
                    photoViewHolder.labels = (LabelListView2) convertView.findViewById(R.id.article_label_list);
                    photoViewHolder.share = (TextView) convertView.findViewById(R.id.share);
                    photoViewHolder.photo = (ImageView) convertView.findViewById(R.id.img_fresh);
                    photoViewHolder.like = (TextView) convertView.findViewById(R.id.like);
                    photoViewHolder.comment = (TextView) convertView.findViewById(R.id.comment);

                    photoViewHolder.squareLayout2 = (SquareLayout2) convertView.findViewById(R.id.square_layout);
                    photoViewHolder.photoCount = (TextView) convertView.findViewById(R.id.photo_count);

                    convertView.setTag(photoViewHolder);
                    break;

                case TYPE_ARTICLE_QUESTION:
                    if (showPublisher) {
                        convertView = inflater.inflate(R.layout.item_question, parent, false);
                    } else {
                        convertView = inflater.inflate(R.layout.item_question_publish, parent, false);
                    }
                    questionViewHolder = new ArticleQuestionHolder();

                    questionViewHolder.parent = (View) convertView.findViewById(R.id.parent);
                    questionViewHolder.avatar = (RoundedImageView) convertView.findViewById(R.id.avatar);
                    questionViewHolder.name = (TextView) convertView.findViewById(R.id.name);
                    questionViewHolder.geek = (ImageView) convertView.findViewById(R.id.geek);

                    questionViewHolder.follow = (TextView) convertView.findViewById(R.id.follow);

                    questionViewHolder.time = (TextView) convertView.findViewById(R.id.time);
                    questionViewHolder.introduce = (TextView) convertView.findViewById(R.id.introduce);
                    questionViewHolder.labels = (LabelListView2) convertView.findViewById(R.id.article_label_list);
                    questionViewHolder.share = (TextView) convertView.findViewById(R.id.share);
                    questionViewHolder.nineGridLayout = (NineGridLayout) convertView.findViewById(R.id.nine_grid_layout);
                    questionViewHolder.answer = (TextView) convertView.findViewById(R.id.answer);

                    convertView.setTag(questionViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case TYPE_ARTICLE_PHOTO:
                    photoViewHolder = (ArticlePhotoHolder) convertView.getTag();
                    break;
                case TYPE_ARTICLE_QUESTION:
                    questionViewHolder = (ArticleQuestionHolder) convertView.getTag();
                    break;
            }
        }

        // 当前项
        final Article item = data.get(position);

        /**
         * 赋值
         */
        switch (type) {
            case TYPE_ARTICLE_PHOTO:

                if (item.getImgs() != null && item.getImgs().size() > 0) {

                    if (item.getImgs().size() > 1) {
                        photoViewHolder.squareLayout2.setVisibility(View.VISIBLE);
                        photoViewHolder.photoCount.setText(String.valueOf(item.getImgs().size()));
                    } else {
                        // 等于1
                        photoViewHolder.squareLayout2.setVisibility(View.INVISIBLE);
                    }

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

                        /*Picasso.with(context)
                                .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                                .placeholder(R.mipmap.default_loading)
                                .error(R.mipmap.default_loading)
                                .resize(width, height)
                                .centerCrop()
                                .into(photoViewHolder.photo);*/
                        Glide.with(context)
                                .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                                .placeholder(R.mipmap.default_loading)
                                .error(R.mipmap.default_loading)
                                .override(width, height)
                                .centerCrop()
                                .dontAnimate()
                                .into(photoViewHolder.photo);
                    } else {

                        /*Picasso.with(context)
                                .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                                .placeholder(R.mipmap.default_loading)
                                .error(R.mipmap.default_loading)
                                .resize(width, width)
                                .centerCrop()
                                .into(photoViewHolder.photo);*/
                        Glide.with(context)
                                .load(item.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL))
                                .placeholder(R.mipmap.default_loading)
                                .error(R.mipmap.default_loading)
                                .override(width, width)
                                .centerCrop()
                                .dontAnimate()
                                .into(photoViewHolder.photo);
                    }
                } else {
                    // 此时, 没有照片
                }

                if (item.getUserBase() != null) {
                    Picasso.with(context).
                            load(item.getUserBase().getHeadUrl())
                            .placeholder(R.mipmap.default_article_mama)
                            .fit()
                            .centerCrop()
                            .into(photoViewHolder.avatar);

                    photoViewHolder.name.setText(item.getUserBase().getName());

                    if (item.getUserBase().getExpertType() != 0) {
                        photoViewHolder.geek.setVisibility(View.VISIBLE);
                    } else {
                        photoViewHolder.geek.setVisibility(View.GONE);
                    }

                    if (sp.getLong(SplashActivity.UID, 0) > 0) {

                        if (!item.getUserBase().getUserId().equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {

                            photoViewHolder.follow.setVisibility(View.VISIBLE);

                            if (item.getUserBase().isHasFollowed()) {
                                // 此时已经关注
                                photoViewHolder.follow.setSelected(true);
                                photoViewHolder.follow.setText(context.getResources().getString(R.string.has_followed));
                                photoViewHolder.follow.setTextColor(context.getResources().getColor(R.color.color_ff4c51));

                                photoViewHolder.follow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 取消关注
                                        onFollowClickListener.onFollowClick(item.getUserBase().getUserId(),
                                                OnFollowClickListener.TYPE_FOLLOW_DELETE);
                                    }
                                });
                            } else {
                                // 此时没有关注
                                photoViewHolder.follow.setSelected(false);
                                photoViewHolder.follow.setText(context.getResources().getString(R.string.add_follow));
                                photoViewHolder.follow.setTextColor(context.getResources().getColor(R.color.white));

                                photoViewHolder.follow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 加关注
                                        onFollowClickListener.onFollowClick(item.getUserBase().getUserId(),
                                                OnFollowClickListener.TYPE_FOLLOW_ADD);
                                    }
                                });
                            }
                        } else {
                            photoViewHolder.follow.setVisibility(View.GONE);
                        }
                    } else {
                        photoViewHolder.follow.setVisibility(View.GONE);
                    }
                }

                photoViewHolder.time.setText(TimeUtil.handleTime(context, item.getCreateTime()));

                /**
                 * 文字内容
                 */
                if (!StringUtil.isEmpty(item.getContent())) {
                    photoViewHolder.introduce.setVisibility(View.VISIBLE);
                    photoViewHolder.introduce.setText(item.getContent());
                } else {
                    photoViewHolder.introduce.setVisibility(View.GONE);
                }

                /**
                 * 添加标签
                 */
                if (item.getTags() != null && item.getTags().size() > 0) {
                    // 此时,显示标签
                    ((View) photoViewHolder.labels.getParent()).setVisibility(View.VISIBLE);

                    ArrayList<Label> labels = new ArrayList<Label>();
                    for(int i = 0; i < item.getTags().size(); i++) {
                        labels.add(new Label(item.getTags().getJSONObject(i)));
                    }

                    photoViewHolder.labels.setLabels(labels);
                    photoViewHolder.labels.setType(SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                    photoViewHolder.labels.setOnLabelClickListener2(this);
                } else {
                    ((View) photoViewHolder.labels.getParent()).setVisibility(View.GONE);
                }

                if (item.getHasLiked()) {
                    photoViewHolder.like.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_press), null, null, null);
                } else {
                    photoViewHolder.like.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.like_normal), null, null, null);
                }

                if (item.getLikeCount() > 0) {
                    photoViewHolder.like.setText(context.getResources().getString(R.string.str_like_num) + " " + item.getLikeCount());
                } else {
                    photoViewHolder.like.setText(context.getResources().getString(R.string.str_like_num));
                }

                if (item.getCommentCount() > 0) {
                    photoViewHolder.comment.setText(context.getResources().getString(R.string.str_comment_num) + " " + item.getCommentCount());
                } else {
                    photoViewHolder.comment.setText(context.getResources().getString(R.string.str_comment_num));
                }

                photoViewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(context, "doLikeBtnClicked");
                        onActionClickListener.onActionClick(OnActionClickListener.ACTION_LIKE, position, item);
                    }
                });

                photoViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(context, "doCommentBtnClicked");
                        onActionClickListener.onActionClick(OnActionClickListener.ACTION_COMMENT, position, item);
                    }
                });

                photoViewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(context, "docShareBtnClicked");
                        onActionClickListener.onActionClick(OnActionClickListener.ACTION_SHARE, position, item);
                    }
                });

                photoViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OtherActivity.class);
                        intent.putExtra("userId", item.getUserBase().getUserId());
                        intent.putExtra("userName", item.getUserBase().getName());
                        context.startActivity(intent);
                    }
                });

                photoViewHolder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FreshDetailsActivity.class);
                        intent.putExtra("article_id", item.getArticleId());
                        context.startActivity(intent);
                    }
                });

                break;

            case TYPE_ARTICLE_QUESTION:

                if (showPublisher) {
                    ((View) questionViewHolder.avatar.getParent()).setVisibility(View.VISIBLE);

                    if (item.getUserBase() != null) {
                        Picasso.with(context).
                                load(item.getUserBase().getHeadUrl())
                                .placeholder(R.mipmap.default_article_mama)
                                .fit()
                                .centerCrop()
                                .into(questionViewHolder.avatar);

                        questionViewHolder.name.setText(item.getUserBase().getName());

                        if (item.getUserBase().getExpertType() != 0) {
                            questionViewHolder.geek.setVisibility(View.VISIBLE);
                        } else {
                            questionViewHolder.geek.setVisibility(View.GONE);
                        }

                        if (sp.getLong(SplashActivity.UID, 0) > 0) {

                            if (!item.getUserBase().getUserId().equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {

                                questionViewHolder.follow.setVisibility(View.VISIBLE);

                                if (item.getUserBase().isHasFollowed()) {
                                    // 此时已经关注
                                    questionViewHolder.follow.setSelected(true);
                                    questionViewHolder.follow.setText(context.getResources().getString(R.string.has_followed));
                                    questionViewHolder.follow.setTextColor(context.getResources().getColor(R.color.color_ff4c51));

                                    questionViewHolder.follow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 此时, 取消关注
                                            onFollowClickListener.onFollowClick(item.getUserBase().getUserId(),
                                                    OnFollowClickListener.TYPE_FOLLOW_DELETE);
                                        }
                                    });
                                } else {
                                    // 此时没有关注
                                    questionViewHolder.follow.setSelected(false);
                                    questionViewHolder.follow.setText(context.getResources().getString(R.string.add_follow));
                                    questionViewHolder.follow.setTextColor(context.getResources().getColor(R.color.white));

                                    questionViewHolder.follow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // 此时, 加关注
                                            onFollowClickListener.onFollowClick(item.getUserBase().getUserId(),
                                                    OnFollowClickListener.TYPE_FOLLOW_ADD);
                                        }
                                    });
                                }
                            } else {
                                questionViewHolder.follow.setVisibility(View.GONE);
                            }
                        } else {
                            questionViewHolder.follow.setVisibility(View.GONE);
                        }

                    }
                } else {
                    ((View) questionViewHolder.avatar.getParent()).setVisibility(View.GONE);
                }

                questionViewHolder.time.setText(TimeUtil.handleTime(context, item.getCreateTime()));

                /**
                 * 文字内容
                 */
                if (!StringUtil.isEmpty(item.getContent())) {
                    questionViewHolder.introduce.setVisibility(View.VISIBLE);
                    questionViewHolder.introduce.setText(item.getContent());
                } else {
                    questionViewHolder.introduce.setVisibility(View.GONE);
                }

                if (item.getImgs() != null && item.getImgs().size() > 0) {
                    ArrayList<SizeImage> images = new ArrayList<SizeImage>();

                    int length = item.getImgs().size();

                    for(int i=0; i < length; i++) {
                        images.add(new SizeImage(item.getImgs().getJSONObject(i)));
                    }

                    questionViewHolder.nineGridLayout.setVisibility(View.VISIBLE);
                    questionViewHolder.nineGridLayout.setImageDatas(images, fragmentManager);
                } else {
                    questionViewHolder.nineGridLayout.setVisibility(View.GONE);
                }

                /**
                 * 添加标签
                 */
                if (item.getTags() != null && item.getTags().size() > 0) {
                    // 此时,显示标签
                    ((View) questionViewHolder.labels.getParent()).setVisibility(View.VISIBLE);

                    ArrayList<Label> labels = new ArrayList<Label>();
                    for(int i = 0; i < item.getTags().size(); i++) {
                        labels.add(new Label(item.getTags().getJSONObject(i)));
                    }

                    questionViewHolder.labels.setLabels(labels);
                    questionViewHolder.labels.setType(SearchDetailActivity.TYPE_BUSINESS_QUESTION);
                    questionViewHolder.labels.setOnLabelClickListener2(this);
                } else {
                    ((View) questionViewHolder.labels.getParent()).setVisibility(View.GONE);
                }

                if (item.getCommentCount() > 0) {
                    questionViewHolder.answer.setText(context.getResources().getString(R.string.str_answer_num) + " " + item.getCommentCount());
                } else {
                    questionViewHolder.answer.setText(context.getResources().getString(R.string.str_answer_num));
                }

                questionViewHolder.answer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onActionClickListener2.onActionClick2(OnActionClickListener2.ACTION_REPLY, position, item);
                    }
                });

                questionViewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onActionClickListener2.onActionClick2(OnActionClickListener2.ACTION_SHARE, position, item);
                    }
                });

                questionViewHolder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, QuestionDetailsActivity.class);
                        intent.putExtra("article_id", item.getArticleId());
                        context.startActivity(intent);
                    }
                });

                questionViewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OtherActivity.class);
                        intent.putExtra("userId", item.getUserBase().getUserId());
                        intent.putExtra("userName", item.getUserBase().getName());
                        context.startActivity(intent);
                    }
                });

                break;
        }

        return convertView;
    }

    @Override
    public void onLabelClick2(TextView labelView, Label label, int type) {
        Theme theme = new Theme(null, null, new Label(label.getId(), label.getTitle()));

        Intent intent = new Intent(context, SearchDetailActivity.class);
        intent.putExtra("tag_id", label.getId());
        intent.putExtra("share_businessType", type);
        if (type == SearchDetailActivity.TYPE_BUSINESS_QUESTION) {
            intent.putExtra("publish_type", SearchDetailActivity.TYPE_PUBLISH_QUESTION);
        }
        context.startActivity(intent);
    }

    /**
     * 基类
     */
    public static class ArticleHolder {
        View parent;

        RoundedImageView avatar;
        TextView name;
        ImageView geek;
        TextView time;

        // 关注
        TextView follow;

        // 内容
        TextView introduce;
        // 标签
        LabelListView2 labels;
        // 分享
        TextView share;
    }

    public static class ArticlePhotoHolder extends ArticleHolder {
        ImageView photo;
        SquareLayout2 squareLayout2;
        TextView photoCount;
        // 点赞
        TextView like;
        // 评论
        TextView comment;
    }

    public static class ArticleQuestionHolder extends ArticleHolder {
        // 九宫格
        NineGridLayout nineGridLayout;
        // 回答
        TextView answer;
    }

}
