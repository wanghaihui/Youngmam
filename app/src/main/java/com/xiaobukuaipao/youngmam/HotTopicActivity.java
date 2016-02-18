package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.greenrobot.event.EventBus;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.adapter.MetaArticleAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleChangeEvent;
import com.xiaobukuaipao.youngmam.domain.LikeCommentNumberEvent;
import com.xiaobukuaipao.youngmam.domain.MetaArticle;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.TopicImageView;
import com.xiaobukuaipao.youngmam.widget.CommentDialog;
import com.xiaobukuaipao.youngmam.widget.FooterView;
import com.xiaobukuaipao.youngmam.widget.HeaderView;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class HotTopicActivity extends BaseHttpFragmentActivity implements OnActionClickListener, CommentDialog.OnSendClickListener,
        YoungShareBoard.OnShareSuccessListener{

    private static final String TAG = HotTopicActivity.class.getSimpleName();

    private TopicImageView mTopicImage;
    private TextView mTopicName;
    private TextView mTopicContent;

    private ListView hotTopicListView;
    private MetaArticleAdapter mMetaArticleAdapter;
    private List<MetaArticle> mMetaArticleList;

    // 当前的topic id
    private String topicId;
    // 缓存
    private YoungCache youngCache;
    // EventBus
    private EventBus eventBus;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);
    // 分享的内容
    private String shareContent;
    // 分享的内容的链接
    private String targetUrl;
    // 分享的图片--活动或文章或主题
    private String imageUrl;

    private boolean normal = false;

    private String topicTitle = "";

    // ActionBar
    private int leftFrameWidth;
    private int rightFrameWidth;

    public void initViews() {
        setContentView(R.layout.activity_hot_topic);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        //ptrFrameLayout = (PtrDefaultFrameLayout) findViewById(R.id.latest_ptr_frame);
        hotTopicListView = (ListView) findViewById(R.id.hot_topic_list);
        setUIListeners();

        // 给ListView添加头部View
        addHeadViewToListView();

        // 给ListView添加底部View
        if (!normal) {
            addFootViewToListView();
        } else {
            addPaddingToListView();
        }

        // 设置数据
        initDatas();

        youngCache = YoungCache.get(this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // 配置需要分享的相关平台
        configPlatforms(mController);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {

        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setRightAction(YoungActionBar.Type.TEXT, R.drawable.general_share, getResources().getString(R.string.str_share));

        if (!StringUtil.isEmpty(topicTitle)) {
            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, topicTitle);
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

                            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, topicTitle);
                            actionBar.getRightFrame().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });
        }

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void getIntentDatas() {
        topicId = getIntent().getStringExtra("topic_id");
        normal = getIntent().getBooleanExtra("normal_topic", false);
        topicTitle = getIntent().getStringExtra("topic_title");
    }

    private void setUIListeners() {
        hotTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = ((MetaArticle) parent.getItemAtPosition(position)).getArticle();
                if (article != null) {
                    Intent intent = new Intent(HotTopicActivity.this, FreshDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 分享
     */
    public void share() {
        MobclickAgent.onEvent(HotTopicActivity.this, "hotTopicShareBtnClicked");

        YoungShareBoard shareBoard = new YoungShareBoard(this);
        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_TOPIC);

        /**
         * 针对微博
         */

        mController.setShareContent(StringUtil.buildWeiboShareTopic(shareContent, targetUrl));
        mController.setShareMedia(new UMImage(this, imageUrl));
    }

    /**
     * 给ListView添加Header View
     */
    private void addHeadViewToListView() {
        HeaderView headerView = new HeaderView(this);

        addActivityImage(headerView);
        addActivityDesc(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        hotTopicListView.addHeaderView(headerView);
    }

    /**
     * 给ListView添加Footer View
     */
    private void addFootViewToListView() {
        final FooterView footerView = new FooterView(this);
        footerView.setPadding(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_20dp));
        Button button = new Button(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        button.setLayoutParams(params);

        button.setGravity(Gravity.CENTER);
        button.setText(getResources().getString(R.string.str_explore_all_topic));
        button.setTextColor(getResources().getColor(R.color.white));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        button.setBackgroundResource(R.drawable.btn_participate_bg);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotTopicActivity.this, AllTopicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        footerView.setContentView(button);

        hotTopicListView.addFooterView(footerView);
    }

    private void addPaddingToListView() {
        final FooterView footerView = new FooterView(this);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        hotTopicListView.addFooterView(footerView);
    }

    private void addActivityImage(HeaderView headerView) {
        mTopicImage = new TopicImageView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                0);
        headerView.getImageLayout().setLayoutParams(params);

        headerView.getImageLayout().addView(mTopicImage);
    }

    private void addActivityDesc(HeaderView headerView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                0,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_reverse_10dp));
        headerView.getTextLayout().setLayoutParams(params);

        mTopicName = (TextView) View.inflate(this, R.layout.font_text_view, null);

        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.setMargins(0,
                0,
                0,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp));
        nameParams.gravity = Gravity.CENTER;
        mTopicName.setLayoutParams(nameParams);

        mTopicName.setTextColor(getResources().getColor(R.color.color_505050));
        mTopicName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        headerView.getTextLayout().addView(mTopicName);

        mTopicContent = (TextView) View.inflate(this, R.layout.font_text_view, null);
        mTopicContent.setTextColor(getResources().getColor(R.color.color_505050));
        mTopicContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTopicContent.setLineSpacing(3f, 1.1f);
        headerView.getTextLayout().addView(mTopicContent);
    }

    private void initDatas() {
        mMetaArticleList = new ArrayList<MetaArticle>();

        mMetaArticleAdapter = new MetaArticleAdapter(this, mMetaArticleList, R.layout.item_meta_article);
        mMetaArticleAdapter.setOnActionClickListener(this);
        hotTopicListView.setAdapter(mMetaArticleAdapter);
    }

    public void executeHttpRequest() {
        // 在这里获得热门专题内容
        mEventLogic.getTopic(topicId);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_topic:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    Log.d(TAG, httpResult.getData());
                    targetUrl = GlobalConstants.SHARE_TOPIC + jsonObject.getString(GlobalConstants.JSON_TOPICID);

                    Glide.with(this).load(jsonObject.getString(GlobalConstants.JSON_IMGURL))
                            .into(mTopicImage);
                    imageUrl = jsonObject.getString(GlobalConstants.JSON_IMGURL);

                    StringBuilder sb = new StringBuilder();
                    sb.append("-");
                    sb.append(topicTitle);
                    sb.append("-");
                    mTopicName.setText(sb.toString());
                    mTopicContent.setText(jsonObject.getString(GlobalConstants.JSON_TOPICDESC));
                    shareContent = jsonObject.getString(GlobalConstants.JSON_TITLE);

                    JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_ITEMS);

                    if (jsonArray != null && jsonArray.size() > 0) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            mMetaArticleList.add(new MetaArticle(jsonArray.getJSONObject(i)));
                        }
                    }

                    mMetaArticleAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.delete_like_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            for (int i = 0; i < mMetaArticleList.size(); i++) {

                                Article article = mMetaArticleList.get(i).getArticle();

                                if (articleId.equals(article.getArticleId())) {
                                    // 此时,喜欢成功,将我的喜欢的缓存清空
                                    youngCache.remove(YoungCache.CACHE_MINE_LIKE);
                                    EventBus.getDefault().post(new ArticleChangeEvent(article.getArticleId(), ArticleChangeEvent.ARTICLE_NOT_LIKE));
                                }
                            }
                        }
                    }

                    mMetaArticleAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.add_like_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            for (int i = 0; i < mMetaArticleList.size(); i++) {

                                Article article = mMetaArticleList.get(i).getArticle();

                                if (articleId.equals(article.getArticleId())) {
                                    // 此时,喜欢成功,将我的喜欢的缓存清空
                                    youngCache.remove(YoungCache.CACHE_MINE_LIKE);
                                    EventBus.getDefault().post(new ArticleChangeEvent(article.getArticleId(), ArticleChangeEvent.ARTICLE_LIKE));
                                }
                            }
                        }

                        // 1
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }

                    mMetaArticleAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.add_article_comment:
                // 发送评论成功
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    String articleId = msg.getData().getString("key");

                    if (status == 0) {
                        // 此时,代表添加评论成功
                        // 此时,添加评论成功,评论页表需要刷新
                        youngCache.remove(YoungCache.CACHE_MINE_COMMENT);
                        EventBus.getDefault().post(new ArticleChangeEvent(articleId, ArticleChangeEvent.ARTICLE_COMMENT));

                        // 2
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;

            case R.id.share_bonus:

                // 发送评论成功
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

            default:
                break;
        }
    }


    @Override
    public void onActionClick(final int action, final int position, final Article article) {
        if (action == OnActionClickListener.ACTION_LIKE) {

            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                if (article.getHasLiked()) {
                    // 文章已经被喜欢,所以此时执行取消喜欢
                    Log.d(TAG, "has liked");
                    mEventLogic.cancelAll();
                    mEventLogic.deleteLikeArticle(article.getArticleId(), null);
                } else {
                    // 文章没有被喜欢,所以此时执行喜欢
                    mEventLogic.cancelAll();
                    mEventLogic.addLikeArticle(article.getArticleId());
                }
            } else {
                // 跳到登录页
                Intent intent = new Intent(HotTopicActivity.this, RegisterAndLoginActivity.class);
                startActivity(intent);
            }

        } else if (action == OnActionClickListener.ACTION_COMMENT) {
            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                // 评论文章
                // 显示评论框
                showCommentDialog(article.getArticleId(), "", null);
            } else {
                // 跳到登录页
                Intent intent = new Intent(HotTopicActivity.this, RegisterAndLoginActivity.class);
                startActivity(intent);
            }
        } else if (action == OnActionClickListener.ACTION_SHARE) {
            // 首先组合基本的分享单元

            String targetUrl = GlobalConstants.SHARE_ARTICLE + article.getArticleId();
            String shareContent = article.getContent();
            String imageUrl = null;
            if (article.getImgs().size() > 0) {
                imageUrl = article.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL);
            }

            YoungShareBoard shareBoard = new YoungShareBoard(this);
            shareBoard.setShareParams(article.getArticleId(), article.getBusinessType());
            shareBoard.setOnShareSuccessListener(this);
            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE);

            /**
             * 针对微博
             */

            mController.setShareContent(StringUtil.buildWeiboShareArticle(shareContent, targetUrl));
            mController.setShareMedia(new UMImage(this, imageUrl));
        }
    }

    public void onEvent(LikeCommentNumberEvent likeCommentNumberEvent) {

        if (mMetaArticleList != null) {
            for (int i = 0; i < mMetaArticleList.size(); i++) {
                if (mMetaArticleList.get(i).getArticle().getArticleId().equals(likeCommentNumberEvent.getArticleId())) {
                    mMetaArticleList.get(i).getArticle().setLikeCount(likeCommentNumberEvent.getLikeNum());
                    mMetaArticleList.get(i).getArticle().setCommentCount(likeCommentNumberEvent.getCommentNum());

                    mMetaArticleAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 处理预加载
     */
    public void onEvent(ArticleChangeEvent articleChangeEvent) {
        Log.d(TAG, "article id :" + articleChangeEvent.getArticleId());
        for(int i=0; i < mMetaArticleList.size(); i++) {

            if (mMetaArticleList.get(i).getArticle().getArticleId().equals(articleChangeEvent.getArticleId())) {
                if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_LIKE) {
                    boolean hasLike = mMetaArticleList.get(i).getArticle().getHasLiked();
                    hasLike = true;
                    mMetaArticleList.get(i).getArticle().setHasLiked(hasLike);

                    int likeCount = mMetaArticleList.get(i).getArticle().getLikeCount();
                    likeCount++;
                    mMetaArticleList.get(i).getArticle().setLikeCount(likeCount);

                    mMetaArticleAdapter.notifyDataSetChanged();
                } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_NOT_LIKE) {
                    boolean hasLike = mMetaArticleList.get(i).getArticle().getHasLiked();
                    hasLike = false;
                    mMetaArticleList.get(i).getArticle().setHasLiked(hasLike);

                    int likeCount = mMetaArticleList.get(i).getArticle().getLikeCount();
                    likeCount--;
                    mMetaArticleList.get(i).getArticle().setLikeCount(likeCount);

                    mMetaArticleAdapter.notifyDataSetChanged();
                } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_DELETE) {
                    mMetaArticleList.remove(i);
                    mMetaArticleAdapter.notifyDataSetChanged();
                } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_COMMENT) {
                    int commentCount = mMetaArticleList.get(i).getArticle().getCommentCount();
                    commentCount++;
                    mMetaArticleList.get(i).getArticle().setCommentCount(commentCount);

                    mMetaArticleAdapter.notifyDataSetChanged();
                } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_NOT_COMMENT) {
                    int commentCount = mMetaArticleList.get(i).getArticle().getCommentCount();
                    commentCount--;
                    mMetaArticleList.get(i).getArticle().setCommentCount(commentCount);

                    mMetaArticleAdapter.notifyDataSetChanged();
                } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_DELETE) {
                    mMetaArticleList.remove(i);
                    mMetaArticleAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void showCommentDialog(String articleId, String name, String originCommentId) {
        CommentDialog dialog = new CommentDialog(HotTopicActivity.this, articleId, name, originCommentId);
        dialog.setOnSendClickListener(this);
        dialog.show();
    }

    @Override
    public void onSendClick(String articleId, String originCommentId, String content) {
        mEventLogic.addArticleComment(articleId, content, originCommentId);
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
    public void onShareSuccess(String articleId, int shareType) {
        mEventLogic.shareBonus(articleId, shareType);
    }
}
