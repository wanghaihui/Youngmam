package com.xiaobukuaipao.youngmam;

import android.os.Message;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.adapter.MineGiftAdapter;
import com.xiaobukuaipao.youngmam.domain.Gift;
import com.xiaobukuaipao.youngmam.domain.GiftMeta;
import com.xiaobukuaipao.youngmam.domain.MineGift;
import com.xiaobukuaipao.youngmam.domain.MineGiftAddrEvent;
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
 * Created by xiaobu1 on 15-7-9.
 */
public class MineGiftActivity extends BaseHttpFragmentActivity {

    // 用于翻页
    private long minTxId = -1;

    private List<MineGift> mineGiftList;
    private MineGiftAdapter mineGiftAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;
    private ListView mineGiftListView;

    private boolean isRefresh = false;

    // EventBus
    private EventBus eventBus;

    public void initViews() {
        setContentView(R.layout.activity_mine_gift);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        mineGiftListView = (ListView) findViewById(R.id.mine_gift_list);

        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_0dp));
        topSpacing.setLayoutParams(params);
        mineGiftListView.addHeaderView(topSpacing);

        initDatas();

        setUIListeners();
    }

    private void initDatas() {
        mineGiftList = new ArrayList<MineGift>();
        mineGiftAdapter = new MineGiftAdapter(this, mineGiftList, R.layout.item_mine_gift);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        mineGiftListView.setAdapter(mineGiftAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mEventLogic.getMineGift(String.valueOf(minTxId));
            }
        });
    }

    private void setUIListeners() {
        // Do nothing
    }

    public void executeHttpRequest() {
        // 获取用户的我的积分
        mEventLogic.getMineGift(String.valueOf(minTxId));
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_mine_gift));

        setBackClickListener(this);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_mine_gift:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_OBJECT);

                        if (isRefresh) {
                            mineGiftList.clear();
                            isRefresh = false;
                        }

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i=0; i < jsonArray.size(); i++) {
                                mineGiftList.add(new MineGift(new Gift(jsonArray.getJSONObject(i).getJSONObject(GlobalConstants.JSON_ITEM)),
                                        new GiftMeta(jsonArray.getJSONObject(i).getJSONObject(GlobalConstants.JSON_TX))));
                            }
                        }

                        minTxId = jsonObject.getLong(GlobalConstants.JSON_MINTXID);

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minTxId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    mineGiftAdapter.notifyDataSetChanged();
                }

                break;
        }
    }

    public void onEvent(MineGiftAddrEvent mineGiftAddrEvent) {
        if (mineGiftAddrEvent.getMineGiftAddr()) {
            // 说明,此时参加活动成功,所以执行刷新操作
            isRefresh = true;
            minTxId = -1;
            mEventLogic.getMineGift(String.valueOf(minTxId));
        }
    }
}
