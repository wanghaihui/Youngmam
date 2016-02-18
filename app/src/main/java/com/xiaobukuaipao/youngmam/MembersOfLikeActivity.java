package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.UserAdapter;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.domain.UserBase;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-27.
 */
public class MembersOfLikeActivity extends BaseEventFragmentActivity {
    private static final String TAG = MembersOfLikeActivity.class.getSimpleName();

    private ListView userListView;
    private List<UserBase> userList;
    private UserAdapter userAdapter;

    /**
     * 文章Id
     */
    private String articleId;
    private int likeCount;
    /**
     * 喜欢这篇文章的人的翻页id
     */
    private long minLikeId = -1;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    @Override
    public void initViews() {
        super.initViews();

        setContentView(R.layout.activity_member_of_like);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        userListView = (ListView) findViewById(R.id.members_list_view);

        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp));
        topSpacing.setLayoutParams(params);
        userListView.addHeaderView(topSpacing);

        userList = new ArrayList<UserBase>();

        userAdapter = new UserAdapter(this, userList, R.layout.item_user);
        userAdapter.setOnFollowClickListener(this);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        userListView.setAdapter(userAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mEventLogic.getArticleLike(articleId, String.valueOf(minLikeId));
            }
        });

        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_article_like_num, likeCount));

        setBackClickListener(this);
    }

    private void getIntentDatas() {
        articleId = getIntent().getStringExtra("article_id");
        likeCount = getIntent().getIntExtra("like_count", 0);
    }

    private void setUIListeners() {
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserBase member = (UserBase) parent.getItemAtPosition(position);
                if (member != null) {
                    Intent intent = new Intent(MembersOfLikeActivity.this, OtherActivity.class);
                    intent.putExtra("userId", member.getUserId());
                    intent.putExtra("userName", member.getName());
                    startActivity(intent);
                }
            }
        });
    }

    public void executeHttpRequest() {

        /**
         * 获取喜欢这篇文章的人的列表
         */
        mEventLogic.getArticleLike(articleId, String.valueOf(minLikeId));
    }


    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);

        switch (msg.what) {
            case R.id.get_article_like:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    if (httpResult.getData() != null) {
                        JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                        if (jsonObject != null) {
                            Log.d(TAG, "datas : " + httpResult.getData());

                            JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                            if (jsonArray != null && jsonArray.size() > 0) {

                                for(int i=0; i < jsonArray.size(); i++) {
                                    userList.add(new Article(jsonArray.getJSONObject(i)).getUserBase());
                                }
                            }

                            minLikeId = jsonObject.getLong(GlobalConstants.JSON_MINLIKEID);

                            // load more complete
                            loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minLikeId > 0);

                        } else {
                            loadMoreListViewContainer.loadMoreFinish(true, false);
                        }

                        userAdapter.notifyDataSetChanged();
                    }
                }

                break;
            default:
                break;
        }
    }

    public void onEvent(FollowEvent followEvent) {
        super.onEvent(followEvent);

        if (followEvent.getType() == FollowEvent.TYPE_ADD_FOLLOW) {
            // 此时, 添加关注
            for (int i=0; i < userList.size(); i++) {
                if (userList.get(i).getUserId().equals(followEvent.getUserId())) {
                    boolean hasFollowed = userList.get(i).isHasFollowed();
                    hasFollowed = !hasFollowed;
                    userList.get(i).setHasFollowed(hasFollowed);
                }
            }

            userAdapter.notifyDataSetChanged();
        } else if (followEvent.getType() == FollowEvent.TYPE_DELETE_FOLLOW) {
            // 此时, 取消关注
            for (int i=0; i < userList.size(); i++) {
                if (userList.get(i).getUserId().equals(followEvent.getUserId())) {
                    boolean hasFollowed = userList.get(i).isHasFollowed();
                    hasFollowed = !hasFollowed;
                    userList.get(i).setHasFollowed(hasFollowed);
                }
            }

            userAdapter.notifyDataSetChanged();
        }
    }
}
