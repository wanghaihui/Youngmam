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
import com.xiaobukuaipao.youngmam.adapter.LikeCommentAdapter;
import com.xiaobukuaipao.youngmam.domain.MamMessage;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.message.YoungBusinessType;
import com.xiaobukuaipao.youngmam.message.YoungMessage;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-10-24.
 */
public class LikeAndCommentActivity extends BaseHttpFragmentActivity {

    public static final int TYPE_LIKE = 200;
    public static final int TYPE_COMMENT = 100;

    private int type;

    private ListView likeCommentView;
    private List<MamMessage> messageList;
    private LikeCommentAdapter likeCommentAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    private long minMsgId = -1;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_like_and_comment);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        likeCommentView = (ListView) findViewById(R.id.like_comment_list_view);
        messageList = new ArrayList<MamMessage>();
        likeCommentAdapter = new LikeCommentAdapter(this, messageList, R.layout.item_like_comment);

        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp));
        topSpacing.setLayoutParams(params);
        likeCommentView.addHeaderView(topSpacing);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        likeCommentView.setAdapter(likeCommentAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 取下一页
                if (type == TYPE_LIKE) {
                    mEventLogic.getMessageByType(TYPE_LIKE, String.valueOf(minMsgId), -1);
                } else if (type == TYPE_COMMENT) {
                    mEventLogic.getMessageByType(TYPE_COMMENT, String.valueOf(minMsgId), -1);
                }
            }
        });

        setUIListeners();
    }

    private void getIntentDatas() {
        type = getIntent().getIntExtra("type", 0);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        if (type == TYPE_LIKE) {
            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_like_num));
        } else if (type == TYPE_COMMENT) {
            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_comment_num));
        }
        setBackClickListener(this);
    }

    public void executeHttpRequest() {
        // 取消息列表
        if (type == TYPE_LIKE) {
            mEventLogic.getMessageByType(TYPE_LIKE, String.valueOf(minMsgId), -1);
        } else if (type == TYPE_COMMENT) {
            mEventLogic.getMessageByType(TYPE_COMMENT, String.valueOf(minMsgId), -1);
        }
    }

    private void setUIListeners() {
        likeCommentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MamMessage mamMessage = (MamMessage) parent.getItemAtPosition(position);

                if (mamMessage != null) {
                    if (mamMessage.getGroupType() == TYPE_LIKE) {
                        // 此时是喜欢列表
                        switch (mamMessage.getType()) {
                            case YoungMessage.MSG_TYPE_ARTICLE_LIKE:
                            case YoungMessage.MSG_TYPE_ARTICLE_COMMENT_LIKE:
                                Intent articleIntent = new Intent(LikeAndCommentActivity.this, FreshDetailsActivity.class);
                                articleIntent.putExtra("article_id", mamMessage.getArticleId());
                                startActivity(articleIntent);
                                break;
                            case YoungMessage.MSG_TYPE_QUESTION_COMMENT_LIKE:
                                Intent questionIntent = new Intent(LikeAndCommentActivity.this, QuestionDetailsActivity.class);
                                questionIntent.putExtra("article_id", mamMessage.getArticleId());
                                startActivity(questionIntent);
                                break;
                            case YoungMessage.MSG_TYPE_SPECIAL_COMMENT_LIKE:
                                Topic topic = new Topic();
                                if (!StringUtil.isEmpty(mamMessage.getArticleId())) {
                                    topic.setBusinessId(mamMessage.getArticleId());
                                    topic.setBusinessType(YoungBusinessType.BUSINESS_TYPE_SPECIAL_PUBLISH);
                                }
                                Intent intent = new Intent(LikeAndCommentActivity.this, SpecialTopicActivity.class);
                                intent.putExtra("topic", topic);
                                startActivity(intent);
                                break;
                        }
                    } else if (mamMessage.getGroupType() == TYPE_COMMENT) {
                        // 此时是评论列表
                        switch (mamMessage.getType()) {
                            case YoungMessage.MSG_TYPE_ARTICLE_COMMENT:
                            case YoungMessage.MSG_TYPE_ARTICLE_COMMENT_REPLY:
                                Intent articleIntent = new Intent(LikeAndCommentActivity.this, FreshDetailsActivity.class);
                                articleIntent.putExtra("article_id", mamMessage.getArticleId());
                                startActivity(articleIntent);
                                break;
                            case YoungMessage.MSG_TYPE_QUESTION_COMMENT:
                            case YoungMessage.MSG_TYPE_QUESTION_COMMENT_REPLY:
                                Intent questionIntent = new Intent(LikeAndCommentActivity.this, QuestionDetailsActivity.class);
                                questionIntent.putExtra("article_id", mamMessage.getArticleId());
                                startActivity(questionIntent);
                                break;
                            case YoungMessage.MSG_TYPE_SPECIAL_COMMENT_REPLY:
                                Topic topic = new Topic();
                                if (!StringUtil.isEmpty(mamMessage.getArticleId())) {
                                    topic.setBusinessId(mamMessage.getArticleId());
                                    topic.setBusinessType(YoungBusinessType.BUSINESS_TYPE_SPECIAL_PUBLISH);
                                }
                                Intent intent = new Intent(LikeAndCommentActivity.this, SpecialTopicActivity.class);
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
            case R.id.get_message_by_type:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                messageList.add(new MamMessage(jsonArray.getJSONObject(i)));
                            }
                        }

                        minMsgId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINMSGID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minMsgId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }

                    likeCommentAdapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }

}
