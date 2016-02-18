package com.xiaobukuaipao.youngmam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.AllExpertActivity;
import com.xiaobukuaipao.youngmam.FreshDetailsActivity;
import com.xiaobukuaipao.youngmam.OtherActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.ExpertAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.Expert;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.view.NestedListView;
import com.xiaobukuaipao.youngmam.widget.YoungRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-23.
 * 关注页
 */
public class FollowFragment extends BaseEventHttpFragment {
    private static final String TAG = FollowFragment.class.getSimpleName();

    // 文章列表
    private ListView articleListView;

    // 缓存
    private YoungCache youngCache;

    private boolean refresh = false;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    // 下拉刷新--SwipeRefreshLayout
    private YoungRefreshLayout swipeRefreshLayout;

    // 查看更多专题
    private RelativeLayout recommendExpertLayout;
    private LinearLayout mMoreExpert;
    private NestedListView expertListView;
    private List<Expert> expertList;
    private ExpertAdapter expertAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_follow, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void initUIAndData() {
        super.initUIAndData();

        articleListView = (ListView) view.findViewById(R.id.article_list_view);
        addListViewHeader();

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
                        mEventLogic.getFriendFeed(String.valueOf(minArticleId), -1);
                        mEventLogic.getRecommendExpert(3);
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
                mEventLogic.getFriendFeed(String.valueOf(minArticleId), -1);
            }
        });

        expertList = new ArrayList<Expert>();
        expertAdapter = new ExpertAdapter(this.getActivity(), expertList, R.layout.item_all_expert);
        expertAdapter.setOnFollowClickListener(this);
        expertAdapter.setFragmentManager(getFragmentManager());
        expertListView.setAdapter(expertAdapter);

        // 配置需要分享的相关平台
        configPlatforms(mController);

        setUIListeners();

        /**
         * 得到推荐的达人列表
         */
        mEventLogic.getRecommendExpert(3);
        /**
         * 取好友的Feed列表
         */
        mEventLogic.getFriendFeed(String.valueOf(minArticleId), -1);
    }

    private void setUIListeners() {
        mMoreExpert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowFragment.this.getActivity(), AllExpertActivity.class);
                startActivity(intent);
            }
        });

        expertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expert geek = (Expert) parent.getItemAtPosition(position);
                if (geek != null) {
                    Intent intent = new Intent(FollowFragment.this.getActivity(), OtherActivity.class);
                    intent.putExtra("userId", geek.getUserId());
                    intent.putExtra("userName", geek.getName());
                    startActivity(intent);
                }
            }
        });

        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FollowFragment.this.getActivity(), FreshDetailsActivity.class);
                intent.putExtra("article_id", ((Article) parent.getItemAtPosition(position)).getArticleId());
                startActivity(intent);
            }
        });
    }

    private void addListViewHeader() {
        View view = this.getActivity().getLayoutInflater().inflate(R.layout.follow_article_header, null);

        recommendExpertLayout = (RelativeLayout) view.findViewById(R.id.recommend_expert_layout);
        recommendExpertLayout.setVisibility(View.GONE);
        mMoreExpert = (LinearLayout) view.findViewById(R.id.more_expert);
        expertListView = (NestedListView) view.findViewById(R.id.recommend_expert_list_view);

        articleListView.addHeaderView(view);
    }


    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);

        switch (msg.what) {
            case R.id.get_recommend_expert:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            recommendExpertLayout.setVisibility(View.VISIBLE);
                            expertListView.setVisibility(View.VISIBLE);

                            expertList.clear();

                            for (int i=0; i < jsonArray.size(); i++) {
                                expertList.add(new Expert(jsonArray.getJSONObject(i)));
                            }
                        } else {
                            recommendExpertLayout.setVisibility(View.GONE);
                            expertListView.setVisibility(View.GONE);
                        }
                    }

                    expertAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.get_friend_feed:
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

                        if (jsonArray != null) {
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

    @Override
    public void onEvent(FollowEvent followEvent) {
        super.onEvent(followEvent);

        if (followEvent.getType() == FollowEvent.TYPE_ADD_FOLLOW) {
            // 此时, 添加关注
            for (int i=0; i < expertList.size(); i++) {
                if (expertList.get(i).getUserId().equals(followEvent.getUserId())) {
                    boolean hasFollowed = expertList.get(i).isHasFollowed();
                    hasFollowed = !hasFollowed;
                    expertList.get(i).setHasFollowed(hasFollowed);
                }
            }

            expertAdapter.notifyDataSetChanged();
        } else if (followEvent.getType() == FollowEvent.TYPE_DELETE_FOLLOW) {
            // 此时, 取消关注
            for (int i=0; i < expertList.size(); i++) {
                if (expertList.get(i).getUserId().equals(followEvent.getUserId())) {
                    boolean hasFollowed = expertList.get(i).isHasFollowed();
                    hasFollowed = !hasFollowed;
                    expertList.get(i).setHasFollowed(hasFollowed);
                }
            }

            expertAdapter.notifyDataSetChanged();
        }
    }

}
