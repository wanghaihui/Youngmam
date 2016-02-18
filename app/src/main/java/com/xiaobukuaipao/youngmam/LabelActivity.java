package com.xiaobukuaipao.youngmam;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xiaobukuaipao.youngmam.adapter.LabelAdapter;
import com.xiaobukuaipao.youngmam.database.LabelTable;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.LabelView;
import com.xiaobukuaipao.youngmam.widget.LabelListView;
import com.xiaobukuaipao.youngmam.widget.SearchView;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-6.
 */
public class LabelActivity extends BaseHttpFragmentActivity implements LabelListView.OnLabelCheckedChangedListener, LabelListView.OnLabelClickListener {
    private static final String TAG = LabelActivity.class.getSimpleName();

    private SearchView searchView;

    private LinearLayout personalAndHotLabelLayout;
    private LinearLayout searchLabelLayout;

    private LabelListView personalLabelView;
    private LabelListView hotLabelView;

    private List<Label> personalList;
    private List<Label> hotList;

    private ListView searchListView;
    private List<Label> searchList;
    private LabelAdapter labelAdapter;

    // 添加按钮
    private TextView mAddBtn;

    // 专属标签提示Layout
    private LinearLayout personalLabelLayout;

    // 选择的标签
    private ArrayList<Label> selectedLabel = new ArrayList<Label>();

    /**
     * 添加标签
     */
    private Label addLabel = new Label();

    private ArrayList<Label> selectedLabels;

    private int type;

    /**
     * 初始化Views
     */
    public void initViews() {
        setContentView(R.layout.activity_label);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        searchView = (SearchView) findViewById(R.id.search_bar);
        searchView.addTextChangedListener(mLabelTextWatcher);

        personalAndHotLabelLayout = (LinearLayout) findViewById(R.id.personal_hot_label_layout);
        personalAndHotLabelLayout.setVisibility(View.VISIBLE);
        searchLabelLayout = (LinearLayout) findViewById(R.id.search_label_layout);
        searchLabelLayout.setVisibility(View.GONE);

        personalLabelView = (LabelListView) findViewById(R.id.personal_label_list);
        hotLabelView = (LabelListView) findViewById(R.id.hot_label_list);

        searchListView = (ListView) findViewById(R.id.search_list);

        mAddBtn = (TextView) findViewById(R.id.add_label);

        personalLabelLayout = (LinearLayout) findViewById(R.id.personal_label_layout);

        personalLabelLayout.setVisibility(View.GONE);
        personalLabelView.setVisibility(View.GONE);

        personalList = new ArrayList<Label>();
        hotList = new ArrayList<Label>();
        searchList = new ArrayList<Label>();

        initDatas();

        setUIListeners();
    }

    private void getIntentDatas() {
        selectedLabels = getIntent().getParcelableArrayListExtra("label_list");
        type = getIntent().getIntExtra("type", 0);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_label_added));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_ok));

        setBackClickListener(this);
    }

    private void initDatas() {

        getPersonalLablesFromDatabase();

        if (personalList.size() > 0) {
            personalLabelLayout.setVisibility(View.VISIBLE);
            personalLabelView.setVisibility(View.VISIBLE);
        } else {
            personalLabelLayout.setVisibility(View.GONE);
            personalLabelView.setVisibility(View.GONE);
        }

        addLabel.setId("-1");
        addLabel.setTitle("");

        personalLabelView.setOnLabelCheckedChangedListener(this);
        hotLabelView.setOnLabelCheckedChangedListener(this);

        personalLabelView.setLabels(personalList);

        labelAdapter = new LabelAdapter(this, searchList, R.layout.item_label);
        searchListView.setAdapter(labelAdapter);


    }

    private void setUIListeners() {
        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "personal list size : " + personalList.size());

                for (int i = 0; i < personalList.size(); i++) {
                    Log.d(TAG, personalList.get(i).getId());
                    if (personalList.get(i).getChecked()) {
                        selectedLabel.add(personalList.get(i));
                    }
                }

                for (int i = 0; i < hotList.size(); i++) {
                    if (hotList.get(i).getChecked()) {
                        selectedLabel.add(hotList.get(i));
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("label_list", selectedLabel);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(searchView.getText().toString())) {

                    if (!containsLabel(searchView.getText().toString())) {
                        if (addLabel.getId().equals("-1")) {
                            addLabel.setTitle(searchView.getText().toString());
                        }

                        addLabel.setChecked(true);

                        // 这样
                        Label label = new Label(addLabel.getId(), addLabel.getTitle());
                        label.setChecked(true);
                        personalList.add(label);

                        // 插入数据库
                        if (!label.getId().equals("-1")) {
                            insertToDatabase(Long.valueOf(label.getId()), label.getTitle());
                        }

                        personalLabelView.addLabel(addLabel);

                        // 状态控制
                        addLabel();
                    } else {
                        if (personalList != null && personalList.size() > 0) {
                            personalLabelView.setLabels(personalList);
                        }

                        if (hotList != null && hotList.size() > 0) {
                            hotLabelView.setLabels(hotList);
                        }
                        addLabel();
                    }
                }
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getItemAtPosition(position);
                if (label != null && !containsLabel(label.getTitle())) {
                    addLabel.setId(label.getId());
                    addLabel.setTitle(label.getTitle());

                    searchView.setText(label.getTitle());

                    searchList.clear();
                    labelAdapter.notifyDataSetChanged();
                } else {
                    if (personalList != null && personalList.size() > 0) {
                        personalLabelView.setLabels(personalList);
                    }

                    if (hotList != null && hotList.size() > 0) {
                        hotLabelView.setLabels(hotList);
                    }
                    addLabel();
                }
            }
        });
    }

    private boolean containsLabel(String word) {
        if (personalList != null && personalList.size() > 0) {
            for(int i=0; i < personalList.size(); i++) {
                if (personalList.get(i).getTitle().equals(word)) {
                    personalList.get(i).setChecked(true);
                    return true;
                }
            }
        }

        if (hotList != null && hotList.size() > 0) {
            for(int i=0; i < hotList.size(); i++) {
                if (hotList.get(i).getTitle().equals(word)) {
                    hotList.get(i).setChecked(true);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 添加标签
     */
    private void addLabel() {

        personalAndHotLabelLayout.setVisibility(View.VISIBLE);
        searchLabelLayout.setVisibility(View.GONE);
        mAddBtn.setEnabled(true);
        searchView.setText("");

        if (personalList.size() > 0) {
            personalLabelLayout.setVisibility(View.VISIBLE);
            personalLabelView.setVisibility(View.VISIBLE);
        } else {
            personalLabelLayout.setVisibility(View.GONE);
            personalLabelView.setVisibility(View.GONE);
        }

        // 置为默认
        addLabel.setId("-1");
        addLabel.setTitle("");
    }

    @Override
    public void onLabelCheckedChanged(LabelView labelView, Label label) {
        Log.d(TAG, "on label checked changed :" + label.getChecked());
        labelView.setChecked(label.getChecked());
    }

    @Override
    public void onLabelClick(LabelView labelView, Label label) {

    }

    /////////////////////////////////////////////////////////////////////////
    private TextWatcher mLabelTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 一变化,便执行网络请求
            if (s.length() > 0) {
                personalAndHotLabelLayout.setVisibility(View.GONE);
                searchLabelLayout.setVisibility(View.VISIBLE);
                mAddBtn.setBackgroundResource(R.drawable.btn_add_label);
                mAddBtn.setTextColor(getResources().getColor(R.color.white));

                mEventLogic.cancel(R.id.get_search_tag);
                mEventLogic.getSearchTag(s.toString(), type);
            } else {
                personalAndHotLabelLayout.setVisibility(View.VISIBLE);
                searchLabelLayout.setVisibility(View.GONE);
                mAddBtn.setBackgroundResource(R.drawable.btn_add_label_unclick);
                mAddBtn.setTextColor(getResources().getColor(R.color.color_505050));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /////////////////////////////////////////////////////////////////////////////////////////////

    public void executeHttpRequest() {
        // 得到热门标签
        mEventLogic.getHotTag(type, null);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_hot_tag:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());

                    if (jsonArray != null && jsonArray.size() > 0) {
                        for(int i=0; i < jsonArray.size(); i++) {
                            hotList.add(new Label(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_TAGID),
                                    jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME)));
                        }

                        if (selectedLabels != null && selectedLabels.size() > 0) {
                            if (hotList.size() > 0) {
                                for (int i = 0; i < selectedLabels.size(); i++) {
                                    for (int j = 0; j < hotList.size(); j++) {
                                        if (selectedLabels.get(i).getId().equals(hotList.get(j).getId())) {
                                            hotList.get(j).setChecked(true);
                                        }
                                    }
                                }
                            }
                        }

                        hotLabelView.setLabels(hotList);
                    }

                }
                break;

            case R.id.get_search_tag:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());

                    Log.d(TAG, "data : " + httpResult.getData());
                    searchList.clear();

                    if (jsonArray != null && jsonArray.size() > 0) {
                        for(int i=0; i < jsonArray.size(); i++) {
                            searchList.add(new Label(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_TAGID),
                                    jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME)));
                        }
                    }

                    labelAdapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public synchronized void getPersonalLablesFromDatabase() {
        Cursor cursor = getContentResolver().query(YoungContentProvider.LABEL_CONTENT_URI, null, null, null, null, null);
        // 遍历
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String tagId = String.valueOf(cursor.getLong(cursor.getColumnIndex(LabelTable.COLUMN_TAG_ID)));
                String name = cursor.getString(cursor.getColumnIndex(LabelTable.COLUMN_NAME));
                Label label = new Label(tagId, name);
                personalList.add(label);
            } while (cursor.moveToNext());
            cursor.close();
        }


        if (selectedLabels != null && selectedLabels.size() > 0) {

            if (personalList.size() > 0) {
                for (int i = 0; i < selectedLabels.size(); i++) {
                    for (int j = 0; j < personalList.size(); j++) {
                        if (selectedLabels.get(i).getId().equals(personalList.get(j).getId())) {
                            personalList.get(j).setChecked(true);
                        }
                    }
                }
            }

            for(int i=0; i < selectedLabels.size(); i++) {
                if (selectedLabels.get(i).getId().equals("-1")) {
                    personalList.add(selectedLabels.get(i));

                }
            }
        }
    }

    public synchronized void insertToDatabase(Long tagId, String name) {
        ContentValues values = new ContentValues();
        values.put(LabelTable.COLUMN_TAG_ID, tagId);
        values.put(LabelTable.COLUMN_NAME, name);
        // 插入数据库
        getContentResolver().insert(YoungContentProvider.LABEL_CONTENT_URI, values);
    }

}
