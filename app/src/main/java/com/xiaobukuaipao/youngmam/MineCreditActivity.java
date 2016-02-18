package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.MineCreditAdapter;
import com.xiaobukuaipao.youngmam.domain.Credit;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.VersionUtil;
import com.xiaobukuaipao.youngmam.view.NestedListView;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-4.
 */
public class MineCreditActivity extends BaseHttpFragmentActivity {
    private static final String TAG = MineCreditActivity.class.getSimpleName();

    // 代表Android
    public static final int DEVICE_TYPE = 3;

    private TextView totalCredit;
    private TextView todayCredit;

    private NestedListView group1ListView;
    private MineCreditAdapter group1Adapter;
    private List<Credit> group1List;

    private NestedListView group2ListView;
    private MineCreditAdapter group2Adapter;
    private List<Credit> group2List;

    private NestedListView group3ListView;
    private MineCreditAdapter group3Adapter;
    private List<Credit> group3List;

    public void initViews() {
        setContentView(R.layout.activity_mine_credit);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        totalCredit = (TextView) findViewById(R.id.total_credit);
        todayCredit = (TextView) findViewById(R.id.today_credit);

        group1ListView = (NestedListView) findViewById(R.id.group1_listview);
        group2ListView = (NestedListView) findViewById(R.id.group2_listview);
        group3ListView = (NestedListView) findViewById(R.id.group3_listview);

        initDatas();

        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_mine_credit));

        setBackClickListener(this);
    }

    public void executeHttpRequest() {
        // 获取用户的我的积分
        mEventLogic.getPointDetail(String.valueOf(DEVICE_TYPE), VersionUtil.getVersionName(this));
    }

    private void initDatas() {
        group1List = new ArrayList<Credit>();
        group1Adapter = new MineCreditAdapter(this, group1List, R.layout.item_mine_credit);
        group1ListView.setAdapter(group1Adapter);

        group2List = new ArrayList<Credit>();
        group2Adapter = new MineCreditAdapter(this, group2List, R.layout.item_mine_credit);
        group2ListView.setAdapter(group2Adapter);

        group3List = new ArrayList<Credit>();
        group3Adapter = new MineCreditAdapter(this, group3List, R.layout.item_mine_credit);
        group3ListView.setAdapter(group3Adapter);

    }

    private void setUIListeners() {
        group1ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MineCreditActivity.this, CreditDetailActivity.class);
                startActivity(intent);
            }
        });

        group2ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MineCreditActivity.this, CreditDetailActivity.class);
                startActivity(intent);
            }
        });

        group3ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MineCreditActivity.this, CreditDetailActivity.class);
                startActivity(intent);
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
            case R.id.get_point_detail:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        Log.d(TAG, "mine credit : " + httpResult.getData());

                        // 总积分
                        totalCredit.setText(jsonObject.getString(GlobalConstants.JSON_FINALPOINT));

                        // 今日积分
                        if (jsonObject.containsKey(GlobalConstants.JSON_TODAYPOINT)) {
                            todayCredit.setText(jsonObject.getString(GlobalConstants.JSON_TODAYPOINT));
                        } else {
                            todayCredit.setText("0");
                        }

                        JSONObject todayJsonObject = jsonObject.getJSONObject(GlobalConstants.JSON_TODAYENTRIES);

                        if (todayJsonObject != null) {
                            if (todayJsonObject.containsKey(GlobalConstants.JSON_GROUP1)) {
                                JSONArray group1Array = todayJsonObject.getJSONArray(GlobalConstants.JSON_GROUP1);
                                if (group1Array != null && group1Array.size() > 0) {
                                    for (int i=0; i < group1Array.size(); i++) {
                                        group1List.add(new Credit(group1Array.getJSONObject(i)));
                                    }
                                }

                                group1Adapter.notifyDataSetChanged();
                            }

                            if (todayJsonObject.containsKey(GlobalConstants.JSON_GROUP2)) {
                                JSONArray group2Array = todayJsonObject.getJSONArray(GlobalConstants.JSON_GROUP2);
                                if (group2Array != null && group2Array.size() > 0) {
                                    for (int i=0; i < group2Array.size(); i++) {
                                        group2List.add(new Credit(group2Array.getJSONObject(i)));
                                    }
                                }

                                group2Adapter.notifyDataSetChanged();
                            }

                            if (todayJsonObject.containsKey(GlobalConstants.JSON_GROUP3)) {
                                JSONArray group3Array = todayJsonObject.getJSONArray(GlobalConstants.JSON_GROUP3);
                                if (group3Array != null && group3Array.size() > 0) {
                                    for (int i=0; i < group3Array.size(); i++) {
                                        group3List.add(new Credit(group3Array.getJSONObject(i)));
                                    }
                                }

                                group3Adapter.notifyDataSetChanged();
                            }
                        }

                    }
                }
                break;
        }
    }
}
