package com.xiaobukuaipao.youngmam;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.GridArticleAdapter;
import com.xiaobukuaipao.youngmam.adapter.LabelAdapter;
import com.xiaobukuaipao.youngmam.adapter.SearchCursorAdapter;
import com.xiaobukuaipao.youngmam.adapter.SearchTagAdapter;
import com.xiaobukuaipao.youngmam.adapter.TagAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.database.SearchTable;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleCommentEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleLikeEvent;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.Tag;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.GridViewWithHeaderAndFooter;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreGridViewContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.manager.YoungDatabaseManager;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.NestedListView;
import com.xiaobukuaipao.youngmam.widget.SearchView1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-7.
 */
public class SearchActivity extends BaseEventFragmentActivity implements LoaderCallbacks<Cursor>,
        SearchCursorAdapter.OnDeleteClickListener {
    private static final String TAG = SearchActivity.class.getSimpleName();

    private SearchView1 searchView1;

    private LinearLayout mLatestLayout;
    private ListView mLatestSearchView;
    private LinearLayout mHotLayout;
    private GridView mHotSearchView;
    private List<Tag> mTagList;
    private TagAdapter tagAdapter;
    private List<String> mLatestSearchList;
    // private List<Label> mHotSearchList;

    private SearchCursorAdapter latestSearchAdapter;
    // private HotSearchAdapter hotSearchAdapter;

    private RelativeLayout resultSearchLayout;
    private GridArticleAdapter mGridArticleAdapter;
    private GridViewWithHeaderAndFooter mSearchGridView;

    // Suggest
    private ListView mSearchLabelView;
    private LabelAdapter labelAdapter;
    private List<Label> labelList;

    // Search TextView
    private TextView searchTxtView;
    // Cancel TextView
    private TextView cancelTxtView;

    // 搜索文章,用于翻页
    private long start = 0;

    // Search标志
    private boolean search = false;

    private boolean searchArticle = false;

    // 缓存
    private YoungCache youngCache;

    private LoadMoreGridViewContainer loadMoreGridViewContainer;

    // 得到传递过来的word
    private String mIntentWord;

    // 最近搜索
    private LinearLayout mLatestSearch;
    // 清空搜索记录
    private LinearLayout mClearSearch;

    private LinearLayout noSearchData;

    // Tags--标签
    private NestedListView tagListView;
    private SearchTagAdapter searchTagAdapter;
    private List<Tag> searchTagList;

    @Override
    public void initViews() {
        super.initViews();
        /**
         * 恩,单独布局吧
         */
        setContentView(R.layout.activity_search);

        getIntentDatas();

        youngCache = YoungCache.get(this);

        // 搜索框的View
        searchView1 = (SearchView1) findViewById(R.id.search);
        searchView1.addTextChangedListener(mSearchTextWatcher);

        resultSearchLayout = (RelativeLayout) findViewById(R.id.result_search_layout);

        mSearchGridView = (GridViewWithHeaderAndFooter) findViewById(R.id.search_grid_view);

        addGridViewHeader();

        mLatestLayout = (LinearLayout) findViewById(R.id.latest_search);
        mLatestSearchView = (ListView) findViewById(R.id.latest_search_list);
        mHotLayout = (LinearLayout) findViewById(R.id.hot_search);
        mHotLayout.setVisibility(View.GONE);
        mHotSearchView = (GridView) findViewById(R.id.tag_grid_view);

        searchTxtView = (TextView) findViewById(R.id.txt_search);
        cancelTxtView = (TextView) findViewById(R.id.cancel);

        mSearchLabelView = (ListView) findViewById(R.id.search_label_list_view);

        mLatestSearch = (LinearLayout) findViewById(R.id.latest_search_layout);
        mClearSearch = (LinearLayout) findViewById(R.id.clear_search_layout);

        noSearchData = (LinearLayout) findViewById(R.id.no_search_data);

        // 配置需要分享的相关平台
        configPlatforms(mController);

        initDatas();
    }

    private void addGridViewHeader() {
        View view = this.getLayoutInflater().inflate(R.layout.search_grid_header, null);
        tagListView = (NestedListView) view.findViewById(R.id.tag_list_view);
        mSearchGridView.addHeaderView(view);
    }

    /**
     * 延时加载数据
     */
    public void delayedLoadDatas() {
        if (mIntentWord != null) {
            search = true;
            searchView1.setText(mIntentWord);

            start = 0;
            mEventLogic.searchArticle(searchView1.getText().toString(), String.valueOf(start));

            searchArticle = true;

            // 将word插入数据库
            insertWordToDatabase(searchView1.getText().toString());
            // 隐藏软键盘
            hideSoftInput();

            searchTxtView.setVisibility(View.GONE);
            cancelTxtView.setVisibility(View.VISIBLE);
        }
    }

    private void getIntentDatas() {
        mIntentWord = getIntent().getStringExtra("word");
    }

    private void initDatas() {
        mLatestSearchList = new ArrayList<String>();
        // mHotSearchList = new ArrayList<Label>();
        mTagList = new ArrayList<Tag>();
        labelList = new ArrayList<Label>();

        // hotSearchAdapter = new HotSearchAdapter(this, mHotSearchList, R.layout.item_hot_search);
        tagAdapter = new TagAdapter(this, mTagList, R.layout.item_tag);
        mHotSearchView.setAdapter(tagAdapter);

        mGridArticleAdapter = new GridArticleAdapter(this, articleList, R.layout.item_grid_fresh);
        mGridArticleAdapter.setOnActionClickListener(this);

        searchTagList = new ArrayList<Tag>();
        searchTagAdapter = new SearchTagAdapter(this, searchTagList, R.layout.item_select_theme_search);
        tagListView.setAdapter(searchTagAdapter);

        // Load More Container
        loadMoreGridViewContainer = (LoadMoreGridViewContainer) findViewById(R.id.load_more_container);
        loadMoreGridViewContainer.useDefaultFooter();

        mSearchGridView.setAdapter(mGridArticleAdapter);

        loadMoreGridViewContainer.setLoadMoreHandler( new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mEventLogic.searchArticle(searchView1.getText().toString(), String.valueOf(start));
            }
        });

        labelAdapter = new LabelAdapter(this, labelList, R.layout.item_label);
        mSearchLabelView.setAdapter(labelAdapter);

        mLatestSearchView.setVisibility(View.VISIBLE);
        mHotSearchView.setVisibility(View.VISIBLE);
        resultSearchLayout.setVisibility(View.GONE);
        noSearchData.setVisibility(View.GONE);

        // 搜索数据表中看看是否有搜索记录
        if (YoungDatabaseManager.getInstance().isExistSearchHistory()) {
            // 如果存在搜索记录
            mLatestSearch.setVisibility(View.VISIBLE);
            mClearSearch.setVisibility(View.VISIBLE);
        } else {
            // 如果不存在
            mLatestSearch.setVisibility(View.GONE);
            mClearSearch.setVisibility(View.GONE);
        }

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(0, null, this);

        setUIListeners();

    }

    private void setUIListeners() {
        // 热门搜索
        mHotSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag tag = (Tag) parent.getItemAtPosition(position);
                // 开始搜索
                search = true;

                start = 0;
                searchView1.setText(tag.getName());
                searchArticle = true;
                mEventLogic.searchArticle(tag.getName(), String.valueOf(start));

                // 将word插入数据库
                insertWordToDatabase(searchView1.getText().toString());
                // 隐藏软键盘
                hideSoftInput();

                searchTxtView.setVisibility(View.GONE);
                cancelTxtView.setVisibility(View.VISIBLE);
            }
        });

        searchTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始搜索
                search = true;
                start = 0;
                searchArticle = true;
                mEventLogic.searchArticle(searchView1.getText().toString(), String.valueOf(start));
                // 将word插入数据库
                if (!StringUtil.isEmpty(searchView1.getText().toString().trim())) {
                    insertWordToDatabase(searchView1.getText().toString());
                }
                // 隐藏软键盘
                hideSoftInput();

                // 同时将Tag--Label列表隐藏掉
                mSearchLabelView.setVisibility(View.GONE);

                searchTxtView.setVisibility(View.GONE);
                cancelTxtView.setVisibility(View.VISIBLE);
            }
        });

        searchView1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (!StringUtil.isEmpty(searchView1.getText().toString())) {
                        search = true;
                        start = 0;
                        searchArticle = true;
                        mEventLogic.searchArticle(searchView1.getText().toString(), String.valueOf(start));
                        // 将word插入数据库
                        insertWordToDatabase(searchView1.getText().toString());
                        // 隐藏软键盘
                        hideSoftInput();

                        // 同时将Tag--Label列表隐藏掉
                        mSearchLabelView.setVisibility(View.GONE);

                        searchTxtView.setVisibility(View.GONE);
                        cancelTxtView.setVisibility(View.VISIBLE);
                    }

                    return true;
                }
                return false;
            }
        });

        cancelTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁Activity
                if (StringUtil.isEmpty(searchView1.getText().toString())) {
                    finish();
                } else {
                    noSearchData.setVisibility(View.GONE);
                    resultSearchLayout.setVisibility(View.GONE);
                    mLatestLayout.setVisibility(View.VISIBLE);
                    mHotLayout.setVisibility(View.VISIBLE);

                    mSearchLabelView.setVisibility(View.GONE);

                    searchTxtView.setVisibility(View.GONE);
                    cancelTxtView.setVisibility(View.VISIBLE);

                    searchView1.setText("");

                    search = false;
                }
            }
        });

        mSearchLabelView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Label label = (Label) parent.getItemAtPosition(position);
                // 开始搜索
                search = true;

                start = 0;
                searchView1.setText(label.getTitle());
                searchArticle = true;
                mEventLogic.searchArticle(label.getTitle(), String.valueOf(start));

                // 将word插入数据库
                insertWordToDatabase(searchView1.getText().toString());
                // 隐藏软键盘
                hideSoftInput();

                searchTxtView.setVisibility(View.GONE);
                cancelTxtView.setVisibility(View.VISIBLE);
            }
        });

        // 最近搜索
        mLatestSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                cursor.moveToPosition(position);

                String word = cursor.getString(cursor.getColumnIndexOrThrow(SearchTable.COLUMN_WORD));

                // 开始搜索
                search = true;

                start = 0;
                searchView1.setText(word);
                searchArticle = true;
                mEventLogic.searchArticle(word, String.valueOf(start));

                // 隐藏软键盘
                hideSoftInput();

                searchTxtView.setVisibility(View.GONE);
                cancelTxtView.setVisibility(View.VISIBLE);
            }
        });

        mClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchDatabase();
            }
        });

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag tag = (Tag) parent.getItemAtPosition(position);
                if (tag != null) {

                    // 标签
                    if (tag.getType() == 1) {
                        Theme theme = new Theme(null, null, new Label(tag.getTagId(), tag.getName()));
                        Intent intent = new Intent(SearchActivity.this, SearchDetailActivity.class);
                        intent.putExtra("tag_id", theme.getTag().getId());
                        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                        startActivity(intent);
                    } else {
                        Theme theme = new Theme(tag.getDesc(), tag.getImgUrl(), new Label(tag.getTagId(), tag.getName()));
                        Intent intent = new Intent(SearchActivity.this, SearchDetailActivity.class);
                        intent.putExtra("tag_id", theme.getTag().getId());
                        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void clearSearchDatabase() {
        YoungDatabaseManager.getInstance().clearSearchDatabase();
        // 如果不存在
        mLatestSearch.setVisibility(View.GONE);
        mClearSearch.setVisibility(View.GONE);
    }

    private TextWatcher mSearchTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 一变化,便执行网络请求
            if (s.length() > 0) {
                noSearchData.setVisibility(View.GONE);
                resultSearchLayout.setVisibility(View.GONE);
                mLatestLayout.setVisibility(View.GONE);
                mHotLayout.setVisibility(View.GONE);

                if (!search) {
                    mSearchLabelView.setVisibility(View.VISIBLE);

                    searchTxtView.setVisibility(View.VISIBLE);
                    cancelTxtView.setVisibility(View.GONE);

                    mEventLogic.cancel(R.id.get_search_tag);
                    mEventLogic.getSearchTag(s.toString(), PublishActivity.TYPE_PUBLISH_ARTICLE);

                } else {
                    mSearchLabelView.setVisibility(View.GONE);

                    searchTxtView.setVisibility(View.GONE);
                    cancelTxtView.setVisibility(View.VISIBLE);

                    search = false;
                }

            } else {
                noSearchData.setVisibility(View.GONE);
                resultSearchLayout.setVisibility(View.GONE);
                mLatestLayout.setVisibility(View.VISIBLE);
                mHotLayout.setVisibility(View.VISIBLE);

                mSearchLabelView.setVisibility(View.GONE);

                searchTxtView.setVisibility(View.GONE);
                cancelTxtView.setVisibility(View.VISIBLE);

                search = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void executeHttpRequest() {
        // 得到热门标签
        mEventLogic.getHotTags();
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        super.onResponse(msg);

        switch (msg.what) {
            /*case R.id.get_recommend_tag:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());

                    if (jsonArray != null && jsonArray.size() > 0) {
                        for(int i=0; i < jsonArray.size(); i++) {
                            mHotSearchList.add(new Label(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_TAGID),
                                    jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_NAME)));
                        }

                        hotSearchAdapter.notifyDataSetChanged();
                    }
                }
                break;*/
            case R.id.get_hot_tags:
                if (checkResponse(msg)) {
                    mHotLayout.setVisibility(View.VISIBLE);

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

            case R.id.get_search_article:
                if (checkResponse(msg)) {

                    resultSearchLayout.setVisibility(View.VISIBLE);
                    mLatestLayout.setVisibility(View.GONE);
                    mHotLayout.setVisibility(View.GONE);

                    HttpResult httpResult = (HttpResult) msg.obj;

                    Log.d(TAG, "data : " + httpResult.getData());

                    if (searchArticle) {
                        articleList.clear();
                        searchTagList.clear();
                        searchArticle = false;
                    }

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        if (jsonObject.containsKey(GlobalConstants.JSON_TAGS)) {
                            JSONArray jsonTagArray = jsonObject.getJSONArray(GlobalConstants.JSON_TAGS);

                            if (jsonTagArray != null && jsonTagArray.size() > 0) {
                                for (int i = 0; i < jsonTagArray.size(); i++) {
                                    searchTagList.add(new Tag(jsonTagArray.getJSONObject(i)));
                                }
                            }
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        start = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_START));

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                articleList.add(new Article(jsonArray.getJSONObject(i)));
                            }
                        }

                        if ((articleList != null && articleList.size() > 0) || (searchTagList != null && searchTagList.size() > 0)) {
                            mSearchGridView.setVisibility(View.VISIBLE);
                            noSearchData.setVisibility(View.GONE);
                        } else {
                            mSearchGridView.setVisibility(View.GONE);
                            noSearchData.setVisibility(View.VISIBLE);
                        }

                        // load more complete
                        loadMoreGridViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), start > 0);
                    } else {
                        if ((articleList != null && articleList.size() > 0) || (searchTagList != null && searchTagList.size() > 0)) {
                            mSearchGridView.setVisibility(View.VISIBLE);
                            noSearchData.setVisibility(View.GONE);
                        } else {
                            mSearchGridView.setVisibility(View.GONE);
                            noSearchData.setVisibility(View.VISIBLE);
                        }

                        loadMoreGridViewContainer.loadMoreFinish(true, false);
                    }

                    searchTagAdapter.notifyDataSetChanged();
                    mGridArticleAdapter.notifyDataSetChanged();

                } else {
                    if ((articleList != null && articleList.size() > 0) || (searchTagList != null && searchTagList.size() > 0)) {
                        mSearchGridView.setVisibility(View.VISIBLE);
                        noSearchData.setVisibility(View.GONE);
                    } else {
                        mSearchGridView.setVisibility(View.GONE);
                        noSearchData.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case R.id.get_search_tag:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONArray jsonArray = JSON.parseArray(httpResult.getData());

                    Log.d(TAG, "data : " + httpResult.getData());
                    labelList.clear();

                    if (jsonArray != null && jsonArray.size() > 0) {
                        for(int i=0; i < jsonArray.size(); i++) {
                            labelList.add(new Label(jsonArray.getJSONObject(i).getString(GlobalConstants.JSON_TAGID),
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

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchView1.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 将搜索的关键词插入数据库
     */
    private void insertWordToDatabase(String word) {
        // 首先判断该Word数据库中是否存在,如果存在,那么先删除此Word

        Cursor cursor = getContentResolver().query(YoungContentProvider.SEARCH_CONTENT_URI, null, null, null, null);
        // 遍历
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (word.equals(cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_WORD)))) {
                    long id = cursor.getInt(cursor.getColumnIndex(SearchTable.COLUMN_ID));
                    getContentResolver().delete(YoungContentProvider.SEARCH_CONTENT_URI,
                            SearchTable.COLUMN_ID + "=?", new String[] { String.valueOf(id)});
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(SearchTable.COLUMN_WORD, word);
        getContentResolver().insert(YoungContentProvider.SEARCH_CONTENT_URI, values);

        // 此时, 如果没有数据
        if (mClearSearch.getVisibility() == View.GONE) {
            mLatestSearch.setVisibility(View.VISIBLE);
            mClearSearch.setVisibility(View.VISIBLE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, YoungContentProvider.SEARCH_CONTENT_URI, null, null,
                null, SearchTable.COLUMN_ID + " desc limit " + 20);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        latestSearchAdapter = new SearchCursorAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        latestSearchAdapter.setOnDeleteClickListener(this);
        mLatestSearchView.setAdapter(latestSearchAdapter);

        if (mLatestSearchView.getCount() > 0) {
            mLatestSearch.setVisibility(View.VISIBLE);
            mClearSearch.setVisibility(View.VISIBLE);
        } else {
            mLatestSearch.setVisibility(View.GONE);
            mClearSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        latestSearchAdapter.swapCursor(null);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDeleteClick(String id) {
        getContentResolver().delete(YoungContentProvider.SEARCH_CONTENT_URI, SearchTable.COLUMN_ID + "=?", new String[] { id });
    }

    public void onEvent(ArticleLikeEvent articleLikeEvent) {
        super.onEvent(articleLikeEvent);

        mGridArticleAdapter.notifyDataSetChanged();
    }

    public void onEvent(ArticleCommentEvent articleCommentEvent) {
        super.onEvent(articleCommentEvent);

        mGridArticleAdapter.notifyDataSetChanged();
    }

}
