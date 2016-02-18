package com.xiaobukuaipao.youngmam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.HotTopicActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SpecialTopicActivity;
import com.xiaobukuaipao.youngmam.TopicWebActivity;
import com.xiaobukuaipao.youngmam.adapter.TopicAdapter;
import com.xiaobukuaipao.youngmam.domain.LikeEvent;
import com.xiaobukuaipao.youngmam.domain.NavigationCategory;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.domain.TopicLikeEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-10-10.
 */
public class TopicCategoryFragment extends BaseHttpFragment implements TopicAdapter.OnThemeLikeClickListener {
    private static final String TAG = TopicCategoryFragment.class.getSimpleName();

    // 专题
    public static final int TYPE_TOPIC = 3;
    // special专题
    public static final int TYPE_SPECIAL = 7;

    private static final String ARG_CATEGORY = "category";
    private NavigationCategory category;
    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    private ListView topicCategoryListView;
    private LoadMoreListViewContainer loadMoreListViewContainer;

    private TopicAdapter topicAdapter;
    private List<Topic> topicList;

    // 翻页id
    private long minId = -1;
    // 每一页的条数
    private int limit = -1;

    // EventBus
    private EventBus eventBus;

    public static Fragment newInstance(NavigationCategory category) {
        TopicCategoryFragment fragment = new TopicCategoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_topic_category, container, false);
        this.view = view;
        mEventLogic = new YoungEventLogic(this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        return view;
    }

    @Override
    public void initUIAndData() {
        topicCategoryListView = (ListView) view.findViewById(R.id.topic_category_list_view);

        LinearLayout topSpacing = new LinearLayout(this.getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                0);
        topSpacing.setLayoutParams(params);
        topicCategoryListView.addHeaderView(topSpacing);

        topicList = new ArrayList<Topic>();
        topicAdapter = new TopicAdapter(this.getActivity(), topicList, R.layout.item_hot_topic_image);
        topicAdapter.setOnThemeLikeClickListener(this);

        loadMoreListViewContainer = (LoadMoreListViewContainer) view.findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        // binding view and data
        topicCategoryListView.setAdapter(topicAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 加载更多
                mEventLogic.getTopicByCategory(category.getId(), String.valueOf(minId), -1);
            }
        });

        loadDatas();

        setUIListeners();
    }

    private void loadDatas() {
        mEventLogic.getTopicByCategory(category.getId(), String.valueOf(minId), -1);
    }

    private void setUIListeners() {
        topicCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic topic = (Topic) parent.getItemAtPosition(position);

                if (topic != null) {
                    if (topic.getBusinessType() > 1000 && topic.getBusinessType() < 2000) {
                        Intent intent = new Intent(TopicCategoryFragment.this.getActivity(), TopicWebActivity.class);
                        intent.putExtra("topic", topic);
                        startActivity(intent);
                    } else {
                        switch (topic.getBusinessType()) {
                            case TYPE_TOPIC:
                                Intent intentNormal = new Intent(TopicCategoryFragment.this.getActivity(), HotTopicActivity.class);
                                intentNormal.putExtra("normal_topic", true);
                                intentNormal.putExtra("topic_id", topic.getBusinessId());
                                intentNormal.putExtra("topic_title", topic.getTitle());
                                startActivity(intentNormal);
                                break;
                            case TYPE_SPECIAL:
                                Intent intent = new Intent(TopicCategoryFragment.this.getActivity(), SpecialTopicActivity.class);
                                intent.putExtra("topic", topic);
                                startActivity(intent);
                                break;
                        }
                    }
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

            case R.id.get_topic_by_category:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSON.parseObject(httpResult.getData());

                    Log.d(TAG, "category datas : " + httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                topicList.add(new Topic(jsonArray.getJSONObject(i)));
                            }
                        }

                        minId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    topicAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.add_like:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String articleId = msg.getData().getString("key");
                    if (jsonObject != null) {

                        if (jsonObject.containsKey(GlobalConstants.JSON_LIKEID)) {
                            EventBus.getDefault().post(new TopicLikeEvent(articleId, LikeEvent.TYPE_ADD_LIKE));
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;

            case R.id.delete_like2:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String articleId = msg.getData().getString("key");
                    if (jsonObject != null) {

                        int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                        if (status == 0) {
                            EventBus.getDefault().post(new TopicLikeEvent(articleId, LikeEvent.TYPE_DELETE_LIKE));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onThemeLikeClick(Topic topic) {
        if (topic.isHasLiked()) {
            // 此时, 取消喜欢
            mEventLogic.deleteLike2(topic.getBusinessId(), String.valueOf(topic.getBusinessType()), null);
        } else {
            // 此时, 执行喜欢操作
            mEventLogic.addLike(topic.getBusinessId(), String.valueOf(topic.getBusinessType()));
        }
    }

    public void onEvent(TopicLikeEvent topicLikeEvent) {
        if (topicList != null && topicList.size() > 0) {
            for (int i = 0; i < topicList.size(); i++) {
                if (topicList.get(i).getBusinessId().equals(topicLikeEvent.getArticleId())) {
                    int likeCount = topicList.get(i).getLikeCount();
                    if (topicLikeEvent.getType() == LikeEvent.TYPE_ADD_LIKE) {
                        likeCount = likeCount + 1;
                        topicList.get(i).setLikeCount(likeCount);
                        topicList.get(i).setHasLiked(true);
                    } else if (topicLikeEvent.getType() == LikeEvent.TYPE_DELETE_LIKE) {
                        likeCount = likeCount - 1;
                        topicList.get(i).setLikeCount(likeCount);
                        topicList.get(i).setHasLiked(false);
                    }

                    topicAdapter.notifyDataSetChanged();
                    break;
                }
            }


        }
    }

}
