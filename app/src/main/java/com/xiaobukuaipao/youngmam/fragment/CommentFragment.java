package com.xiaobukuaipao.youngmam.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.FreshDetailsActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.adapter.CommentAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.ArticleChangeEvent;
import com.xiaobukuaipao.youngmam.domain.Comment;
import com.xiaobukuaipao.youngmam.domain.FragmentDeliverEvent;
import com.xiaobukuaipao.youngmam.domain.MineCommentDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.parallax.ScrollTabHolderFragment;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.MaterialAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-29.
 */
public class CommentFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener {
    private static final String TAG = CommentFragment.class.getSimpleName();

    private static final String ARG_POSITION = "position";

    private ListView commentListView;

    private CommentAdapter commentAdapter;

    private List<Comment> commentList;

    private int position;

    private long minCommentId = -1;

    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    private YoungCache youngCache;
    // EventBus
    private EventBus eventBus;

    /**
     * 用于Fragment预加载
     */
    private boolean preLoad = false;

    // 当前ListView的滚动状态
    private int mCurrentScrollState;
    private boolean isLoadingMore = false;

    // 加载更多
    private RelativeLayout loadMoreLayout;
    private TextView mLoadText;
    private RelativeLayout noDataLayout;

    public static Fragment instance(int position) {
        CommentFragment fragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 位置
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "on create view");
        View view = inflater.inflate(R.layout.fragment_mine_comment, container, false);

        commentListView = (ListView) view.findViewById(R.id.comment_list_view);

        loadMoreLayout = (RelativeLayout) inflater.inflate(R.layout.cube_views_load_more_default_footer, commentListView, false);
        mLoadText = (TextView) loadMoreLayout.findViewById(R.id.cube_views_load_more_default_footer_text_view);
        noDataLayout = (RelativeLayout) loadMoreLayout.findViewById(R.id.no_data_layout);

        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            mLoadText.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        } else {
            mLoadText.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
            ((TextView) noDataLayout.findViewById(R.id.no_data_txt)).setText(getResources().getString(R.string.str_goto_login));
        }

        commentListView.addFooterView(loadMoreLayout);

        // ListView添加头部
        View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, commentListView, false);
        commentListView.addHeaderView(placeHolderView);

        mEventLogic = new YoungEventLogic(this);

        this.view = view;
        return view;
    }

    @Override
    public void initUIAndData() {
        commentList = new ArrayList<Comment>();

        // 设置滑动监听器
        commentListView.setOnScrollListener(this);

        commentAdapter = new CommentAdapter(CommentFragment.this.getActivity(), commentList, R.layout.item_comment);

        // binding view and data
        commentListView.setAdapter(commentAdapter);

        youngCache = YoungCache.get(this.getActivity());
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        Log.d(TAG, "init ui and data");
        setUIListeners();

    }

    private void setUIListeners() {
        commentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Comment comment = (Comment) parent.getItemAtPosition(position);

                if (comment != null) {
                    // 删除评论
                    MaterialAlertDialog dialog = new MaterialAlertDialog(CommentFragment.this.getActivity(),
                            getResources().getString(R.string.comment), getResources().getString(R.string.is_delete_comment), null);

                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mEventLogic.cancelAll();
                            mEventLogic.deleteComment(comment.getArticleId(), comment.getCommentId());
                        }
                    });

                    dialog.setOnCancelButtonClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    });

                    dialog.show();
                }

                return true;
            }
        });

        commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Comment comment = (Comment) parent.getItemAtPosition(position);
                if (comment != null) {
                    Intent intent = new Intent(CommentFragment.this.getActivity(), FreshDetailsActivity.class);
                    intent.putExtra("article_id", comment.getArticleId());
                    startActivity(intent);
                }
            }
        });
    }

    private void parseCacheMineComment() {
        commentList.clear();
        JSONObject jsonObject = JSONObject.parseObject(youngCache.getAsString(YoungCache.CACHE_MINE_COMMENT));

        if (jsonObject != null) {
            // 在这里操作数据
            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

            if (jsonArray != null && jsonArray.size() > 0) {
                for(int i=0; i < jsonArray.size(); i++) {
                    commentList.add(new Comment(jsonArray.getJSONObject(i)));
                }
            }

            minCommentId = jsonObject.getLong(GlobalConstants.JSON_MINCOMMENTID);

            if (isLoadingMore) {
                // 不管有没有数据,都执行加载完成
                onLoadComplete();
            }

            mLoadText.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        } else {
            if (isLoadingMore) {
                // 不管有没有数据,都执行加载完成
                onLoadComplete();
            }
        }

        commentAdapter.notifyDataSetChanged();
    }

    private void loadDatas() {
        /**
         * 获得自己喜欢过的评论
         */
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            if (youngCache.getAsString(YoungCache.CACHE_MINE_COMMENT) != null) {
                parseCacheMineComment();
            } else {
                mEventLogic.getMycommentArticle(String.valueOf(minCommentId));
            }
        }
    }

    /**
     * 处理预加载
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible() && (commentListView != null && commentListView.getVisibility() == View.VISIBLE)) {
            SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                if (youngCache.getAsString(YoungCache.CACHE_MINE_COMMENT) != null) {
                    parseCacheMineComment();
                } else {
                    EventBus.getDefault().post(new FragmentDeliverEvent(FragmentDeliverEvent.USER_STATIS));
                    preLoad = true;
                    minCommentId = -1;
                    mEventLogic.getMycommentArticle(String.valueOf(minCommentId));
                }
            }
        } else {
            Log.d(TAG, "not load data");
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    /////////////////////////////////////////////////////////////////////////
    @Override
    public void	onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 在滚动
        if (scrollTabHolder != null) {
            scrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, position);

            boolean loadMore = firstVisibleItem + visibleItemCount >= (totalItemCount - 1);

            if (!isLoadingMore && loadMore && mCurrentScrollState != SCROLL_STATE_IDLE) {
                mLoadText.setText(getResources().getString(R.string.cube_views_load_more_loading));
                isLoadingMore = true;
                onLoadMore();
            }
        }
    }

    @Override
    public void	onScrollStateChanged(AbsListView view, int scrollState) {
        // do nothing
        if ( scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE ) {
            view.invalidateViews();
        }

        Log.d(TAG, "on scroll state changed : " + scrollState);
        mCurrentScrollState = scrollState;
    }

    //////////////////////////////////////////////////////////////////////////
    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && commentListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        commentListView.setSelectionFromTop(1, scrollHeight);
    }


    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_mycomment_article:

                Log.d(TAG, "on response");
                if (checkResponse(msg)) {

                    HttpResult httpResult = (HttpResult) msg.obj;

                    if (httpResult.getData() != null) {

                        Log.d(TAG, httpResult.getData());

                        JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());



                        if (jsonObject != null) {
                            // 在这里操作数据
                            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                            if (preLoad) {
                                commentList.clear();
                                preLoad = false;
                            }

                            if (jsonArray != null && jsonArray.size() > 0) {
                                for(int i=0; i < jsonArray.size(); i++) {
                                    commentList.add(new Comment(jsonArray.getJSONObject(i)));
                                }
                            } else {

                            }

                            if (minCommentId == -1) {
                                if (StringUtil.isNotEmpty(httpResult.getData())) {
                                    if (checkTavailable(httpResult.getData())) {
                                        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                                        if (sp.getLong(SplashActivity.UID, 0) > 0) {
                                            youngCache.put(YoungCache.CACHE_MINE_COMMENT, httpResult.getData());
                                        }
                                    }
                                }
                            }

                            if (jsonObject.containsKey(GlobalConstants.JSON_MINCOMMENTID)) {
                                minCommentId = jsonObject.getLong(GlobalConstants.JSON_MINCOMMENTID);
                            }

                            if (isLoadingMore) {
                                // 不管有没有数据,都执行加载完成
                                onLoadComplete();
                            }

                            mLoadText.setVisibility(View.VISIBLE);
                            noDataLayout.setVisibility(View.GONE);
                            ((TextView) noDataLayout.findViewById(R.id.no_data_txt)).setText(getResources().getString(R.string.str_goto_comment));
                        } else {
                            if (isLoadingMore) {
                                // 不管有没有数据,都执行加载完成
                                onLoadComplete();
                            }

                            if (commentList != null && commentList.size() > 0) {
                                mLoadText.setVisibility(View.VISIBLE);
                                mLoadText.setText(getResources().getString(R.string.cube_views_load_more_loaded_empty));
                                noDataLayout.setVisibility(View.GONE);
                            } else {
                                mLoadText.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                ((TextView) noDataLayout.findViewById(R.id.no_data_txt)).setText(getResources().getString(R.string.str_goto_comment));
                            }

                        }
                    } else {
                        // 此时没有返回数据-- 不显示评论列表
                        if (isLoadingMore) {
                            // 不管有没有数据,都执行加载完成
                            onLoadComplete();
                        }

                        mLoadText.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                        ((TextView) noDataLayout.findViewById(R.id.no_data_txt)).setText(getResources().getString(R.string.str_goto_comment));
                    }

                    commentAdapter.notifyDataSetChanged();
                } else {
                    if (isLoadingMore) {
                        // 不管有没有数据,都执行加载完成
                        onLoadComplete();
                    }
                    mLoadText.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                    ((TextView) noDataLayout.findViewById(R.id.no_data_txt)).setText(getResources().getString(R.string.str_goto_comment));
                }
                break;

            case R.id.delete_comment:
                // 删除评论成功
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    String commentId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {

                        for(int i=0; i < commentList.size(); i++) {
                            if (commentList.get(i).getCommentId().equals(commentId)) {

                                EventBus.getDefault().post(new ArticleChangeEvent(commentList.get(i).getArticleId(), ArticleChangeEvent.ARTICLE_NOT_COMMENT));

                                commentList.remove(i);

                                // 此时,添加评论成功,评论页表需要刷新
                                youngCache.remove(YoungCache.CACHE_MINE_COMMENT);
                                // Log.d(TAG, "delete comment : article id " + commentList.get(i).getArticleId());

                                EventBus.getDefault().post(new FragmentDeliverEvent(FragmentDeliverEvent.USER_STATIS));
                            }
                        }

                        if (commentList != null && commentList.size() == 0) {
                            mLoadText.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);
                            ((TextView) noDataLayout.findViewById(R.id.no_data_txt)).setText(getResources().getString(R.string.str_goto_comment));
                        }

                        commentAdapter.notifyDataSetChanged();
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

    /**
     * 加载更多
     */
    private void onLoadMore() {
        Log.d(TAG, "on Load More");
        Log.d(TAG, "min comment id : " + minCommentId);
        if (minCommentId > 0) {
            mEventLogic.getMycommentArticle(String.valueOf(minCommentId));
        } else {
            mLoadText.setText(getResources().getString(R.string.cube_views_load_more_loaded_empty));
        }
    }

    /**
     * 加载完成
     */
    private void onLoadComplete() {
        Log.d(TAG, "on Load Complete");
        isLoadingMore = false;

        if (minCommentId == 0) {
            mLoadText.setText(getResources().getString(R.string.cube_views_load_more_loaded_empty));
        }
    }

    /**
     * 延时加载刷新
     */
    public void onEvent(MineCommentDelayRefreshEvent mineCommentDelayRefreshEvent) {
        if (mineCommentDelayRefreshEvent.getDelayRefresh()) {
            // 此时, 重新加载
            SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                EventBus.getDefault().post(new FragmentDeliverEvent(FragmentDeliverEvent.USER_STATIS));
                preLoad = true;
                minCommentId = -1;
                mEventLogic.getMycommentArticle(String.valueOf(minCommentId));
            }
        }
    }
}
