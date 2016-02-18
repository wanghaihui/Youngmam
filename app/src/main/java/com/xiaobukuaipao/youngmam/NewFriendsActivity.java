package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.NewFriendsAdapter;
import com.xiaobukuaipao.youngmam.domain.MamMessage;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-10-24.
 */
public class NewFriendsActivity extends BaseHttpFragmentActivity {
    private static final String TAG = NewFriendsActivity.class.getSimpleName();

    public static final int TYPE_NEW_FRIENDS = 600;

    private ListView newFriendsView;
    private List<MamMessage> messageList;
    private NewFriendsAdapter newFriendsAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    private long minMsgId = -1;

    public void initViews() {
        setContentView(R.layout.activity_new_friends);
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        newFriendsView = (ListView) findViewById(R.id.new_friends_list_view);
        messageList = new ArrayList<MamMessage>();
        newFriendsAdapter = new NewFriendsAdapter(this, messageList, R.layout.item_new_friends);

        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp));
        topSpacing.setLayoutParams(params);
        newFriendsView.addHeaderView(topSpacing);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        newFriendsView.setAdapter(newFriendsAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 取下一页
                mEventLogic.getMessageByType(TYPE_NEW_FRIENDS, String.valueOf(minMsgId), -1);
            }
        });

        setUIListeners();
    }

    public void executeHttpRequest() {
        // 取消息列表
        mEventLogic.getMessageByType(TYPE_NEW_FRIENDS, String.valueOf(minMsgId), -1);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_new_friends));

        setBackClickListener(this);
    }

    private void setUIListeners() {
        newFriendsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MamMessage mamMessage = (MamMessage) parent.getItemAtPosition(position);
                if (mamMessage != null) {
                    Intent intent = new Intent(NewFriendsActivity.this, OtherActivity.class);
                    intent.putExtra("userId", mamMessage.getActorUserBase().getUserId());
                    intent.putExtra("userName", mamMessage.getActorUserBase().getName());
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
        switch (msg.what) {
            case R.id.get_message_by_type:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                messageList.add(new MamMessage(jsonArray.getJSONObject(i)));
                            }
                        }

                        minMsgId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINMSGID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minMsgId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    newFriendsAdapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }
}
