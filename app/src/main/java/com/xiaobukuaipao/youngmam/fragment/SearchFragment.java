package com.xiaobukuaipao.youngmam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xiaobukuaipao.youngmam.AllThemeActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SearchActivity;
import com.xiaobukuaipao.youngmam.SearchDetailActivity;
import com.xiaobukuaipao.youngmam.adapter.TagAdapter;
import com.xiaobukuaipao.youngmam.adapter.ThemeAdapter;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.Tag;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.view.NestedGridView;
import com.xiaobukuaipao.youngmam.view.NestedListView;
import com.xiaobukuaipao.youngmam.widget.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-3.
 */
public class SearchFragment extends BaseHttpFragment implements ObservableScrollView.Callbacks {
    private static final String TAG = SearchFragment.class.getSimpleName();

    private ObservableScrollView mScrollView;

    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    // 热门标签
    private NestedGridView tagGridView;

    private List<Tag> mTagList;
    private TagAdapter tagAdapter;

    // 查看更多话题
    private LinearLayout mMoreTheme;

    // 热门话题
    private NestedListView themeListView;
    private ThemeAdapter selectThemeAdapter;
    private List<Theme> selectThemeList;

    private RelativeLayout mSearchView;

    private boolean hotTagRefresh = false;
    private boolean hotThemeRefresh = false;

    // 记录当前的ScrollView的Y值
    private int currentScrollY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_search, container, false);
        this.view = view;
        mEventLogic = new YoungEventLogic(this);
        return view;
    }

    @Override
    public void initUIAndData() {
        mSearchView = (RelativeLayout) view.findViewById(R.id.search_view);

        mScrollView = (ObservableScrollView) view.findViewById(R.id.scroll_view);
        mScrollView.smoothScrollTo(0, 0);

        mScrollView.setCallbacks(this);
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onScrollChanged(mScrollView.getScrollY());
                    }
                });

        tagGridView = (NestedGridView) view.findViewById(R.id.tag_grid_view);

        mMoreTheme = (LinearLayout) view.findViewById(R.id.more_theme);

        themeListView = (NestedListView) view.findViewById(R.id.hot_theme_list);

        mTagList = new ArrayList<Tag>();

        tagAdapter = new TagAdapter(this.getActivity(), mTagList, R.layout.item_tag);
        tagGridView.setAdapter(tagAdapter);

        selectThemeList = new ArrayList<Theme>();
        selectThemeAdapter = new ThemeAdapter(this.getActivity(), selectThemeList, R.layout.item_select_theme);
        themeListView.setAdapter(selectThemeAdapter);

        // 获取热门标签
        mEventLogic.getHotTags();
        /**
         * 获取热门话题
         */
        mEventLogic.getOnboardTheme();

        setUIListeners();
    }

    private void setUIListeners() {
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFragment.this.getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        tagGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag tag = (Tag) parent.getItemAtPosition(position);

                if (tag != null) {
                    Theme theme = new Theme(null, null, new Label(tag.getTagId(), tag.getName()));

                    Intent intent = new Intent(SearchFragment.this.getActivity(), SearchDetailActivity.class);
                    intent.putExtra("tag_id", theme.getTag().getId());
                    intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                    startActivity(intent);
                }
            }
        });

        mMoreTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFragment.this.getActivity(), AllThemeActivity.class);
                startActivity(intent);
            }
        });

        themeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Theme theme = (Theme) parent.getItemAtPosition(position);
                if (theme != null) {
                    Intent intent = new Intent(SearchFragment.this.getActivity(), SearchDetailActivity.class);
                    intent.putExtra("tag_id", theme.getTag().getId());
                    intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
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
            case R.id.get_hot_tags:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());

                    if (jsonArray != null && jsonArray.size() > 0) {
                        if (hotTagRefresh) {
                            mTagList.clear();
                            hotTagRefresh = false;
                        }

                        for(int i=0; i < jsonArray.size(); i++) {
                            mTagList.add(new Tag(jsonArray.getJSONObject(i)));
                        }

                        tagAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.get_onboard_theme:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());

                    if (jsonArray != null && jsonArray.size() > 0) {
                        if (hotThemeRefresh) {
                            selectThemeList.clear();
                            hotThemeRefresh = false;
                        }

                        for(int i=0; i < jsonArray.size(); i++) {
                            selectThemeList.add(new Theme(jsonArray.getJSONObject(i)));
                        }

                        selectThemeAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    /**
     * 处理预加载
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            hotTagRefresh = true;
            mEventLogic.getHotTags();

            hotThemeRefresh = true;
            mEventLogic.getOnboardTheme();

            // ScrollView滚动到顶部
            // 解决显示定位的问题，是设置ScrollView.scrollTo()
            // 但是此方法在onCreate()方法中使用时，必须的另开一个线程
            // 因为此方法，需要在ScrollView中的内容完全加载之后，才会调用，在onCreate()方法没结束之前，ScrollView中的内容是没有完全加载的
            // 所以需要另开一个线程
            mScrollView.post(new Runnable() {
                public void run() {
                    tagGridView.setFocusable(false);
                    themeListView.setFocusable(false);
                    mScrollView.smoothScrollTo(0, currentScrollY);
                }
            });
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onScrollChanged(int scrollY) {
        currentScrollY = scrollY;
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent() {

    }
}
