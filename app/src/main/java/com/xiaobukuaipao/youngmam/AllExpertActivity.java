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
import com.xiaobukuaipao.youngmam.adapter.ExpertAdapter;
import com.xiaobukuaipao.youngmam.domain.Expert;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-2.
 */
public class AllExpertActivity extends BaseEventFragmentActivity {

    private long minLastArticleId = -1;

    private ListView expertListView;
    private List<Expert> expertList;
    private ExpertAdapter expertAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    public void initViews() {
        super.initViews();

        setContentView(R.layout.activity_all_expert);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        expertListView = (ListView) findViewById(R.id.all_expert_list);

        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        topSpacing.setLayoutParams(params);
        expertListView.addHeaderView(topSpacing);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (minLastArticleId > 0) {
                    mEventLogic.getAllExpert(String.valueOf(minLastArticleId));
                }
            }
        });

        initDatas();
        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_expert_hall));

        setBackClickListener(this);
    }

    /**
     * 添加ListView的头部
     */
    private void addListViewHeader() {
        View view = this.getLayoutInflater().inflate(R.layout.all_expert_header, null);
        expertListView.addHeaderView(view);
    }

    public void executeHttpRequest() {
        /**
         * 获取首页达人列表
         */
        // mEventLogic.getOnboardExpert();
        // 获取全部达人
        mEventLogic.getAllExpert(String.valueOf(minLastArticleId));
    }

    private void initDatas() {
        expertList = new ArrayList<Expert>();
        expertAdapter = new ExpertAdapter(this, expertList, R.layout.item_all_expert);
        expertAdapter.setOnFollowClickListener(this);
        expertAdapter.setFragmentManager(getSupportFragmentManager());
        expertListView.setAdapter(expertAdapter);
    }

    private void setUIListeners() {

        expertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expert geek = (Expert) parent.getItemAtPosition(position);
                if (geek != null) {
                    Intent intent = new Intent(AllExpertActivity.this, OtherActivity.class);
                    intent.putExtra("userId", geek.getUserId());
                    intent.putExtra("userName", geek.getName());
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
            case R.id.get_all_expert:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                expertList.add(new Expert(jsonArray.getJSONObject(i)));
                            }
                        }

                        minLastArticleId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINLASTARTICLEID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minLastArticleId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    expertAdapter.notifyDataSetChanged();
                }
                break;
        }
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
