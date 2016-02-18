package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.umeng.socialize.media.UMImage;
import com.xiaobukuaipao.youngmam.adapter.GridArticleAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.ActivityParticipateEvent;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleCommentEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleLikeEvent;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.MineQuestionDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.domain.Tag;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.imagechooser.ImageChooserActivity;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreGridViewContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.ActivityImageView;
import com.xiaobukuaipao.youngmam.widget.HeaderView;
import com.xiaobukuaipao.youngmam.widget.InnerGridView;
import com.xiaobukuaipao.youngmam.widget.InnerListView;
import com.xiaobukuaipao.youngmam.widget.ObservableScrollView;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-6-6.
 * 话题--文章标签--问答
 */
public class SearchDetailActivity extends BaseEventFragmentActivity implements ObservableScrollView.Callbacks {
    private static final String TAG = SearchDetailActivity.class.getSimpleName();

    public static final int TYPE_PUBLISH_ARTICLE = 1;
    public static final int TYPE_PUBLISH_QUESTION = 2;
    private int publishType = TYPE_PUBLISH_ARTICLE;

    // 分享文章
    public static final int TYPE_BUSINESS_ARTICLE = 6;
    // 分享问题
    public static final int TYPE_BUSINESS_QUESTION = 8;

    // 缓存
    private YoungCache youngCache;

    // 得到传递过来的word
    private String mIntentWord;

    private LoadMoreListViewContainer loadMoreListViewContainer;
    private LoadMoreGridViewContainer loadMoreGridViewContainer;

    // 搜索结果
    private InnerListView mSearchDetailListView;

    private GridArticleAdapter mGridArticleAdapter;
    private InnerGridView mSearchDetailGridView;

    // 分享的内容
    private String shareContent;
    // 分享的内容的链接
    private String targetUrl;
    // 分享的图片--活动或文章或主题
    private String imageUrl;

    // 传过来的话题详情
    private Theme theme;

    private HeaderView headerView;

    private View mStickyView;
    private View mPlaceholderView;

    private ObservableScrollView observableScrollView;

    // 记录当前的ScrollView的Y值
    private int currentScrollY;

    private ActivityImageView mThemeImage;
    private TextView mThemeName;
    private TextView mThemeContent;

    private int mStickyViewHeight;

    private TextView mTotalNum;
    private ImageView gridToggle;
    private ImageView listToggle;

    private boolean listMode = true;

    private TextView themePublish;
    private TextView themeShare;

    private int totalCount;

    // ActionBar
    private int leftFrameWidth;
    private int rightFrameWidth;

    private boolean isRefresh = false;

    private boolean isTag = false;

    private boolean isShareTag = false;

    /**
     * 分享类型
     */
    private int shareBusinessType;

    private String tagId;

    // 统一的Tag
    private Tag tag;

    public void initViews() {
        super.initViews();
        /**
         * 恩,单独布局吧
         */
        setContentView(R.layout.activity_search_detail);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        youngCache = YoungCache.get(this);

        mStickyView = (View) findViewById(R.id.sticky_view_change);
        mPlaceholderView = (View) findViewById(R.id.placeholder_view_change);

        observableScrollView = (ObservableScrollView) findViewById(R.id.theme_scroll_view);
        observableScrollView.setCallbacks(this);

        headerView = (HeaderView) findViewById(R.id.head_view);
        addToHeadView();

        mTotalNum = (TextView) mStickyView.findViewById(R.id.total_num);
        gridToggle = (ImageView) mStickyView.findViewById(R.id.grid_toggle);
        gridToggle.setImageResource(R.mipmap.gridview_indicator_unselected);
        listToggle = (ImageView) mStickyView.findViewById(R.id.list_toggle);
        listToggle.setImageResource(R.mipmap.listview_indicator_selected);

        mSearchDetailListView = (InnerListView) findViewById(R.id.search_detail_list);
        mSearchDetailListView.setParentScrollView(observableScrollView);
        mSearchDetailListView.setPlaceholderView(mPlaceholderView);
        mSearchDetailListView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(SearchDetailActivity.this) - DisplayUtil.dip2px(this, 54));
        mSearchDetailListView.setVisibility(View.VISIBLE);

        mSearchDetailGridView = (InnerGridView) findViewById(R.id.search_detail_grid);
        mSearchDetailGridView.setParentScrollView(observableScrollView);
        mSearchDetailGridView.setPlaceholderView(mPlaceholderView);
        mSearchDetailGridView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(this) - DisplayUtil.dip2px(this, 54));
        mSearchDetailGridView.setVisibility(View.GONE);

        observableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(observableScrollView.getScrollY());
                    }
                });

        mStickyView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mStickyViewHeight = mStickyView.getHeight();
                        mSearchDetailListView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(SearchDetailActivity.this) - mStickyViewHeight);
                        mSearchDetailGridView.setMaxHeight(DisplayUtil.getAvailableScreenHeight(SearchDetailActivity.this) - mStickyViewHeight);
                        mStickyView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

        article2Adapter.setOnActionClickListener(this);

        mGridArticleAdapter = new GridArticleAdapter(this, articleList, R.layout.item_grid_fresh);
        mGridArticleAdapter.setOnActionClickListener(this);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.list_load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        mSearchDetailListView.setAdapter(article2Adapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (publishType == TYPE_PUBLISH_ARTICLE) {
                    mEventLogic.searchArticleByTag(tagId, String.valueOf(minArticleId), -1);
                } else if (publishType == TYPE_PUBLISH_QUESTION) {
                    // 按照tagId取问答列表接口
                    mEventLogic.searchQuestionByTag(tagId, String.valueOf(minArticleId), -1);
                }
            }
        });

        loadMoreGridViewContainer = (LoadMoreGridViewContainer) findViewById(R.id.grid_load_more_container);
        loadMoreGridViewContainer.useDefaultFooter();

        mSearchDetailGridView.setAdapter(mGridArticleAdapter);

        loadMoreGridViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (publishType == TYPE_PUBLISH_ARTICLE) {
                    mEventLogic.searchArticleByTag(tagId, String.valueOf(minArticleId), -1);
                } else if (publishType == TYPE_PUBLISH_QUESTION) {
                    // 按照tagId取问答列表接口
                    mEventLogic.searchQuestionByTag(tagId, String.valueOf(minArticleId), -1);
                }
            }
        });

        themePublish = (TextView) findViewById(R.id.theme_publish);
        if (publishType == TYPE_PUBLISH_ARTICLE) {
            themePublish.setText(getResources().getString(R.string.str_publish_theme));
        } else if (publishType == TYPE_PUBLISH_QUESTION) {
            themePublish.setText(getResources().getString(R.string.str_ask));
        }

        themeShare = (TextView) findViewById(R.id.theme_share);

        // 配置需要分享的相关平台
        configPlatforms(mController);

        initDatas();
    }

    private void getIntentDatas() {
        tagId = getIntent().getStringExtra("tag_id");
        shareBusinessType = getIntent().getIntExtra("share_businessType", TYPE_BUSINESS_ARTICLE);
        publishType = getIntent().getIntExtra("publish_type", TYPE_PUBLISH_ARTICLE);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);

        setBackClickListener(this);
    }

    private void initDatas() {
        headerView.setVisibility(View.GONE);
        mTotalNum.setText(getResources().getString(R.string.float_num, totalCount));

        setUIListeners();
    }

    private void setUIListeners() {
        mSearchDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (article != null) {
                    Intent intent = new Intent(SearchDetailActivity.this, FreshDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
            }
        });

        mSearchDetailGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (article != null) {
                    Intent intent = new Intent(SearchDetailActivity.this, FreshDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
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

        themePublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishTheme();
            }
        });

        themeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void share() {
        if (tag != null) {
            targetUrl = GlobalConstants.SHARE_THEME + tag.getTagId() + "&businessType=" + shareBusinessType;
            // 分享活动
            YoungShareBoard shareBoard = new YoungShareBoard(this);

            isShareTag = true;
            shareBoard.setShareParams(tag.getTagId(), tag.getType());
            shareBoard.setOnShareSuccessListener(this);

            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            setShareContent(mController, targetUrl, StringUtil.isEmpty(tag.getDesc()) ? tag.getName() : tag.getDesc(), tag.getPosterUrl(),
                    tag.getName(), BaseHttpFragmentActivity.SHARE_TYPE_THEME);
            /**
             * 针对微博
             */
            mController.setShareContent(StringUtil.buildWeiboShareTheme(tag.getName(), targetUrl));
            mController.setShareMedia(new UMImage(this, tag.getPosterUrl()));
        }
    }

    private void publishTheme() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            // 标签带上

            if (publishType == TYPE_PUBLISH_ARTICLE) {
                if (tag != null) {
                    if (!tag.getName().equals("编辑精选")) {
                        Label label = new Label(tag.getTagId(), tag.getName());
                        Intent intent = new Intent(SearchDetailActivity.this, ImageChooserActivity.class);
                        intent.putExtra("label", label);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "编辑精选是由花妈选出的, 不能发布哦~", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (publishType == TYPE_PUBLISH_QUESTION) {
                if (tag != null) {
                    if (!tag.getName().equals("编辑精选")) {
                        Label label = new Label(tag.getTagId(), tag.getName());
                        Intent intent = new Intent(SearchDetailActivity.this, PublishActivity.class);
                        intent.putExtra("label", label);
                        intent.putExtra("type_publish", PublishActivity.TYPE_PUBLISH_QUESTION);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "编辑精选是由花妈选出的, 不能发布哦~", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            // 跳到登录页
            Intent intent = new Intent(SearchDetailActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    private void changeShowMode() {
        if (listMode) {
            listToggle.setImageResource(R.mipmap.listview_indicator_selected);
            gridToggle.setImageResource(R.mipmap.gridview_indicator_unselected);

            loadMoreListViewContainer.setVisibility(View.VISIBLE);
            mSearchDetailGridView.setVisibility(View.GONE);
        } else {
            listToggle.setImageResource(R.mipmap.listview_indicator_unselected);
            gridToggle.setImageResource(R.mipmap.gridview_indicator_selected);

            loadMoreListViewContainer.setVisibility(View.GONE);
            mSearchDetailGridView.setVisibility(View.VISIBLE);
        }

        if (currentScrollY < mPlaceholderView.getTop()) {
            // ScrollView滚动
            observableScrollView.smoothScrollTo(0, currentScrollY);
        }

    }

    private void addToHeadView() {
        addThemeImage(headerView);
        addThemeDesc(headerView);
        headerView.getFloatLayout().setVisibility(View.GONE);
    }

    private void addThemeImage(HeaderView headerView) {
        mThemeImage = new ActivityImageView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                0);
        headerView.getImageLayout().setLayoutParams(params);

        headerView.getImageLayout().addView(mThemeImage);
    }

    private void addThemeDesc(HeaderView headerView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                0,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp),
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp));
        headerView.getTextLayout().setLayoutParams(params);

        mThemeName = (TextView) View.inflate(this, R.layout.font_text_view, null);

        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        nameParams.gravity = Gravity.CENTER;

        mThemeName.setLayoutParams(nameParams);

        mThemeName.setTextColor(getResources().getColor(R.color.color_ff4c51));
        mThemeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        headerView.getTextLayout().addView(mThemeName);

        mThemeContent = (TextView) View.inflate(this, R.layout.font_text_view, null);

        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        contentParams.setMargins(0,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_15dp),
                0,
                0);

        mThemeContent.setLayoutParams(contentParams);

        mThemeContent.setTextColor(getResources().getColor(R.color.color_505050));
        mThemeContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mThemeContent.setLineSpacing(3f, 1.1f);
        headerView.getTextLayout().addView(mThemeContent);
    }

    /**
     * 延时加载数据
     */
    public void delayedLoadDatas() {
        if (publishType == TYPE_PUBLISH_ARTICLE) {
            mEventLogic.searchArticleByTag(tagId, String.valueOf(minArticleId), -1);
        } else if (publishType == TYPE_PUBLISH_QUESTION) {
            // 按照tagId取问答列表接口
            mEventLogic.searchQuestionByTag(tagId, String.valueOf(minArticleId), -1);
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
            case R.id.get_search_article_by_tag:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        Log.d(TAG, "datas : " + httpResult.getData());

                        tag = new Tag(jsonObject.getJSONObject(GlobalConstants.JSON_TAG));

                        if (!StringUtil.isEmpty(tag.getPosterUrl())) {
                            headerView.setVisibility(View.VISIBLE);
                            headerView.getFloatLayout().setVisibility(View.GONE);

                            Glide.with(this)
                                    .load(tag.getPosterUrl())
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(mThemeImage);

                            StringBuilder sb = new StringBuilder();
                            sb.append("-");
                            sb.append(tag.getName());
                            sb.append("-");
                            mThemeName.setText(sb.toString());

                            if (!StringUtil.isEmpty(tag.getDesc())) {
                                mThemeContent.setVisibility(View.VISIBLE);
                                mThemeContent.setText(tag.getDesc());
                            } else {
                                mThemeContent.setVisibility(View.GONE);
                            }
                        } else {
                            headerView.setVisibility(View.GONE);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 0);
                            mPlaceholderView.setLayoutParams(params);
                        }

                        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, tag.getName());

                        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, tag.getName());
                        leftFrameWidth = DisplayUtil.dip2px(this, 54);
                        rightFrameWidth = DisplayUtil.dip2px(this, 54);

                        actionBar.getRightFrame().getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(leftFrameWidth, 0, rightFrameWidth, 0);
                                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                        actionBar.getMiddleFrame().setLayoutParams(params);

                                        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, tag.getName());

                                        actionBar.getRightFrame().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    }
                                });

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (isRefresh) {
                            articleList.clear();
                            isRefresh = false;
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_COUNT)) {
                            totalCount = jsonObject.getInteger(GlobalConstants.JSON_COUNT);
                            mTotalNum.setText(getResources().getString(R.string.float_num, totalCount));
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

            case R.id.get_search_question_by_tag:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        listToggle.setVisibility(View.INVISIBLE);
                        gridToggle.setVisibility(View.INVISIBLE);

                        tag = new Tag(jsonObject.getJSONObject(GlobalConstants.JSON_TAG));

                        if (!StringUtil.isEmpty(tag.getPosterUrl())) {
                            headerView.setVisibility(View.VISIBLE);
                            headerView.getFloatLayout().setVisibility(View.GONE);

                            int width = DisplayUtil.getScreenWidth(this) - 2 * DisplayUtil.dip2px(this, 10);
                            Glide.with(this)
                                    .load(tag.getPosterUrl())
                                    .override(width, width * 345 / 600)
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(mThemeImage);

                            StringBuilder sb = new StringBuilder();
                            sb.append("-");
                            sb.append(tag.getName());
                            sb.append("-");
                            mThemeName.setText(sb.toString());

                            if (!StringUtil.isEmpty(tag.getDesc())) {
                                mThemeContent.setVisibility(View.VISIBLE);
                                mThemeContent.setText(tag.getDesc());
                            } else {
                                mThemeContent.setVisibility(View.GONE);
                            }
                        } else {
                            headerView.setVisibility(View.GONE);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 0);
                            mPlaceholderView.setLayoutParams(params);
                        }

                        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, tag.getName());

                        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, tag.getName());
                        leftFrameWidth = DisplayUtil.dip2px(this, 54);
                        rightFrameWidth = DisplayUtil.dip2px(this, 54);

                        actionBar.getRightFrame().getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(leftFrameWidth, 0, rightFrameWidth, 0);
                                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                        actionBar.getMiddleFrame().setLayoutParams(params);

                                        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, tag.getName());

                                        actionBar.getRightFrame().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    }
                                });

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (isRefresh) {
                            articleList.clear();
                            isRefresh = false;
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_COUNT)) {
                            totalCount = jsonObject.getInteger(GlobalConstants.JSON_COUNT);
                            mTotalNum.setText(getResources().getString(R.string.float_num, totalCount));
                        }

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                articleList.add(new Article(jsonArray.getJSONObject(i)));
                            }
                        }

                        minArticleId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINARTICLEID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minArticleId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    article2Adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public void onEvent(ActivityParticipateEvent activityParticipateEvent) {
        if (activityParticipateEvent.getParticipate()) {
            // 说明,此时参加活动成功,所以执行刷新操作
            isRefresh = true;
            minArticleId = -1;
            if (tag != null) {
                mEventLogic.searchArticleByTag(tag.getTagId(), String.valueOf(minArticleId), -1);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onScrollChanged(int scrollY) {
        currentScrollY = scrollY;

        mStickyView.setTranslationY(Math.max(mPlaceholderView.getTop(), scrollY));
        mSearchDetailListView.setStickyScrollY(scrollY);
        mSearchDetailGridView.setStickyScrollY(scrollY);
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

    /**
     * 刷新问题列表
     * @param mineQuestionDelayRefreshEvent
     */
    public void onEvent(MineQuestionDelayRefreshEvent mineQuestionDelayRefreshEvent) {
        if (mineQuestionDelayRefreshEvent.getDelayRefresh()) {
            isRefresh = true;
            minArticleId = -1;
            if (tag != null) {
                mEventLogic.searchQuestionByTag(tagId, String.valueOf(minArticleId), -1);
            }
        }
    }
}
