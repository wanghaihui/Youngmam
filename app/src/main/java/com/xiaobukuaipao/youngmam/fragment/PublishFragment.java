package com.xiaobukuaipao.youngmam.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.adapter.PublishArticleAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.MinePublishDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.loadmore.GridViewWithHeaderAndFooter;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreGridViewContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.parallax.ScrollTabHolderFragment;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-29.
 */
public class PublishFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener {
    private static final String TAG = PublishFragment.class.getSimpleName();

    private static final String ARG_POSITION = "position";
    private static final String ARG_USERID = "userId";

    // GridView--可以添加头部和底部
    private GridViewWithHeaderAndFooter publishGridView;
    private PublishArticleAdapter publishArticleAdapter;
    private List<Article> articleList;

    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    // 用于翻页
    private long minArticleId = -1;

    private YoungCache youngCache;

    // EventBus
    private EventBus eventBus;

    /**
     * 用于Fragment预加载
     */
    private boolean preLoad = false;

    private int position;
    private String userId;

    private LoadMoreGridViewContainer loadMoreGridViewContainer;

    /**
     * 空View
     */
    private View emptyView;

    public static Fragment instance(int position, String userId) {
        PublishFragment fragment = new PublishFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        bundle.putString(ARG_USERID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 位置
        position = getArguments().getInt(ARG_POSITION);
        userId = getArguments().getString(ARG_USERID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_publish, container, false);

        publishGridView = (GridViewWithHeaderAndFooter) view.findViewById(R.id.publish_grid_view);
        emptyView = (View) view.findViewById(R.id.empty_layout);

        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {

        } else {

        }

        // GridView添加头部
        View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, publishGridView, false);
        publishGridView.addHeaderView(placeHolderView);

        mEventLogic = new YoungEventLogic(this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        this.view = view;
        return view;
    }


    @Override
    public void initUIAndData() {
        articleList = new ArrayList<Article>();
        publishArticleAdapter = new PublishArticleAdapter(PublishFragment.this.getActivity(), articleList, R.layout.item_fore_image);

        loadMoreGridViewContainer = (LoadMoreGridViewContainer) view.findViewById(R.id.grid_load_more_container);
        loadMoreGridViewContainer.useDefaultFooter();
        // 重点--扩展性
        loadMoreGridViewContainer.setOnScrollListener(this);

        publishGridView.setAdapter(publishArticleAdapter);

        loadMoreGridViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // Load More
                if (userId == null) {
                    mEventLogic.getPublishedArticle(String.valueOf(minArticleId), userId);
                } else {
                    mEventLogic.getOtherArticle(String.valueOf(minArticleId), userId);
                }
            }
        });

        youngCache = YoungCache.get(this.getActivity());

        /**
         * 获得我发布的新鲜事
         */
        loadDatas();

        setUIListeners();
    }

    private void setUIListeners() {
        // Do nothing
    }

    ///////////////////////////////////////////////////////////////////////////

    private void loadDatas() {
        // 如果登录
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            minArticleId = -1;
            if (userId == null) {
                mEventLogic.getPublishedArticle(String.valueOf(minArticleId), userId);
            } else {
                mEventLogic.getOtherArticle(String.valueOf(minArticleId), userId);
            }
        }
    }

    /**
     * 处理预加载
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void	onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 在滚动
        if (scrollTabHolder != null) {
            scrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, position);
        }
    }

    @Override
    public void	onScrollStateChanged(AbsListView view, int scrollState) {
        // do nothing
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void adjustScroll(int scrollHeight) {
        /**
         * 此问题待解决--目前尚无解决方案
         */
        /*if (scrollHeight == 0 && publishGridView.getFirstVisiblePosition() >= 1) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

        } else {
            publishGridView.setSelectionFromTop(0, scrollHeight);
        }*/
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_publish_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    if (jsonObject != null) {

                        if (preLoad) {
                            articleList.clear();
                            preLoad = false;
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);
                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                articleList.add(new Article(jsonArray.getJSONObject(i)));
                            }
                        }

                        if (minArticleId == -1) {
                            if (StringUtil.isNotEmpty(httpResult.getData())) {
                                // 然后判断此时是否是T票失效
                                if (checkTavailable(httpResult.getData())) {
                                    SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                                    if (sp.getLong(SplashActivity.UID, 0) > 0) {
                                        // youngCache.put(YoungCache.CACHE_MINE_PUBLISH, httpResult.getData());
                                    }
                                }
                            }
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_MINARTICLEID)) {
                            minArticleId = jsonObject.getLong(GlobalConstants.JSON_MINARTICLEID);
                        } else {
                            minArticleId = 0;
                        }

                        // load more complete
                        loadMoreGridViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minArticleId > 0);
                    } else {
                        loadMoreGridViewContainer.loadMoreFinish(true, false);
                    }

                    if (articleList.size() == 0) {
                        /**
                         * 此时, 没有发布过文章
                         */
                        emptyView.setVisibility(View.VISIBLE);
                        publishGridView.setEmptyView(emptyView);
                        loadMoreGridViewContainer.getFooterView().setVisibility(View.INVISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        loadMoreGridViewContainer.getFooterView().setVisibility(View.VISIBLE);
                    }

                    publishArticleAdapter.notifyDataSetChanged();
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
     * 延时加载刷新
     */
    public void onEvent(MinePublishDelayRefreshEvent minePublishDelayRefreshEvent) {
        if (minePublishDelayRefreshEvent.getDelayRefresh()) {
            // 此时, 重新加载
            SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                minArticleId = -1;
                preLoad = true;
                if (userId == null) {
                    mEventLogic.getPublishedArticle(String.valueOf(minArticleId), userId);
                } else {
                    mEventLogic.getOtherArticle(String.valueOf(minArticleId), userId);
                }
            }
        }
    }

}
