package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.adapter.GiftAdapter;
import com.xiaobukuaipao.youngmam.domain.Gift;
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
 * Created by xiaobu1 on 15-6-30.
 */
public class GiftCenterActivity extends BaseHttpFragmentActivity {
    private static final String TAG = GiftCenterActivity.class.getSimpleName();

    private int credit = 0;
    // 用于翻页
    private long minItemId = -1;

    private ListView giftListView;
    private List<Gift> mGiftList;
    private GiftAdapter giftAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    // EventBus
    private EventBus eventBus;

    public void initViews() {
        setContentView(R.layout.activity_gift_center);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        giftListView = (ListView) findViewById(R.id.gift_list);

        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_0dp));
        topSpacing.setLayoutParams(params);
        giftListView.addHeaderView(topSpacing);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (minItemId > 0) {
                    mEventLogic.getAllGift(String.valueOf(minItemId));
                }
            }
        });

        initDatas();

        initUIListeners();
    }

    private void initDatas() {
        mGiftList = new ArrayList<Gift>();
        giftAdapter = new GiftAdapter(this, mGiftList, R.layout.item_gift);
        giftListView.setAdapter(giftAdapter);

    }

    private void initUIListeners() {
        giftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gift gift = (Gift) parent.getItemAtPosition(position);
                if (gift != null && (gift.getStatus() == 1)) {
                    Intent intent = new Intent(GiftCenterActivity.this, GiftDetailActivity.class);
                    intent.putExtra("item_id", gift.getItemId());
                    intent.putExtra("gift_name", gift.getName());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_gift_center));
        actionBar.setRightAction(YoungActionBar.Type.TEXT_IMAGE, R.mipmap.mine_credit, credit > 0 ? String.valueOf(credit) : "");

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiftCenterActivity.this, MineCreditActivity.class);
                startActivity(intent);
            }
        });
    }

    public void executeHttpRequest() {
        // 获取用户的总积分
        mEventLogic.getAllCredit();
        // 获取全部商品列表
        mEventLogic.getAllGift(String.valueOf(minItemId));
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_all_credit:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                        credit = Integer.valueOf(jsonObject.getString(GlobalConstants.JSON_BONUSPOINT));
                    }

                    ((TextView) actionBar.getRightFrame().getChildAt(0)).setText(credit > 0 ? String.valueOf(credit) : "");
                }
                break;
            case R.id.get_all_gift:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        Log.d(TAG, "datas : " + httpResult.getData());
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        minItemId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINITEMID));

                        int length = jsonArray.size();

                        for (int i = 0; i < length; i++) {
                            mGiftList.add(new Gift(jsonArray.getJSONObject(i)));
                        }

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minItemId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    giftAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
