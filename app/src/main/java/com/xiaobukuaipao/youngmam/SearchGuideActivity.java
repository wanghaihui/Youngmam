package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.xiaobukuaipao.youngmam.adapter.TagAdapter;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.Tag;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.view.NestedGridView;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-10-27.
 */
public class SearchGuideActivity extends BaseHttpFragmentActivity {

    // 热门标签
    private NestedGridView tagGridView;

    private List<Tag> mTagList;
    private TagAdapter tagAdapter;

    private RelativeLayout mSearchView;

    @Override
    public void initViews() {
        /**
         * 恩,单独布局吧
         */
        setContentView(R.layout.activity_search_guide);

        mSearchView = (RelativeLayout) findViewById(R.id.search_view);

        tagGridView = (NestedGridView) findViewById(R.id.tag_grid_view);
        mTagList = new ArrayList<Tag>();
        tagAdapter = new TagAdapter(this, mTagList, R.layout.item_tag);
        tagGridView.setAdapter(tagAdapter);

        setUIListeners();
    }

    private void setUIListeners() {
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchGuideActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        tagGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag tag = (Tag) parent.getItemAtPosition(position);

                if (tag != null) {
                    Theme theme = new Theme(null, null, new Label(tag.getTagId(), tag.getName()));

                    Intent intent = new Intent(SearchGuideActivity.this, SearchDetailActivity.class);
                    intent.putExtra("tag_id", theme.getTag().getId());
                    intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                    startActivity(intent);
                }
            }
        });
    }

    public void executeHttpRequest() {
        // 获取热门标签
        mEventLogic.getHotTags();
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
                        mTagList.clear();

                        for(int i=0; i < jsonArray.size(); i++) {
                            mTagList.add(new Tag(jsonArray.getJSONObject(i)));
                        }

                        tagAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }

    }
}
