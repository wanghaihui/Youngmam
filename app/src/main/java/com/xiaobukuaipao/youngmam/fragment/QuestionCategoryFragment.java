package com.xiaobukuaipao.youngmam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.QuestionDetailsActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.Category;
import com.xiaobukuaipao.youngmam.domain.MineQuestionDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-9-25.
 */
public class QuestionCategoryFragment extends BaseEventHttpFragment {

    private static final String TAG = QuestionCategoryFragment.class.getSimpleName();

    private static final String ARG_CATEGORY = "category";
    private Category category;

    private ListView questionCategoryListView;
    private LoadMoreListViewContainer loadMoreListViewContainer;

    // 每一页的条数
    private int limit = -1;

    private boolean isRefresh = false;

    public static Fragment newInstance(Category category) {
        QuestionCategoryFragment fragment = new QuestionCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 位置
        category = getArguments().getParcelable(ARG_CATEGORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_category, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void initUIAndData() {
        super.initUIAndData();

        questionCategoryListView = (ListView) view.findViewById(R.id.question_category_list_view);

        LinearLayout topSpacing = new LinearLayout(this.getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                0);
        topSpacing.setLayoutParams(params);
        questionCategoryListView.addHeaderView(topSpacing);

        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        // binding view and data
        questionCategoryListView.setAdapter(article2Adapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 加载更多
                mEventLogic.getQuestionByTagName(category.getName(), String.valueOf(minArticleId), limit);
            }
        });

        loadDatas();

        setUIListeners();
    }

    private void loadDatas() {
        mEventLogic.getQuestionByTagName(category.getName(), String.valueOf(minArticleId), limit);
    }

    private void setUIListeners() {
        questionCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);

                if (article != null) {
                    Intent intent = new Intent(QuestionCategoryFragment.this.getActivity(), QuestionDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
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

            case R.id.get_question_by_tagname:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSON.parseObject(httpResult.getData());

                    if (jsonObject != null) {

                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (isRefresh) {
                            articleList.clear();
                            isRefresh = false;
                        }

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                articleList.add(new Article(jsonArray.getJSONObject(i)));
                            }
                        }

                        minArticleId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINARTICLEID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minArticleId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    article2Adapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 刷新问题列表
     * @param mineQuestionDelayRefreshEvent
     */
    public void onEvent(MineQuestionDelayRefreshEvent mineQuestionDelayRefreshEvent) {
        if (mineQuestionDelayRefreshEvent.getDelayRefresh()) {
            isRefresh = true;
            minArticleId = -1;
            mEventLogic.getQuestionByTagName(category.getName(), String.valueOf(minArticleId), limit);
        }
    }
}
