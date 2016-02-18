package com.xiaobukuaipao.youngmam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.CategoryAdapter;
import com.xiaobukuaipao.youngmam.adapter.ImagePagerAdapter;
import com.xiaobukuaipao.youngmam.adapter.QuestionsAdapter;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleCommentEvent;
import com.xiaobukuaipao.youngmam.domain.BannerActivity;
import com.xiaobukuaipao.youngmam.domain.Category;
import com.xiaobukuaipao.youngmam.domain.CommentEvent;
import com.xiaobukuaipao.youngmam.domain.MineQuestionDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.view.NestedGridView;
import com.xiaobukuaipao.youngmam.view.NestedListView;
import com.xiaobukuaipao.youngmam.widget.AutoScrollViewPager;
import com.xiaobukuaipao.youngmam.widget.ScaleViewPager;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-9-24.
 * 问答区
 */
public class QuestionsActivity extends BaseEventFragmentActivity {
    private static final String TAG = QuestionsActivity.class.getSimpleName();

    private ListView questionsListView;
    private LoadMoreListViewContainer loadMoreListViewContainer;
    // 下拉刷新 -- SwipeRefreshLayout
    private YoungRefreshLayout swipeRefreshLayout;

    private AutoScrollViewPager activityViewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<BannerActivity> bannerActivityList;
    private LinearLayout mDotsLayout;
    // 记录当前选中位置
    private int currentIndex;

    private NestedGridView questionsGridView;
    private ArrayList<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    private NestedListView selectedQuestionListView;
    private List<Article> selectedQuestionList;
    private QuestionsAdapter selectedQuestionAdapter;

    // 提问问题
    private RelativeLayout bottomLayout;

    private boolean refresh = false;

    // 每一页的条数
    private int limit = -1;

    public void initViews() {
        super.initViews();

        setContentView(R.layout.activity_questions);
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        questionsListView = (ListView) findViewById(R.id.questions_list_view);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);

        addListViewHeader();

        article2Adapter.setFragmentManager(getSupportFragmentManager());

        swipeRefreshLayout = (YoungRefreshLayout) findViewById(R.id.ptr_frame);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        refresh = true;
                        minArticleId = -1;
                        mEventLogic.getAllQuestions(String.valueOf(minArticleId), limit);
                        mEventLogic.getBestQuestions(String.valueOf(-1), -1);
                    }
                }, 1000);
            }
        });

        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        // binding view and data
        questionsListView.setAdapter(article2Adapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 加载更多
                mEventLogic.getAllQuestions(String.valueOf(minArticleId), limit);
            }
        });

        bannerActivityList = new ArrayList<BannerActivity>();

        categoryList = new ArrayList<Category>();
        categoryList.add(new Category("1", "备孕生产", R.mipmap.questions_bysc));
        categoryList.add(new Category("2", "宝宝喂养", R.mipmap.questions_bbwy));
        categoryList.add(new Category("3", "疾病防治", R.mipmap.questions_jbfz));
        categoryList.add(new Category("4", "两性家庭", R.mipmap.questions_lxjt));
        categoryList.add(new Category("5", "囤货指南", R.mipmap.questions_thzn));
        categoryList.add(new Category("6", "产后恢复", R.mipmap.questions_chhf));
        categoryAdapter = new CategoryAdapter(this, categoryList, R.layout.item_category);
        questionsGridView.setAdapter(categoryAdapter);

        selectedQuestionList = new ArrayList<Article>();

        selectedQuestionAdapter = new QuestionsAdapter(this, selectedQuestionList, R.layout.item_questions, QuestionsAdapter.TYPE_QUESTION_SELECT);
        selectedQuestionListView.setAdapter(selectedQuestionAdapter);

        setUIListeners();
    }

    public void executeHttpRequest() {
        /**
         * 得到当前的Banner中活动的内容
         */
        // mEventLogic.getOnboardBanner(SelectFragment.BANNER_AREA_QUESTION);

        /**
         * 获取精选问题摘要列表
         */
        mEventLogic.getBestQuestions(String.valueOf(-1), -1);

        /**
         * 获取最新问题的摘要列表接口--第一页
         */
        mEventLogic.getAllQuestions(String.valueOf(minArticleId), limit);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_question_answer));
        setBackClickListener(this);
    }

    private void addListViewHeader() {
        View view = this.getLayoutInflater().inflate(R.layout.question_and_answer_header, null);
        activityViewPager = (AutoScrollViewPager) view.findViewById(R.id.activity_view_pager);
        activityViewPager.setVisibility(View.GONE);
        mDotsLayout = (LinearLayout) view.findViewById(R.id.guide_dots_layout);
        mDotsLayout.setVisibility(View.GONE);

        questionsGridView = (NestedGridView) view.findViewById(R.id.questions_grid_view);
        selectedQuestionListView = (NestedListView) view.findViewById(R.id.selected_question_list_view);

        questionsListView.addHeaderView(view);
    }

    /**
     * 设置监听器
     */
    private void setUIListeners() {
        questionsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QuestionsActivity.this, QuestionCategoryActivity.class);
                intent.putParcelableArrayListExtra("question_category_list", categoryList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        /**
         * 精选问题
         */
        selectedQuestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);

                if (article != null) {
                    Intent intent = new Intent(QuestionsActivity.this, QuestionDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
            }
        });

        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);

                if (article != null) {
                    Intent intent = new Intent(QuestionsActivity.this, QuestionDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
            }
        });

        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = QuestionsActivity.this.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    Intent intent = new Intent(QuestionsActivity.this, PublishActivity.class);
                    intent.putExtra("type_publish", PublishActivity.TYPE_PUBLISH_QUESTION);
                    startActivity(intent);
                } else {
                    // 跳到登录页
                    Intent intent = new Intent(QuestionsActivity.this, RegisterAndLoginActivity.class);
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
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());
                    if (jsonArray != null && jsonArray.size() > 0) {
                        bannerActivityList.clear();

                        // 此时,存在活动
                        for(int i=0; i < jsonArray.size(); i++) {
                            bannerActivityList.add(new BannerActivity(jsonArray.getJSONObject(i)));
                        }

                        imagePagerAdapter = new ImagePagerAdapter(this, bannerActivityList).setInfiniteLoop(true);
                        activityViewPager.setAdapter(imagePagerAdapter);
                        activityViewPager.setOnPageChangeListener(new BannerOnPageChangeListener());
                        activityViewPager.setInterval(5000);
                        activityViewPager.startAutoScroll();

                        imagePagerAdapter.notifyDataSetChanged();

                        if (bannerActivityList != null && bannerActivityList.size() > 1) {
                            mDotsLayout.removeAllViews();

                            for (int i = 0; i < bannerActivityList.size(); i++) {
                                ImageView imageView = new ImageView(this);
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

            case R.id.get_best_questions:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSON.parseObject(httpResult.getData());
                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
                        if (jsonArray != null && jsonArray.size() > 0) {
                            selectedQuestionList.clear();
                            for (int i=0; i < jsonArray.size(); i++) {
                                selectedQuestionList.add(new Article(jsonArray.getJSONObject(i)));
                            }

                            selectedQuestionAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;

            case R.id.get_all_questions:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSON.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (refresh) {
                            articleList.clear();
                            swipeRefreshLayout.setRefreshing(false);
                            refresh = false;
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

            default:
                break;
        }
    }

    public class BannerOnPageChangeListener implements ScaleViewPager.OnPageChangeListener {
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

    public void onEvent(ArticleCommentEvent articleCommentEvent) {
        super.onEvent(articleCommentEvent);

        if (articleCommentEvent.getType() == CommentEvent.TYPE_DELETE_COMMENT) {
            for (int i=0; i < selectedQuestionList.size(); i++) {
                if (selectedQuestionList.get(i).getArticleId().equals(articleCommentEvent.getArticleId())) {
                    selectedQuestionList.get(i).setCommentCount(selectedQuestionList.get(i).getCommentCount() - articleCommentEvent.getCount());
                }
            }
        } else if (articleCommentEvent.getType() == CommentEvent.TYPE_ADD_COMMENT) {
            for (int i=0; i < selectedQuestionList.size(); i++) {
                if (selectedQuestionList.get(i).getArticleId().equals(articleCommentEvent.getArticleId())) {
                    selectedQuestionList.get(i).setCommentCount(selectedQuestionList.get(i).getCommentCount() + articleCommentEvent.getCount());
                }
            }
        }

        selectedQuestionAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新问题列表
     * @param mineQuestionDelayRefreshEvent
     */
    public void onEvent(MineQuestionDelayRefreshEvent mineQuestionDelayRefreshEvent) {
        if (mineQuestionDelayRefreshEvent.getDelayRefresh()) {
            refresh = true;
            minArticleId = -1;
            mEventLogic.getAllQuestions(String.valueOf(minArticleId), limit);
        }
    }
}
