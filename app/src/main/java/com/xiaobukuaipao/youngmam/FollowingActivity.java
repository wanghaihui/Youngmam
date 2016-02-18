package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.UserAdapter;
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
 * Created by xiaobu1 on 15-10-20.
 */
public class FollowingActivity extends BaseEventFragmentActivity {

    private static final String TAG = FollowingActivity.class.getSimpleName();

    public static final int TYPE_FOLLOWING = 0;
    public static final int TYPE_FOLLOWER = 1;

    private String host;
    private long minUserId = -1;

    private ListView userListView;
    private List<UserBase> userList;
    private UserAdapter userAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    private int type = TYPE_FOLLOWING;

    public void initViews() {
        super.initViews();

        setContentView(R.layout.activity_following);

        getIntentDatas();
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        userListView = (ListView) findViewById(R.id.user_list_view);
        userList = new ArrayList<UserBase>();
        userAdapter = new UserAdapter(this, userList, R.layout.item_user);
        userAdapter.setOnFollowClickListener(this);

        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();
        // binding view and data
        userListView.setAdapter(userAdapter);
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (host != null) {
                    if (type == TYPE_FOLLOWING) {
                        // 被关注者列表
                        mEventLogic.getFollowing(host, String.valueOf(minUserId), -1);
                    } else if (type == TYPE_FOLLOWER) {
                        // 粉丝列表
                        mEventLogic.getFollowers(host, String.valueOf(minUserId), -1);
                    }
                }
            }
        });

        setUIListeners();
    }

    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        if (type == TYPE_FOLLOWING) {
            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_follow));
        } else if (type == TYPE_FOLLOWER) {
            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_huafen));
        }
        setBackClickListener(this);
    }

    private void getIntentDatas() {
        host = getIntent().getStringExtra("host");
        type = getIntent().getIntExtra("type", TYPE_FOLLOWING);
    }

    public void executeHttpRequest() {
        // 取被关注者列表
        if (host != null) {
            if (type == TYPE_FOLLOWING) {
                // 被关注者列表
                mEventLogic.getFollowing(host, String.valueOf(minUserId), -1);
            } else if (type == TYPE_FOLLOWER) {
                // 粉丝列表
                mEventLogic.getFollowers(host, String.valueOf(minUserId), -1);
            }
        }
    }

    private void setUIListeners() {
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserBase userBase = (UserBase) parent.getItemAtPosition(position);
                if (userBase != null) {
                    Intent intent = new Intent(FollowingActivity.this, OtherActivity.class);
                    intent.putExtra("userId", userBase.getUserId());
                    intent.putExtra("userName", userBase.getName());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);

        switch (msg.what) {
            case R.id.get_following:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        Log.d(TAG, "datas : " + httpResult.getData());

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for(int i=0; i < jsonArray.size(); i++) {
                                userList.add(new UserBase(jsonArray.getJSONObject(i)));
                            }
                        }

                        minUserId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINUSERID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minUserId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    userAdapter.notifyDataSetChanged();
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
