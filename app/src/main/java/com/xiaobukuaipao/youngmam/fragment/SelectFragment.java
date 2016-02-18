package com.xiaobukuaipao.youngmam.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.AllThemeActivity;
import com.xiaobukuaipao.youngmam.AllTopicActivity;
import com.xiaobukuaipao.youngmam.HotTopicActivity;
import com.xiaobukuaipao.youngmam.PublishActivity;
import com.xiaobukuaipao.youngmam.QuestionDetailsActivity;
import com.xiaobukuaipao.youngmam.QuestionsActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.RegisterAndLoginActivity;
import com.xiaobukuaipao.youngmam.SearchDetailActivity;
import com.xiaobukuaipao.youngmam.SpecialTopicActivity;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.TopicWebActivity;
import com.xiaobukuaipao.youngmam.WelfareActivity;
import com.xiaobukuaipao.youngmam.adapter.HotThemeAdapter;
import com.xiaobukuaipao.youngmam.adapter.ImagePagerAdapter;
import com.xiaobukuaipao.youngmam.adapter.QuestionsAdapter;
import com.xiaobukuaipao.youngmam.adapter.TopicAdapter;
import com.xiaobukuaipao.youngmam.adapter.TopicAdapter.OnThemeLikeClickListener;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleDeleteEvent;
import com.xiaobukuaipao.youngmam.domain.BannerActivity;
import com.xiaobukuaipao.youngmam.domain.LikeEvent;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.domain.TopicLikeEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.view.NestedGridView;
import com.xiaobukuaipao.youngmam.view.NestedListView;
import com.xiaobukuaipao.youngmam.widget.AutoScrollViewPager;
import com.xiaobukuaipao.youngmam.widget.ScaleViewPager.OnPageChangeListener;
import com.xiaobukuaipao.youngmam.widget.YoungRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-23.
 * 精选页
 */
public class SelectFragment extends BaseEventHttpFragment implements OnThemeLikeClickListener {

    private static final String TAG = SelectFragment.class.getSimpleName();

    public static final int BANNER_AREA_SELECT = 1;
    public static final int BANNER_AREA_QUESTION = 2;
    public static final int BANNER_AREA_WELFARE = 3;

    // 专题
    public static final int TYPE_TOPIC = 3;
    // special专题
    public static final int TYPE_SPECIAL = 7;

    private AutoScrollViewPager activityViewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<BannerActivity> bannerActivityList;
    private LinearLayout mDotsLayout;
    // 记录当前选中位置
    private int currentIndex;

    // 查看更多专题
    private LinearLayout mMoreTopic;
    // 查看更多话题
    private LinearLayout mMoreTheme;
    // 查看更多问题
    private LinearLayout mMoreQuestion;

    // 用于翻页
    private long minId = -1;

    private ListView selectArticleListView;
    private LoadMoreListViewContainer loadMoreListViewContainer;
    // 下拉刷新 -- SwipeRefreshLayout
    private YoungRefreshLayout swipeRefreshLayout;

    private boolean refresh = false;

    private FrameLayout questionLayout;
    private FrameLayout welfareLayout;

    private NestedListView newQuestionListView;
    private List<Article> newQuestionList;
    private QuestionsAdapter questionsAdapter;

    private NestedListView hotTopicListView;
    private TopicAdapter hotTopicAdapter;
    private List<Topic> hotTopicList;

    private NestedGridView hotThemeGridView;
    private HotThemeAdapter hotThemeAdapter;
    private List<Theme> hotThemeList;

    private FrameLayout pagerLayout;

    private RelativeLayout hotTopicLayout;
    private RelativeLayout hotSubjectLayout;
    private RelativeLayout newQuestionLayout;
    private RelativeLayout huayoungSelectLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_select, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void initUIAndData() {
        super.initUIAndData();
        selectArticleListView = (ListView) view.findViewById(R.id.select_article_list_view);
        addListViewHeader();

        hotTopicList = new ArrayList<Topic>();
        hotTopicAdapter = new TopicAdapter(this.getActivity(), hotTopicList, R.layout.item_hot_topic_image);
        hotTopicAdapter.setOnThemeLikeClickListener(this);
        hotTopicListView.setAdapter(hotTopicAdapter);

        swipeRefreshLayout = (YoungRefreshLayout) view.findViewById(R.id.ptr_frame);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        refresh = true;
                        minArticleId = -1;
                        mEventLogic.getBestFeed(String.valueOf(minArticleId), -1);

                        // 全部刷新
                        mEventLogic.getOnboardBanner(BANNER_AREA_SELECT);
                        mEventLogic.getNewQuestions(String.valueOf(-1), -1);
                        mEventLogic.getTopicList(minId, 2);
                        mEventLogic.getRecommendTheme(2);

                    }
                }, 1000);
            }
        });

        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_container);

        loadMoreListViewContainer.useDefaultFooter();

        // binding view and data
        selectArticleListView.setAdapter(article2Adapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 加载更多...
                mEventLogic.getBestFeed(String.valueOf(minArticleId), -1);
            }
        });

        bannerActivityList = new ArrayList<BannerActivity>();

        // 热门话题
        hotThemeList = new ArrayList<Theme>();
        hotThemeAdapter = new HotThemeAdapter(this.getActivity(), hotThemeList, R.layout.item_hot_theme);
        hotThemeGridView.setAdapter(hotThemeAdapter);

        // 最新问题
        newQuestionList = new ArrayList<Article>();
        questionsAdapter = new QuestionsAdapter(this.getActivity(), newQuestionList, R.layout.item_questions, QuestionsAdapter.TYPE_QUESTION_NEW);
        newQuestionListView.setAdapter(questionsAdapter);

        /**
         * 得到当前的Banner中活动的内容
         */
        mEventLogic.getOnboardBanner(BANNER_AREA_SELECT);

        /**
         * 获取最新问题
         */
        mEventLogic.getNewQuestions(String.valueOf(-1), -1);

        /**
         * 获取专题列表--两条
         */
        mEventLogic.getTopicList(minId, 2);

        /**
         * 获取推荐话题
         */
        mEventLogic.getRecommendTheme(2);

        /**
         * 获取首页的编辑精选
         */
        mEventLogic.getBestFeed(String.valueOf(minArticleId), -1);

        // 设置监听器
        setUIListeners();
    }

    private void addListViewHeader() {
        View view = this.getActivity().getLayoutInflater().inflate(R.layout.select_topic_header, null);

        pagerLayout = (FrameLayout) view.findViewById(R.id.pager_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.getScreenWidth(this.getActivity()),
                DisplayUtil.getScreenWidth(this.getActivity()) * 250 / 640);
        pagerLayout.setLayoutParams(params);

        pagerLayout.setVisibility(View.GONE);

        activityViewPager = (AutoScrollViewPager) view.findViewById(R.id.activity_view_pager);
        mDotsLayout = (LinearLayout) view.findViewById(R.id.guide_dots_layout);

        questionLayout = (FrameLayout) view.findViewById(R.id.question_layout);
        welfareLayout = (FrameLayout) view.findViewById(R.id.welfare_layout);

        hotTopicLayout = (RelativeLayout) view.findViewById(R.id.hot_topic_layout);
        hotTopicLayout.setVisibility(View.GONE);
        mMoreTopic = (LinearLayout) view.findViewById(R.id.more_topic);
        hotTopicListView = (NestedListView) view.findViewById(R.id.hot_topic_list_view);

        hotSubjectLayout = (RelativeLayout) view.findViewById(R.id.hot_subject_layout);
        hotSubjectLayout.setVisibility(View.GONE);
        mMoreTheme = (LinearLayout) view.findViewById(R.id.more_theme);
        hotThemeGridView = (NestedGridView) view.findViewById(R.id.hot_theme_list_view);

        newQuestionLayout = (RelativeLayout) view.findViewById(R.id.new_question_layout);
        newQuestionLayout.setVisibility(View.GONE);
        mMoreQuestion = (LinearLayout) view.findViewById(R.id.more_question);
        // 新问题列表
        newQuestionListView = (NestedListView) view.findViewById(R.id.new_question_list_view);
        View footView = this.getActivity().getLayoutInflater().inflate(R.layout.ask_question_footer, null);
        newQuestionListView.addFooterView(footView);
        newQuestionListView.setVisibility(View.GONE);

        huayoungSelectLayout = (RelativeLayout) view.findViewById(R.id.huayoung_select_layout);
        huayoungSelectLayout.setVisibility(View.GONE);

        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = SelectFragment.this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    Intent intent = new Intent(SelectFragment.this.getActivity(), PublishActivity.class);
                    intent.putExtra("type_publish", PublishActivity.TYPE_PUBLISH_QUESTION);
                    startActivity(intent);
                } else {
                    // 跳到登录页
                    Intent intent = new Intent(SelectFragment.this.getActivity(), RegisterAndLoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        selectArticleListView.addHeaderView(view);
    }

    private void setUIListeners() {
        // 问答区
        questionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFragment.this.getActivity(), QuestionsActivity.class);
                startActivity(intent);
            }
        });

        // 福利社
        welfareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFragment.this.getActivity(), WelfareActivity.class);
                startActivity(intent);
            }
        });

        mMoreTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFragment.this.getActivity(), AllTopicActivity.class);
                startActivity(intent);
            }
        });

        mMoreQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFragment.this.getActivity(), QuestionsActivity.class);
                startActivity(intent);
            }
        });

        hotTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic topic = (Topic) parent.getItemAtPosition(position);

                if (topic != null) {
                    if (topic.getBusinessType() > 1000 && topic.getBusinessType() < 2000) {
                        Intent intent = new Intent(SelectFragment.this.getActivity(), TopicWebActivity.class);
                        intent.putExtra("topic", topic);
                        startActivity(intent);
                    } else {
                        switch (topic.getBusinessType()) {
                            case TYPE_TOPIC:
                                Intent intentNormal = new Intent(SelectFragment.this.getActivity(), HotTopicActivity.class);
                                intentNormal.putExtra("normal_topic", true);
                                intentNormal.putExtra("topic_id", topic.getBusinessId());
                                intentNormal.putExtra("topic_title", topic.getTitle());
                                startActivity(intentNormal);
                                break;
                            case TYPE_SPECIAL:
                                Intent intent = new Intent(SelectFragment.this.getActivity(), SpecialTopicActivity.class);
                                intent.putExtra("topic", topic);
                                startActivity(intent);
                                break;
                        }
                    }
                }
            }
        });

        newQuestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);

                if (article != null) {
                    Intent intent = new Intent(SelectFragment.this.getActivity(), QuestionDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
            }
        });

        mMoreTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectFragment.this.getActivity(), AllThemeActivity.class);
                startActivity(intent);
            }
        });

        hotThemeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Theme theme = (Theme) parent.getItemAtPosition(position);
                if (theme != null) {
                    Intent intent = new Intent(SelectFragment.this.getActivity(), SearchDetailActivity.class);
                    intent.putExtra("tag_id", theme.getTag().getId());
                    intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);
        switch (msg.what) {
            case R.id.get_onboard_banner:
                if (checkResponse(msg)) {
                    pagerLayout.setVisibility(View.VISIBLE);

                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());
                    if (jsonArray != null && jsonArray.size() > 0) {
                        bannerActivityList.clear();

                        // 此时,存在活动
                        for(int i=0; i < jsonArray.size(); i++) {
                            bannerActivityList.add(new BannerActivity(jsonArray.getJSONObject(i)));
                        }

                        imagePagerAdapter = new ImagePagerAdapter(this.getActivity(), bannerActivityList).setInfiniteLoop(true);
                        activityViewPager.setAdapter(imagePagerAdapter);
                        activityViewPager.setOnPageChangeListener(new BannerOnPageChangeListener());
                        activityViewPager.setInterval(5000);
                        activityViewPager.startAutoScroll();

                        imagePagerAdapter.notifyDataSetChanged();

                        if (bannerActivityList != null && bannerActivityList.size() > 1) {
                            mDotsLayout.removeAllViews();

                            for (int i = 0; i < bannerActivityList.size(); i++) {
                                ImageView imageView = new ImageView(this.getActivity());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                imageView.setLayoutParams(params);
                                imageView.setPadding(0, 0, 6, 0);
                                imageView.setImageResource(R.drawable.guide_dot2);

                                mDotsLayout.addView(imageView);
                            }

                            currentIndex = 0;
                            // 设置为白色,即选中状态
                            mDotsLayout.getChildAt(currentIndex).setEnabled(false);
                        }
                    }
                }
                break;

            case R.id.get_topic_list:
                if (checkResponse(msg)) {
                    hotTopicLayout.setVisibility(View.VISIBLE);

                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {

                            hotTopicList.clear();

                            for (int i = 0; i < jsonArray.size(); i++) {
                                hotTopicList.add(new Topic(jsonArray.getJSONObject(i)));
                            }
                        }
                    }

                    hotTopicAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.get_best_feed:
                if (checkResponse(msg)) {
                    huayoungSelectLayout.setVisibility(View.VISIBLE);

                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        Log.d(TAG, "best feeds : " + httpResult.getData());

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (refresh) {
                            articleList.clear();
                            swipeRefreshLayout.setRefreshing(false);
                            refresh = false;
                        }

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i=0; i < jsonArray.size(); i++) {
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

            case R.id.get_new_questions:
                if (checkResponse(msg)) {
                    newQuestionLayout.setVisibility(View.VISIBLE);
                    newQuestionListView.setVisibility(View.VISIBLE);

                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
                        if (jsonArray != null && jsonArray.size() > 0) {
                            newQuestionList.clear();

                            for (int i=0; i < jsonArray.size(); i++) {
                                newQuestionList.add(new Article(jsonArray.getJSONObject(i)));
                            }

                            questionsAdapter.notifyDataSetChanged();
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

            case R.id.get_recommend_theme:
                if (checkResponse(msg)) {
                    hotSubjectLayout.setVisibility(View.VISIBLE);

                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONArray jsonArray = JSONArray.parseArray(httpResult.getData());

                    if (jsonArray != null && jsonArray.size() > 0) {
                        Log.d(TAG, "theme datas : " + httpResult.getData());

                        hotThemeList.clear();

                        for (int i = 0; i < jsonArray.size(); i++) {
                            hotThemeList.add(new Theme(jsonArray.getJSONObject(i)));
                        }

                        hotThemeAdapter.notifyDataSetChanged();
                    }
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterAll(mEventLogic);
    }
    ///////////////////////////////////////////////////////////////////////////////////

    public class BannerOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            if (bannerActivityList.size() != 0) {
                setCurrentDot(position % bannerActivityList.size());
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 设置当前的点
     */
    private void setCurrentDot(int position) {
        if (position < 0 || position > bannerActivityList.size() - 1 || currentIndex == position) {
            return;
        }

        mDotsLayout.getChildAt(position).setEnabled(false);
        mDotsLayout.getChildAt(currentIndex).setEnabled(true);

        currentIndex = position;
    }

    @Override
    public void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        activityViewPager.stopAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        // start auto scroll when onResume
        activityViewPager.startAutoScroll();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onThemeLikeClick(Topic topic) {
        if (topic.isHasLiked()) {
            // 此时, 取消喜欢
            mEventLogic.deleteLike2(topic.getBusinessId(), String.valueOf(topic.getBusinessType()), null);
        } else {
            // 此时, 执行喜欢操作
            mEventLogic.addLike(topic.getBusinessId(), String.valueOf(topic.getBusinessType()));
        }
    }

    public void onEvent(TopicLikeEvent topicLikeEvent) {
        if (hotTopicList != null && hotTopicList.size() > 0) {
            for (int i = 0; i < hotTopicList.size(); i++) {
                if (hotTopicList.get(i).getBusinessId().equals(topicLikeEvent.getArticleId())) {
                    int likeCount = hotTopicList.get(i).getLikeCount();
                    if (topicLikeEvent.getType() == LikeEvent.TYPE_ADD_LIKE) {
                        likeCount = likeCount + 1;
                        hotTopicList.get(i).setLikeCount(likeCount);
                        hotTopicList.get(i).setHasLiked(true);
                    } else if (topicLikeEvent.getType() == LikeEvent.TYPE_DELETE_LIKE) {
                        likeCount = likeCount - 1;
                        hotTopicList.get(i).setLikeCount(likeCount);
                        hotTopicList.get(i).setHasLiked(false);
                    }

                    hotTopicAdapter.notifyDataSetChanged();
                    break;
                }
            }

        }
    }

    @Override
    public void onEvent(ArticleDeleteEvent articleDeleteEvent) {
        super.onEvent(articleDeleteEvent);

        for (int i=0; i < newQuestionList.size(); i++) {
            if (newQuestionList.get(i).getUserBase().getUserId().equals(articleDeleteEvent.getArticleId())) {
                newQuestionList.remove(i);
            }
        }
        questionsAdapter.notifyDataSetChanged();
    }

}
