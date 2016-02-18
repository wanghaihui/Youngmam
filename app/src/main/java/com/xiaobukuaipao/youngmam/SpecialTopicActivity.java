package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.greenrobot.event.EventBus;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.domain.CommentEvent;
import com.xiaobukuaipao.youngmam.domain.LikeEvent;
import com.xiaobukuaipao.youngmam.domain.SpecialCard;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.domain.TopicCommentEvent;
import com.xiaobukuaipao.youngmam.domain.TopicLikeEvent;
import com.xiaobukuaipao.youngmam.font.FontTextView;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.SpecialCardHeadView;
import com.xiaobukuaipao.youngmam.view.SpecialCardPosterView;
import com.xiaobukuaipao.youngmam.view.SpecialCardView;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-8.
 */
public class SpecialTopicActivity extends BaseHttpFragmentActivity implements YoungShareBoard.OnShareSuccessListener {

    private static final String TAG = SpecialTopicActivity.class.getSimpleName();

    public static final String TAG_POSTER = "posterTag";
    public static final String TAG_TITLE = "title";
    public static final String TAG_P = "p";
    public static final String TAG_IMGURL = "imgUrl";
    public static final String TAG_HEADLINE1 = "headline1";
    public static final String TAG_ITEMLIST = "itemList";
    public static final String TAG_NUMBULLET1 = "numBullet1";
    public static final String TAG_PRICETAG = "priceTag";
    public static final String TAG_ADDRTAG = "addrTag";
    public static final String TAG_LINK = "link";

    private LinearLayout specialTopicLayout;

    private List<SpecialCard> specialCards;

    private Topic topic;
    private String topicId;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);
    // 分享的内容

    // ActionBar
    private int leftFrameWidth;
    private int rightFrameWidth;

    private LinearLayout bottomLayout;
    private TextView likeText;
    private TextView commentText;
    private int commentCount;
    private TextView shareText;

    // EventBus
    private EventBus eventBus;

    public void initViews() {
        setContentView(R.layout.activity_special_topic);

        getIntentDatas();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        specialTopicLayout = (LinearLayout) findViewById(R.id.special_topic_layout);

        // 底部布局
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        bottomLayout.setVisibility(View.INVISIBLE);

        likeText = (TextView) findViewById(R.id.like);
        commentText = (TextView) findViewById(R.id.comment);
        shareText = (TextView) findViewById(R.id.share);

        initDatas();

        // 配置需要分享的相关平台
        configPlatforms(mController);

        // 设置监听器
        setUIListeners();
    }

    private void initDatas() {
        specialCards = new ArrayList<SpecialCard>();
    }

    public void executeHttpRequest() {
        // 获取新专题
        if (topic != null) {
            mEventLogic.getSpecialTopic(topic.getBusinessId());
            showProgress(getResources().getString(R.string.loading));
        } else if (!StringUtil.isEmpty(topicId)) {
            mEventLogic.getSpecialTopic(topicId);
            showProgress(getResources().getString(R.string.loading));
        }
    }

    private void getIntentDatas() {
        topic = getIntent().getParcelableExtra("topic");
        topicId = getIntent().getStringExtra("special_topic_id");
    }

    private void setUIListeners() {
        likeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like();
            }
        });

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });

        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void like() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            if (topic.isHasLiked()) {
                // 此时, 取消喜欢
                mEventLogic.deleteLike2(topic.getBusinessId(), String.valueOf(topic.getBusinessType()), null);
            } else {
                // 此时, 喜欢
                mEventLogic.addLike(topic.getBusinessId(), String.valueOf(topic.getBusinessType()));
            }
        } else {
            // 跳到登录页
            Intent intent = new Intent(SpecialTopicActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    private void comment() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            Intent intent = new Intent(SpecialTopicActivity.this, TopicCommentActivity.class);
            intent.putExtra("topic", topic);
            intent.putExtra("comment_count", commentCount);
            startActivity(intent);
        } else {
            // 跳到登录页
            Intent intent = new Intent(SpecialTopicActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);

        if (topic != null) {
            actionBar.setRightAction(YoungActionBar.Type.TEXT, R.drawable.general_share, getResources().getString(R.string.str_share));

            leftFrameWidth = DisplayUtil.dip2px(this, 54);
            actionBar.getRightFrame().getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            rightFrameWidth = actionBar.getRightFrame().getWidth();

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(leftFrameWidth, 0, rightFrameWidth, 0);
                            params.addRule(RelativeLayout.CENTER_IN_PARENT);
                            actionBar.getMiddleFrame().setLayoutParams(params);

                            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, topic.getTitle());
                            actionBar.getRightFrame().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });

            actionBar.getRightFrame().setVisibility(View.INVISIBLE);
        }

        setBackClickListener(this);

    }

    private void share() {
        // 分享特殊专题
        YoungShareBoard shareBoard = new YoungShareBoard(this);

        shareBoard.setShareParams(topic.getBusinessId(), topic.getBusinessType());
        shareBoard.setOnShareSuccessListener(this);

        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        setShareContent(mController, topic.getTargetUrl(), topic.getDesc(), topic.getPosterUrl(), topic.getTitle(), BaseHttpFragmentActivity.SHARE_TYPE_TOPIC);

        /**
         * 针对微博
         */
        mController.setShareContent(StringUtil.buildWeiboShareTopic(topic.getDesc(), topic.getTargetUrl()));
        mController.setShareMedia(new UMImage(this, topic.getPosterUrl()));
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_special_topic:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null && jsonObject.containsKey(GlobalConstants.JSON_DATA)) {

                        Log.d(TAG, "special topic datas : " + httpResult.getData());

                        final JSONObject totalJsonObject = jsonObject.getJSONObject(GlobalConstants.JSON_DATA);

                        topic.setDesc(totalJsonObject.getString(GlobalConstants.JSON_DESC));
                        topic.setHasLiked(totalJsonObject.getBoolean(GlobalConstants.JSON_HASLIKED));
                        topic.setLikeCount(totalJsonObject.getInteger(GlobalConstants.JSON_LIKECOUNT));
                        topic.setTitle(totalJsonObject.getString(GlobalConstants.JSON_TITLE));

                        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, totalJsonObject.getString(GlobalConstants.JSON_TITLE));

                        if (!StringUtil.isEmpty(topicId)) {
                            leftFrameWidth = DisplayUtil.dip2px(this, 54);
                            actionBar.getRightFrame().getViewTreeObserver().addOnGlobalLayoutListener(
                                    new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            rightFrameWidth = actionBar.getRightFrame().getWidth();

                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                                            params.setMargins(leftFrameWidth, 0, rightFrameWidth, 0);
                                            params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                            actionBar.getMiddleFrame().setLayoutParams(params);

                                            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, totalJsonObject.getString(GlobalConstants.JSON_TITLE));
                                            actionBar.getRightFrame().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        }
                                    });
                        }

                        bottomLayout.setVisibility(View.VISIBLE);
                        if (totalJsonObject.getBoolean(GlobalConstants.JSON_HASLIKED)) {
                            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_press), null, null, null);
                        } else {
                            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_normal), null, null, null);
                        }

                        likeText.setText(this.getResources().getString(R.string.str_like,
                                totalJsonObject.getInteger(GlobalConstants.JSON_LIKECOUNT)));

                        commentCount = totalJsonObject.getInteger(GlobalConstants.JSON_COMMENTCOUNT);
                        commentText.setText(this.getResources().getString(R.string.str_comment,
                                totalJsonObject.getInteger(GlobalConstants.JSON_COMMENTCOUNT)));

                        if (totalJsonObject.containsKey(GlobalConstants.JSON_CARDS)) {

                            JSONArray jsonArray = totalJsonObject.getJSONArray(GlobalConstants.JSON_CARDS);

                            if (jsonArray != null && jsonArray.size() > 0) {
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    specialCards.add(new SpecialCard(jsonArray.getJSONObject(i)));
                                }

                                // 开始填充数据
                                for (int i = 0; i < specialCards.size(); i++) {
                                    SpecialCard specialCard = (SpecialCard) specialCards.get(i);
                                    SpecialCardView specialCardView = new SpecialCardView(this);

                                    specialTopicLayout.addView(specialCardView);

                                    if (!StringUtil.isEmpty(specialCard.getHeadImg())) {
                                        SpecialCardHeadView specialCardHeadView = new SpecialCardHeadView(this);
                                        specialCardView.addView(specialCardHeadView);

                                        Glide.with(this)
                                                .load(specialCard.getHeadImg())
                                                .placeholder(R.mipmap.default_loading)
                                                .error(R.mipmap.default_loading)
                                                .override(specialCard.getHeadImgWidth(), specialCard.getHeadImgHeight())
                                                .centerCrop()
                                                .dontAnimate()
                                                .into(specialCardHeadView);
                                    }

                                    JSONArray items = specialCard.getItems();

                                    if (items != null && items.size() > 0) {
                                        for (int j = 0; j < items.size(); j++) {
                                            addSpecialCardContent(specialCardView, items.getJSONObject(j));
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                break;

            case R.id.share_bonus:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 3
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;

            case R.id.add_like:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String articleId = msg.getData().getString("key");
                    if (jsonObject != null) {

                        if (jsonObject.containsKey(GlobalConstants.JSON_LIKEID)) {
                            EventBus.getDefault().post(new TopicLikeEvent(articleId, LikeEvent.TYPE_ADD_LIKE));
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;

            case R.id.delete_like2:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String articleId = msg.getData().getString("key");
                    if (jsonObject != null) {

                        int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                        if (status == 0) {
                            EventBus.getDefault().post(new TopicLikeEvent(articleId, LikeEvent.TYPE_DELETE_LIKE));
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 添加卡片内容
     */
    private void addSpecialCardContent(SpecialCardView specialCardView, final JSONObject jsonObject) {
        if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_POSTER)) {
            SpecialCardPosterView specialCardPosterView = new SpecialCardPosterView(this);
            specialCardView.addView(specialCardPosterView);

            int width = DisplayUtil.getScreenWidth(this) - 2 * DisplayUtil.dip2px(this, 10);

            if (StringUtil.isEmpty(topic.getPosterUrl())) {
                topic.setPosterUrl(jsonObject.getString(GlobalConstants.JSON_POSTERURL));
            }

            Glide.with(this)
                    .load(jsonObject.getString(GlobalConstants.JSON_POSTERURL))
                    .placeholder(R.mipmap.default_loading)
                    .error(R.mipmap.default_loading)
                    .override(width, width * jsonObject.getInteger(GlobalConstants.JSON_IMGHEIGHT) / jsonObject.getInteger(GlobalConstants.JSON_IMGWIDTH))
                    .centerCrop()
                    .dontAnimate()
                    .into(specialCardPosterView.getPosterView());

            switch (jsonObject.getInteger(GlobalConstants.JSON_MARKER)) {
                case 1:
                    specialCardPosterView.getMarkerView().setVisibility(View.VISIBLE);
                    specialCardPosterView.getMarkerView().setImageResource(R.mipmap.poster_marker_recommend);
                    break;
                case 2:
                    specialCardPosterView.getMarkerView().setVisibility(View.VISIBLE);
                    specialCardPosterView.getMarkerView().setImageResource(R.mipmap.poster_marker_hot);
                    break;
                case 3:
                    specialCardPosterView.getMarkerView().setVisibility(View.VISIBLE);
                    specialCardPosterView.getMarkerView().setImageResource(R.mipmap.poster_marker_feature);
                    break;
                default:
                    specialCardPosterView.getMarkerView().setVisibility(View.GONE);
                    break;
            }

        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_TITLE)) {
            FontTextView fontTextView = (FontTextView) View.inflate(this, R.layout.special_card_title, null);
            specialCardView.addView(fontTextView);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.setMargins(0, this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp), 0, 0);
            fontTextView.setLayoutParams(params);

            fontTextView.setText(jsonObject.getString(GlobalConstants.JSON_TITLE));
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_P)) {
            FontTextView fontTextView = (FontTextView) View.inflate(this, R.layout.special_card_p, null);
            specialCardView.addView(fontTextView);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    0);
            fontTextView.setLayoutParams(params);

            fontTextView.setText(jsonObject.getString(GlobalConstants.JSON_TEXT));
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_IMGURL)) {

            ImageView ratioImageView = new ImageView(this);
            specialCardView.addView(ratioImageView);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    0);
            ratioImageView.setLayoutParams(params);

            int width = DisplayUtil.getScreenWidth(this) - 2 * DisplayUtil.dip2px(this, 10) - 2 * DisplayUtil.dip2px(this, 15);

            /*picasso.load(jsonObject.getString(GlobalConstants.JSON_IMGURL))
                    .resize(width, width * jsonObject.getInteger(GlobalConstants.JSON_IMGHEIGHT) / jsonObject.getInteger(GlobalConstants.JSON_IMGWIDTH))
                    .placeholder(R.mipmap.default_loading)
                    .centerCrop()
                    .into(ratioImageView);*/
            Glide.with(this)
                    .load(jsonObject.getString(GlobalConstants.JSON_IMGURL))
                    .override(width, width * jsonObject.getInteger(GlobalConstants.JSON_IMGHEIGHT) / jsonObject.getInteger(GlobalConstants.JSON_IMGWIDTH))
                    .centerCrop()
                    .into(ratioImageView);
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_HEADLINE1)) {
            LinearLayout headlineLayout = (LinearLayout) View.inflate(this, R.layout.special_card_headline1, null);
            specialCardView.addView(headlineLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    0);
            headlineLayout.setLayoutParams(params);

            FontTextView fontTextView = (FontTextView) headlineLayout.findViewById(R.id.head);

            fontTextView.setText(jsonObject.getString(GlobalConstants.JSON_HEAD));

            if (jsonObject.containsKey(GlobalConstants.JSON_TEXT)) {
                FontTextView fontTextViewText = (FontTextView) headlineLayout.findViewById(R.id.text);

                fontTextViewText.setText(jsonObject.getString(GlobalConstants.JSON_TEXT));
            }
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_ITEMLIST)) {

            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_ITEMLIST);

            if (jsonArray != null && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    FontTextView fontTextView = (FontTextView) View.inflate(this, R.layout.special_card_itemlist, null);
                    specialCardView.addView(fontTextView);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_25dp),
                            this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                            this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_25dp),
                            0);
                    fontTextView.setLayoutParams(params);

                    fontTextView.setText(jsonArray.getString(i));
                }
            }
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_NUMBULLET1)) {
            RelativeLayout numBulletLayout = (RelativeLayout) View.inflate(this, R.layout.special_card_numbullet1, null);
            specialCardView.addView(numBulletLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    0);
            numBulletLayout.setLayoutParams(params);

            FontTextView fontTextView = (FontTextView) numBulletLayout.findViewById(R.id.num);

            fontTextView.setText(jsonObject.getString(GlobalConstants.JSON_NUM));

            if (jsonObject.containsKey(GlobalConstants.JSON_TEXT)) {
                FontTextView fontTextViewText = (FontTextView) numBulletLayout.findViewById(R.id.text);

                fontTextViewText.setText(jsonObject.getString(GlobalConstants.JSON_TEXT));
            }
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_PRICETAG)) {
            RelativeLayout priceLayout = (RelativeLayout) View.inflate(this, R.layout.special_card_pricetag, null);
            specialCardView.addView(priceLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelSize(R.dimen.activity_basic_margin_15dp),
                    0);
            priceLayout.setLayoutParams(params);

            FontTextView fontTextView = (FontTextView) priceLayout.findViewById(R.id.price);

            fontTextView.setText(jsonObject.getString(GlobalConstants.JSON_PRICE));

            FontTextView btnText = (FontTextView) priceLayout.findViewById(R.id.btnText);
            btnText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SpecialTopicActivity.this, BusinessDetailActivity.class);
                    intent.putExtra("target_url", jsonObject.getString(GlobalConstants.JSON_TARGETURL));
                    startActivity(intent);
                }
            });
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_ADDRTAG)) {
            LinearLayout addrLayout = (LinearLayout) View.inflate(this, R.layout.special_card_addrtag, null);
            specialCardView.addView(addrLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(this.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                    0);
            addrLayout.setLayoutParams(params);

            RelativeLayout priceLayout = (RelativeLayout) addrLayout.findViewById(R.id.price_layout);
            RelativeLayout addrDetailLayout = (RelativeLayout) addrLayout.findViewById(R.id.addr_layout);
            RelativeLayout phoneLayout = (RelativeLayout) addrLayout.findViewById(R.id.phone_layout);
            RelativeLayout timeLayout = (RelativeLayout) addrLayout.findViewById(R.id.time_layout);


            FontTextView priceText = (FontTextView) addrLayout.findViewById(R.id.per_price);
            if (jsonObject.containsKey(GlobalConstants.JSON_PRICE)) {
                priceLayout.setVisibility(View.VISIBLE);
                priceText.setText(jsonObject.getString(GlobalConstants.JSON_PRICE));
            } else {
                priceLayout.setVisibility(View.GONE);
            }

            FontTextView addrText = (FontTextView) addrLayout.findViewById(R.id.addr);
            if (jsonObject.containsKey(GlobalConstants.JSON_ADDR)) {
                addrDetailLayout.setVisibility(View.VISIBLE);
                addrText.setText(jsonObject.getString(GlobalConstants.JSON_ADDR));
            } else {
                addrDetailLayout.setVisibility(View.GONE);
            }

            FontTextView phoneText = (FontTextView) addrLayout.findViewById(R.id.phone);
            if (jsonObject.containsKey(GlobalConstants.JSON_PHONE)) {
                phoneLayout.setVisibility(View.VISIBLE);
                phoneText.setText(jsonObject.getString(GlobalConstants.JSON_PHONE));

                // 拨打电话
                phoneLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strMobile = jsonObject.getString(GlobalConstants.JSON_PHONE).replace("-", "");
                        //此处应该对电话号码进行验证
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + strMobile));
                        SpecialTopicActivity.this.startActivity(intent);
                    }
                });
            } else {
                phoneLayout.setVisibility(View.GONE);
            }

            FontTextView businessHoursText = (FontTextView) addrLayout.findViewById(R.id.time);
            if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSHOURS)) {
                timeLayout.setVisibility(View.VISIBLE);
                businessHoursText.setText(jsonObject.getString(GlobalConstants.JSON_BUSINESSHOURS));
            } else {
                timeLayout.setVisibility(View.GONE);
            }
        } else if (jsonObject.getString(GlobalConstants.JSON_TAG).equals(TAG_LINK)) {
            RelativeLayout linkLayout = (RelativeLayout) View.inflate(this, R.layout.special_card_link, null);
            specialCardView.addView(linkLayout);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(this.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                    this.getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                    0);
            linkLayout.setLayoutParams(params);

            FontTextView linkText = (FontTextView) linkLayout.findViewById(R.id.link_text);
            linkText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            if (jsonObject.containsKey(GlobalConstants.JSON_TEXT)) {
                linkText.setText(jsonObject.getString(GlobalConstants.JSON_TEXT));
            }

            linkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SpecialTopicActivity.this, BusinessDetailActivity.class);
                    intent.putExtra("target_url", jsonObject.getString(GlobalConstants.JSON_TARGETURL));
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onShareSuccess(String shareId, int shareType) {
        mEventLogic.shareBonus(shareId, shareType);
    }

    public void onEvent(TopicLikeEvent topicLikeEvent) {

        int likeCount = topic.getLikeCount();

        if (topicLikeEvent.getType() == LikeEvent.TYPE_ADD_LIKE) {
            likeCount = likeCount + 1;
            topic.setLikeCount(likeCount);
            topic.setHasLiked(true);
            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_press), null, null, null);

            likeText.setText(this.getResources().getString(R.string.str_like,
                    topic.getLikeCount()));
        } else if (topicLikeEvent.getType() == LikeEvent.TYPE_DELETE_LIKE) {
            likeCount = likeCount - 1;
            topic.setLikeCount(likeCount);
            topic.setHasLiked(false);
            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.like_normal), null, null, null);

            likeText.setText(this.getResources().getString(R.string.str_like,
                    topic.getLikeCount()));
        }

    }

    public void onEvent(TopicCommentEvent topicCommentEvent) {
        int count = topicCommentEvent.getCount();

        if (topicCommentEvent.getType() == CommentEvent.TYPE_ADD_COMMENT) {
            commentCount = commentCount + count;
            commentText.setText(this.getResources().getString(R.string.str_comment, commentCount));
        } else if (topicCommentEvent.getType() == CommentEvent.TYPE_DELETE_COMMENT) {
            commentCount = commentCount - count;
            commentText.setText(this.getResources().getString(R.string.str_comment, commentCount));
        }

    }

}
