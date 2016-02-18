package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.SystemMessageAdapter;
import com.xiaobukuaipao.youngmam.domain.BannerActivity;
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
public class SystemMessageActivity extends BaseHttpFragmentActivity {
    private static final String TAG = SystemMessageActivity.class.getSimpleName();

    public static final int TYPE_SYSTEM_MESSAGE = 10000;

    private ListView styemMessageView;
    private List<MamMessage> messageList;
    private SystemMessageAdapter systemMessageAdapter;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    private long minMsgId = -1;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_system_message);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        styemMessageView = (ListView) findViewById(R.id.system_message_list_view);
        messageList = new ArrayList<MamMessage>();
        systemMessageAdapter = new SystemMessageAdapter(this, messageList, R.layout.item_system_message);

        LinearLayout topSpacing = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.activity_basic_margin_10dp));
        topSpacing.setLayoutParams(params);
        styemMessageView.addHeaderView(topSpacing);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        styemMessageView.setAdapter(systemMessageAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                // 取下一页
                mEventLogic.getMessageByType(TYPE_SYSTEM_MESSAGE, String.valueOf(minMsgId), -1);
            }
        });

        setUIListeners();
    }

    public void executeHttpRequest() {
        mEventLogic.getMessageByType(TYPE_SYSTEM_MESSAGE, String.valueOf(minMsgId), -1);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_system_message));

        setBackClickListener(this);
    }

    private void setUIListeners() {
        styemMessageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MamMessage mamMessage = (MamMessage) parent.getItemAtPosition(position);
                if (mamMessage != null) {
                    switch (mamMessage.getType()) {
                        case YoungMessage.NOTIFY_TYPE_ACTIVITY_ACTIVE:
                            BannerActivity bannerActivity = new BannerActivity();
                            if (!StringUtil.isEmpty(mamMessage.getArticleId())) {
                                bannerActivity.setBusinessId(mamMessage.getArticleId());
                            }
                            Intent latestArticleIntent = new Intent(SystemMessageActivity.this, LatestActivity.class);
                            latestArticleIntent.putExtra("normal_activity", bannerActivity);
                            startActivity(latestArticleIntent);
                            break;

                        case YoungMessage.NOTIFY_TYPE_SPECIAL_PUBLISH:
                            Topic topic = new Topic();
                            if (!StringUtil.isEmpty(mamMessage.getArticleId())) {
                                topic.setBusinessId(mamMessage.getArticleId());
                                topic.setBusinessType(YoungBusinessType.BUSINESS_TYPE_SPECIAL_PUBLISH);
                            }
                            Intent intent = new Intent(SystemMessageActivity.this, SpecialTopicActivity.class);
                            intent.putExtra("topic", topic);
                            startActivity(intent);
                            break;

                        case YoungMessage.NOTIFY_TYPE_WEB_ACTIVITY_ACTIVE:
                            if (!StringUtil.isEmpty(mamMessage.getTargetUrl())) {
                                Intent webActivityIntent = new Intent(SystemMessageActivity.this, BannerH5Activity.class);
                                webActivityIntent.putExtra("target_url", mamMessage.getTargetUrl());
                                startActivity(webActivityIntent);
                            }
                            break;
                        case YoungMessage.NOTIFY_TYPE_WEBPAGE_PUBLISH:
                            if (!StringUtil.isEmpty(mamMessage.getTargetUrl())) {
                                Topic topicWeb = new Topic();
                                topicWeb.setTargetUrl(mamMessage.getTargetUrl());
                                Intent webpageIntent = new Intent(SystemMessageActivity.this, TopicWebActivity.class);
                                webpageIntent.putExtra("topic", topicWeb);
                                startActivity(webpageIntent);
                            }
                            break;
                        case YoungMessage.NOTIFY_TYPE_URL_PUBLISH:
                            if (!StringUtil.isEmpty(mamMessage.getTargetUrl())) {
                                Topic topicUrl = new Topic();
                                topicUrl.setTargetUrl(mamMessage.getTargetUrl());
                                Intent urlIntent = new Intent(SystemMessageActivity.this, TopicWebActivity.class);
                                urlIntent.putExtra("topic", topicUrl);
                                startActivity(urlIntent);
                            }
                            break;
                        case YoungMessage.NOTIFY_TYPE_THEME_PUBLISH:
                            Intent themePublishIntent = new Intent(SystemMessageActivity.this, SearchDetailActivity.class);
                            themePublishIntent.putExtra("tag_id", mamMessage.getArticleId());
                            themePublishIntent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                            startActivity(themePublishIntent);
                            break;

                        case YoungMessage.NOTIFY_TYPE_ARTICLE_PUBLISH:
                            Intent articlePublishIntent = new Intent(SystemMessageActivity.this, FreshDetailsActivity.class);
                            articlePublishIntent.putExtra("article_id", mamMessage.getArticleId());
                            startActivity(articlePublishIntent);
                            break;
                        default:
                            break;
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

                        Log.d(TAG, "system message : " + httpResult.getData());
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

                    systemMessageAdapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }
}
