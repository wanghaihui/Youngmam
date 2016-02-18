package com.xiaobukuaipao.youngmam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.xiaobukuaipao.youngmam.FreshDetailsActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.NavigationCategory;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.YoungRefreshLayout;

/**
 * Created by xiaobu1 on 15-4-23.
 */
public class FindFragment extends  BaseEventHttpFragment {
    private static final String TAG = FindFragment.class.getSimpleName();

    private ListView articleListView;

    // 缓存
    private YoungCache youngCache;

    private boolean refresh = false;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    // 下拉刷新--SwipeRefreshLayout
    private YoungRefreshLayout swipeRefreshLayout;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);

    private static final String ARG_CATEGORY = "category";
    private NavigationCategory category;

    public static Fragment newInstance(NavigationCategory category) {
        FindFragment fragment = new FindFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 位置
        category = getArguments().getParcelable(ARG_CATEGORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_find, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void initUIAndData() {
        super.initUIAndData();

        articleListView = (ListView) view.findViewById(R.id.fresh_list_view);

        LinearLayout topSpacing = new LinearLayout(this.getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_0dp));
        topSpacing.setLayoutParams(params);
        articleListView.addHeaderView(topSpacing);

        /**
         * 得到对应的分类内容
         */
        mEventLogic.getFeedByCategory(category.getId(), String.valueOf(minArticleId), -1);

        youngCache = YoungCache.get(this.getActivity());

        swipeRefreshLayout = (YoungRefreshLayout) view.findViewById(R.id.ptr_frame);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        minArticleId = -1;
                        refresh = true;
                        mEventLogic.getFeedByCategory(category.getId(), String.valueOf(minArticleId), -1);
                    }
                }, 1000);

            }
        });

        // load more container
        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_container);

        loadMoreListViewContainer.useDefaultFooter();

        // binding view and data
        articleListView.setAdapter(article2Adapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mEventLogic.getFeedByCategory(category.getId(), String.valueOf(minArticleId), -1);
            }
        });

        // 配置需要分享的相关平台
        configPlatforms(mController);

        setUIListeners();
    }

    private void setUIListeners() {
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindFragment.this.getActivity(), FreshDetailsActivity.class);
                intent.putExtra("article_id", ((Article) parent.getItemAtPosition(position)).getArticleId());
                startActivity(intent);
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
            case R.id.get_feed_by_category:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

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
                } else {
                    swipeRefreshLayout.setRefreshing(false);
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

    /**
     * 处理预加载
     */
    /*public void onEvent(ArticleChangeEvent articleChangeEvent) {
        Log.d(TAG, "article id :" + articleChangeEvent.getArticleId());
        if (articleList != null) {
            for (int i = 0; i < articleList.size(); i++) {
                if (articleList.get(i).getArticleId().equals(articleChangeEvent.getArticleId())) {
                    if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_LIKE) {
                        boolean hasLike = articleList.get(i).getHasLiked();
                        hasLike = true;
                        articleList.get(i).setHasLiked(hasLike);

                        int likeCount = articleList.get(i).getLikeCount();
                        likeCount++;
                        articleList.get(i).setLikeCount(likeCount);

                        article2Adapter.notifyDataSetChanged();
                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_NOT_LIKE) {
                        boolean hasLike = articleList.get(i).getHasLiked();
                        hasLike = false;
                        articleList.get(i).setHasLiked(hasLike);

                        int likeCount = articleList.get(i).getLikeCount();
                        likeCount--;
                        articleList.get(i).setLikeCount(likeCount);
                        article2Adapter.notifyDataSetChanged();
                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_COMMENT) {
                        int commentCount = articleList.get(i).getCommentCount();
                        commentCount++;
                        articleList.get(i).setCommentCount(commentCount);

                        article2Adapter.notifyDataSetChanged();
                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_NOT_COMMENT) {
                        int commentCount = articleList.get(i).getCommentCount();
                        commentCount--;
                        articleList.get(i).setCommentCount(commentCount);

                        article2Adapter.notifyDataSetChanged();
                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_DELETE) {
                        articleList.remove(i);
                        article2Adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }*/


}
