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
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.adapter.Article2Adapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.MineQuestionDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.parallax.ScrollTabHolderFragment;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-10-8.
 */
public class AskedQuestionFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener {
    private static final String TAG = AskedQuestionFragment.class.getSimpleName();

    private static final String ARG_POSITION = "position";
    private static final String ARG_USERID = "userId";

    // GridView--可以添加头部和底部
    private ListView askedQuestionListView;

    private YoungCache youngCache;

    /**
     * 用于Fragment预加载
     */
    private boolean preLoad = false;

    private int position;
    private String userId;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    /**
     * 空View
     */
    private View emptyView;

    public static Fragment instance(int position, String userId) {
        AskedQuestionFragment fragment = new AskedQuestionFragment();
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
        View view = inflater.inflate(R.layout.fragment_mine_asked_question, container, false);

        askedQuestionListView = (ListView) view.findViewById(R.id.asked_question_list_view);
        emptyView = (View) view.findViewById(R.id.empty_layout);

        /*SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {

        }
        */
        // GridView添加头部
        View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, askedQuestionListView, false);
        askedQuestionListView.addHeaderView(placeHolderView);

        this.view = view;
        return view;
    }

    @Override
    public void initUIAndData() {
        super.initUIAndData();

        article2Adapter = new Article2Adapter(AskedQuestionFragment.this.getActivity(), articleList, false);
        article2Adapter.setFragmentManager(this.getChildFragmentManager());
        article2Adapter.setOnFollowClickListener(this);
        article2Adapter.setOnActionClickListener(this);
        article2Adapter.setOnActionClickListener2(this);

        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.list_load_more_container);
        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setOnScrollListener(this);

        askedQuestionListView.setAdapter(article2Adapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // Load More
                SharedPreferences sp = AskedQuestionFragment.this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                if (userId == null) {
                    if (sp.getLong(SplashActivity.UID, 0) > 0) {
                        mEventLogic.getUserQuestions(String.valueOf(sp.getLong(SplashActivity.UID, 0)),
                                (minArticleId == -1 ? null : String.valueOf(minArticleId)), -1);
                    }
                } else {
                    mEventLogic.getUserQuestions(userId,
                            (minArticleId == -1 ? null : String.valueOf(minArticleId)), -1);
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

    private void loadDatas() {
        // 如果登录
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (userId == null) {
            if(sp.getLong(SplashActivity.UID, 0) > 0) {
                minArticleId = -1;
                mEventLogic.getUserQuestions(String.valueOf(sp.getLong(SplashActivity.UID, 0)),
                        (minArticleId == -1 ? null : String.valueOf(minArticleId)), -1);
            }
        } else {
            minArticleId = -1;
            mEventLogic.getUserQuestions(userId,
                    (minArticleId == -1 ? null : String.valueOf(minArticleId)), -1);
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
        if (scrollHeight == 0 && askedQuestionListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        // setSelectionFromTop方法来实现精确的恢复数据
        askedQuestionListView.setSelectionFromTop(1, scrollHeight);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);

        switch (msg.what) {
            case R.id.get_user_questions:
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

                        if (jsonObject.containsKey(GlobalConstants.JSON_MINARTICLEID)) {
                            minArticleId = jsonObject.getLong(GlobalConstants.JSON_MINARTICLEID);
                        } else {
                            minArticleId = 0;
                        }

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minArticleId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    if (articleList.size() == 0) {
                        /**
                         * 此时, 没有发布过文章
                         */
                        emptyView.setVisibility(View.VISIBLE);
                        askedQuestionListView.setEmptyView(emptyView);
                        loadMoreListViewContainer.getFooterView().setVisibility(View.INVISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        loadMoreListViewContainer.getFooterView().setVisibility(View.VISIBLE);
                    }

                    article2Adapter.notifyDataSetChanged();
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
    /**
     * 延时加载刷新
     */
    public void onEvent(MineQuestionDelayRefreshEvent mineQuestionDelayRefreshEvent) {
        if (mineQuestionDelayRefreshEvent.getDelayRefresh()) {
            // 此时, 重新加载
            SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                minArticleId = -1;
                preLoad = true;
                if (userId == null) {
                    mEventLogic.getUserQuestions(String.valueOf(sp.getLong(SplashActivity.UID, 0)),
                            (minArticleId == -1 ? null : String.valueOf(minArticleId)), -1);
                } else {
                    mEventLogic.getUserQuestions(userId,
                            (minArticleId == -1 ? null : String.valueOf(minArticleId)), -1);
                }
            }
        }
    }
}
