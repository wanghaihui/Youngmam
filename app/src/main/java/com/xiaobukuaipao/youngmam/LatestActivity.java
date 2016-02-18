package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.UMImage;
import com.xiaobukuaipao.youngmam.adapter.GridArticleAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.ActivityParticipateEvent;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleCommentEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleLikeEvent;
import com.xiaobukuaipao.youngmam.domain.BannerActivity;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.imagechooser.ImageChooserActivity;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreGridViewContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.ActivityContentImageView;
import com.xiaobukuaipao.youngmam.view.ActivityImageView;
import com.xiaobukuaipao.youngmam.widget.HeaderView;
import com.xiaobukuaipao.youngmam.widget.InnerGridView;
import com.xiaobukuaipao.youngmam.widget.InnerListView;
import com.xiaobukuaipao.youngmam.widget.ObservableScrollView;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-4-24.
 * 最新活动
 */
public class LatestActivity extends BaseEventFragmentActivity implements ObservableScrollView.Callbacks {

    private ActivityImageView mActivityImage;
    private TextView mParticipateView;
    private TextView mActivityContent;

    // 标签
    private Label mTag;

    private InnerListView mLatestListView;

    private GridArticleAdapter mGridArticleAdapter;
    private InnerGridView mLatestGridView;

    /**
     * 当前活动内容
     */
    private BannerActivity normalActivity;

    // 缓存
    private YoungCache youngCache;

    private LoadMoreListViewContainer loadMoreListViewContainer;
    private LoadMoreGridViewContainer loadMoreGridViewContainer;

    // 分享的内容
    private String shareContent;
    // 分享的内容的链接
    private String targetUrl;
    // 分享的图片--活动或文章或主题
    private String imageUrl;

    // 我要参加
    private Button btnParticipate;

    private boolean isRefresh = false;

    private HeaderView headerView;

    private ObservableScrollView observableScrollView;

    private View mStickyView;
    private View mPlaceholderView;

    private TextView mTotalNum;
    private ImageView gridToggle;
    private ImageView listToggle;

    private boolean listMode = true;

    private int mStickyViewHeight;

    // 记录当前的ScrollView的Y值
    private int currentScrollY;

    @Override
    public void initViews() {
        super.initViews();

        setContentView(R.layout.activity_latest);
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        getIntentDatas();

        youngCache = YoungCache.get(this);

        mStickyView = (View) findViewById(R.id.sticky_view_change);
        mPlaceholderView = (View) findViewById(R.id.placeholder_view_change);

        observableScrollView = (ObservableScrollView) findViewById(R.id.activity_scroll_view);
        observableScrollView.setCallbacks(this);

        headerView = (HeaderView) findViewById(R.id.head_view);
        addToHeadView();

        mTotalNum = (TextView) mStickyView.findViewById(R.id.total_num);
        gridToggle = (ImageView) mStickyView.findViewById(R.id.grid_toggle);
        gridToggle.setImageResource(R.mipmap.gridview_indicator_unselected);
        listToggle = (ImageView) mStickyView.findViewById(R.id.list_toggle);
        listToggle.setImageResource(R.mipmap.listview_indicator_selected);

        mLatestListView = (InnerListView) findViewById(R.id.activity_list);
        mLatestListView.setParentScrollView(observableScrollView);
        mLatestListView.setPlaceholderView(mPlaceholderView);
        mLatestListView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(LatestActivity.this) - DisplayUtil.dip2px(this, 54));
        mLatestListView.setVisibility(View.VISIBLE);

        mLatestGridView = (InnerGridView) findViewById(R.id.activity_grid);
        mLatestGridView.setParentScrollView(observableScrollView);
        mLatestGridView.setPlaceholderView(mPlaceholderView);
        mLatestGridView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(this) - DisplayUtil.dip2px(this, 54));
        mLatestGridView.setVisibility(View.GONE);

        observableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(observableScrollView.getScrollY());
                    }
                });

        mStickyView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        mStickyViewHeight = mStickyView.getHeight();
                        mLatestListView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(LatestActivity.this) - mStickyViewHeight);
                        mLatestGridView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(LatestActivity.this) - mStickyViewHeight);
                        mStickyView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.list_load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (normalActivity != null) {
                    mEventLogic.getActivityArticle(normalActivity.getBusinessId(),
                            String.valueOf(minArticleId));
                }
            }
        });

        loadMoreGridViewContainer = (LoadMoreGridViewContainer) findViewById(R.id.grid_load_more_container);
        loadMoreGridViewContainer.useDefaultFooter();

        loadMoreGridViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (normalActivity != null) {
                    mEventLogic.getActivityArticle(normalActivity.getBusinessId(),
                            String.valueOf(minArticleId));
                }
            }
        });

        btnParticipate = (Button) findViewById(R.id.id_participate);

        // 设置数据
        initDatas();

        // 配置需要分享的相关平台
        configPlatforms(mController);

        setUIListeners();
    }

    public void executeHttpRequest() {
        // 在这里获得参与活动的文章
        if (normalActivity != null) {
            mEventLogic.getActivityArticle(normalActivity.getBusinessId(),
                    String.valueOf(minArticleId));
        }
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_latest_activity));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, R.drawable.general_share, getResources().getString(R.string.str_share));

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    /**
     * 分享
     */
    public void share() {
        MobclickAgent.onEvent(LatestActivity.this, "activeShareBtnClicked");

        // 分享活动
        YoungShareBoard shareBoard = new YoungShareBoard(this);
        shareBoard.setShareParams(normalActivity.getBusinessId(), normalActivity.getBusinessType());
        shareBoard.setOnShareSuccessListener(this);

        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY);

        /**
         * 针对微博
         */
        mController.setShareContent(StringUtil.buildWeiboShareActivity(shareContent, targetUrl));
        mController.setShareMedia(new UMImage(this, imageUrl));
    }

    /**
     * 得到Intent Datas
     */
    private void getIntentDatas() {
        normalActivity = getIntent().getParcelableExtra("normal_activity");
    }

    /**
     * 给ListView添加Header View
     */
    private void addToHeadView() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                0);
        headerView.setLayoutParams(layoutParams);

        // 添加海报图片
        addActivityImage(headerView);
        // 添加参加的人数
        addActivityParticipate(headerView);
        // 添加活动描述
        addActivityDesc(headerView);
    }

    private void addActivityImage(HeaderView headerView) {
        mActivityImage = new ActivityImageView(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mActivityImage.setLayoutParams(params);

        headerView.getImageLayout().addView(mActivityImage);
    }

    private void addActivityParticipate(HeaderView headerView) {
        mParticipateView = (TextView) View.inflate(this, R.layout.font_text_view, null);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM|Gravity.RIGHT;
        params.setMargins(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin));
        mParticipateView.setLayoutParams(params);
        mParticipateView.setTextColor(this.getResources().getColor(R.color.color_ff4c51));
        mParticipateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mParticipateView.setBackgroundResource(R.drawable.rectangle_left_corner);
        // setPadding必须在setBackgroundResource之后, 才能生效
        mParticipateView.setPadding(getResources().getDimensionPixelOffset(R.dimen.activity_participate_paddingLeft),
                getResources().getDimensionPixelOffset(R.dimen.activity_participate_paddingTop),
                getResources().getDimensionPixelOffset(R.dimen.activity_participate_paddingLeft),
                getResources().getDimensionPixelOffset(R.dimen.activity_participate_paddingTop));

        headerView.getImageLayout().addView(mParticipateView);
    }

    private void addActivityDesc(HeaderView headerView) {
        mActivityContent = (TextView) View.inflate(this, R.layout.font_text_view, null);

        mActivityContent.setTextColor(getResources().getColor(R.color.color_505050));
        mActivityContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mActivityContent.setLineSpacing(3f, 1.1f);

        headerView.getTextLayout().addView(mActivityContent);
    }

    /**
     * 添加活动奖品等
     * @param headerView
     */
    private void addActivityExtra(HeaderView headerView, String imgDesc, String url) {
        if (!StringUtil.isEmpty(url)) {
            ActivityContentImageView imageView = new ActivityContentImageView(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_7dp), 0, 0);
            imageView.setLayoutParams(params);

            headerView.getTextLayout().addView(imageView);
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .into(imageView);
        }

        if (!StringUtil.isEmpty(imgDesc)) {
            TextView textView = (TextView) View.inflate(this, R.layout.font_text_view, null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp), 0, 0);
            textView.setLayoutParams(params);

            textView.setTextColor(getResources().getColor(R.color.color_505050));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setLineSpacing(3f, 1.1f);
            headerView.getTextLayout().addView(textView);
            textView.setText(imgDesc);
        }
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        article2Adapter.setOnActionClickListener(this);
        mLatestListView.setAdapter(article2Adapter);

        mGridArticleAdapter = new GridArticleAdapter(this, articleList, R.layout.item_grid_fresh);
        mGridArticleAdapter.setOnActionClickListener(this);
        mLatestGridView.setAdapter(mGridArticleAdapter);

    }

    private void setUIListeners() {
        mLatestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (article != null) {
                    Intent intent = new Intent(LatestActivity.this, FreshDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
            }
        });

        btnParticipate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 参加活动
                participateActivity();
            }
        });

        listToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listMode) {
                    listMode = true;
                    changeShowMode();
                }
            }
        });

        gridToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listMode) {
                    listMode = false;
                    changeShowMode();
                }
            }
        });

    }

    private void changeShowMode() {
        if (listMode) {
            listToggle.setImageResource(R.mipmap.listview_indicator_selected);
            gridToggle.setImageResource(R.mipmap.gridview_indicator_unselected);

            loadMoreListViewContainer.setVisibility(View.VISIBLE);
            mLatestGridView.setVisibility(View.GONE);


        } else {
            listToggle.setImageResource(R.mipmap.listview_indicator_unselected);
            gridToggle.setImageResource(R.mipmap.gridview_indicator_selected);

            loadMoreListViewContainer.setVisibility(View.GONE);
            mLatestGridView.setVisibility(View.VISIBLE);
        }

        if (currentScrollY < mPlaceholderView.getTop()) {
            // ScrollView滚动
            observableScrollView.smoothScrollTo(0, currentScrollY);
        }

    }

    private void participateActivity() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            Intent intent = new Intent(LatestActivity.this, ImageChooserActivity.class);
            // 标签带上
            if (mTag != null && !StringUtil.isEmpty(mTag.getTitle())) {
                intent.putExtra("label", mTag);
            }
            startActivity(intent);
        } else {
            // 跳到登录页
            Intent intent = new Intent(LatestActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);

        switch (msg.what) {
            case R.id.get_activity_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        if (jsonObject.containsKey(GlobalConstants.JSON_ACTIVITY)) {
                            JSONObject activityObject = jsonObject.getJSONObject(GlobalConstants.JSON_ACTIVITY);

                            Glide.with(this)
                                    .load(activityObject.getString(GlobalConstants.JSON_POSTERURL))
                                    .centerCrop()
                                    .into(mActivityImage);

                            mParticipateView.setText(getResources().getString(R.string.activity_participate,
                                    activityObject.getInteger(GlobalConstants.JSON_USERCOUNT)));

                            // 活动内容
                            shareContent = activityObject.getString(GlobalConstants.JSON_TITLE);
                            imageUrl = activityObject.getString(GlobalConstants.JSON_POSTERURL);
                            targetUrl = GlobalConstants.SHARE_ACTIVITY + activityObject.getString(GlobalConstants.JSON_ACTIVITYID);

                            mActivityContent.setText(activityObject.getString(GlobalConstants.JSON_DESC));

                            // 添加标签
                            mTag = new Label(activityObject.getJSONObject(GlobalConstants.JSON_TAG).getString(GlobalConstants.JSON_TAGID),
                                    activityObject.getJSONObject(GlobalConstants.JSON_TAG).getString(GlobalConstants.JSON_NAME));

                            // 添加活动奖品
                            JSONArray imgsArray = activityObject.getJSONArray(GlobalConstants.JSON_IMGS);

                            if (!isRefresh) {
                                if (imgsArray != null && imgsArray.size() > 0) {
                                    for (int i = 0; i < imgsArray.size(); i++) {
                                        addActivityExtra(headerView, ((JSONObject) imgsArray.get(i)).getString(GlobalConstants.JSON_IMG_DESC),
                                                ((JSONObject) imgsArray.get(i)).getString(GlobalConstants.JSON_URL));
                                    }
                                }
                            }

                            mTotalNum.setText(getResources().getString(R.string.float_num,
                                    activityObject.getInteger(GlobalConstants.JSON_ARTICLECOUNT)));
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (isRefresh) {
                            articleList.clear();
                            isRefresh = false;
                        }

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                articleList.add(new Article(jsonArray.getJSONObject(i)));
                            }
                        }

                        minArticleId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINARTICLEID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minArticleId > 0);
                        loadMoreGridViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minArticleId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                        loadMoreGridViewContainer.loadMoreFinish(true, false);
                    }

                    article2Adapter.notifyDataSetChanged();
                    mGridArticleAdapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }

    public void onEvent(ActivityParticipateEvent activityParticipateEvent) {
        if (activityParticipateEvent.getParticipate()) {
            // 说明,此时参加活动成功,所以执行刷新操作
            isRefresh = true;
            minArticleId = -1;
            if (normalActivity != null) {
                mEventLogic.getActivityArticle(normalActivity.getBusinessId(),
                        String.valueOf(minArticleId));
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onScrollChanged(int scrollY) {
        currentScrollY = scrollY;

        mStickyView.setTranslationY(Math.max(mPlaceholderView.getTop(), scrollY));
        mLatestListView.setStickyScrollY(scrollY);
        mLatestGridView.setStickyScrollY(scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent() {

    }


    @Override
    public void onShareSuccess(String shareId, int shareType) {
        mEventLogic.shareBonus(shareId, shareType);
    }

    public void onEvent(ArticleLikeEvent articleLikeEvent) {
        super.onEvent(articleLikeEvent);

        mGridArticleAdapter.notifyDataSetChanged();
    }

    public void onEvent(ArticleCommentEvent articleCommentEvent) {
        super.onEvent(articleCommentEvent);

        mGridArticleAdapter.notifyDataSetChanged();
    }
}
