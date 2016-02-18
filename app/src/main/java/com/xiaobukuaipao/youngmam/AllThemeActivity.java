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
import com.xiaobukuaipao.youngmam.adapter.ThemeAdapter;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-1.
 */
public class AllThemeActivity extends BaseEventFragmentActivity {
    private static final String TAG = AllThemeActivity.class.getSimpleName();

    // 用于翻页
    private long minLastArticleId = -1;

    private ListView themeListView;
    private List<Theme> mThemeList;
    private ThemeAdapter themeAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    @Override
    public void initViews() {
        super.initViews();

        setContentView(R.layout.activity_all_theme);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        themeListView = (ListView) findViewById(R.id.all_theme_list);
        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                0);
        topSpacing.setLayoutParams(params);
        themeListView.addHeaderView(topSpacing);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mEventLogic.getAllTheme(String.valueOf(minLastArticleId), -1);
            }
        });

        initDatas();

        setUIListeners();
    }

    private void initDatas() {
        mThemeList = new ArrayList<Theme>();
        themeAdapter = new ThemeAdapter(this, mThemeList, R.layout.item_select_theme);
        themeListView.setAdapter(themeAdapter);
    }

    private void setUIListeners() {
        themeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Theme theme = (Theme) parent.getItemAtPosition(position);
                if (theme != null) {
                    Intent intent = new Intent(AllThemeActivity.this, SearchDetailActivity.class);
                    intent.putExtra("tag_id", theme.getTag().getId());
                    intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
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
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_all_theme));

        setBackClickListener(this);
    }

    public void executeHttpRequest() {
        // 获取全部话题
        mEventLogic.getAllTheme(String.valueOf(minLastArticleId), -1);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_all_theme:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                mThemeList.add(new Theme(jsonArray.getJSONObject(i)));
                            }
                        }

                        minLastArticleId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINLASTARTICLEID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minLastArticleId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    themeAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
