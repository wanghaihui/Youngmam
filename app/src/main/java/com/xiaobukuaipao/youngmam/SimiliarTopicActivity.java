package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.adapter.ArticleAdapter;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener;
import com.xiaobukuaipao.youngmam.domain.ArticleChangeEvent;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.LikeCommentNumberEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreContainer;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreHandler;
import com.xiaobukuaipao.youngmam.loadmore.LoadMoreListViewContainer;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.CommentDialog;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class SimiliarTopicActivity extends BaseHttpFragmentActivity implements OnActionClickListener, CommentDialog.OnSendClickListener {
    private static final String TAG = SimiliarTopicActivity.class.getSimpleName();

    private ListView mSimiliarListView;

    private ArticleAdapter mArticleAdapter;
    private List<Article> mArticleList;

    private String editorTagId;
    private String title;

    private long minArticleId = -1;

    // 缓存
    private YoungCache youngCache;

    // EventBus
    private EventBus eventBus;

    private LoadMoreListViewContainer loadMoreListViewContainer;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);
    // 分享的内容
    private String shareContent;
    // 分享的内容的链接
    private String targetUrl;
    // 分享的图片--活动或文章或主题
    private String imageUrl;

    public void initViews() {
        setContentView(R.layout.activity_similiar_topic);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        youngCache = YoungCache.get(this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        mSimiliarListView = (ListView) findViewById(R.id.similiar_topic_list);

        // 配置需要分享的相关平台
        configPlatforms(mController);

        // 设置数据
        initDatas();
    }

    private void getIntentDatas() {
        editorTagId = getIntent().getStringExtra("editor_tag_id");
        title = getIntent().getStringExtra("title");
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, title);

        setBackClickListener(this);
    }

    private void initDatas() {
        mArticleList = new ArrayList<Article>();

        mArticleAdapter = new ArticleAdapter(this, mArticleList, R.layout.item_fresh);
        mArticleAdapter.setOnActionClickListener(this);

        // Load More Container
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_container);
        loadMoreListViewContainer.useDefaultFooter();

        mSimiliarListView.setAdapter(mArticleAdapter);

        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mEventLogic.getEditorChoiceArticle(editorTagId, String.valueOf(minArticleId));
            }
        });

        setUIListeners();
    }

    private void setUIListeners() {
        mSimiliarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (article != null) {
                    Intent intent = new Intent(SimiliarTopicActivity.this, FreshDetailsActivity.class);
                    intent.putExtra("article_id", article.getArticleId());
                    startActivity(intent);
                }
            }
        });
    }

    public void executeHttpRequest() {
        // 在这里获得编辑标签下的文章
        mEventLogic.getEditorChoiceArticle(editorTagId, String.valueOf(minArticleId));
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_editor_choice_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        for (int i = 0; i < jsonArray.size(); i++) {
                            mArticleList.add(new Article(jsonArray.getJSONObject(i)));
                        }

                        minArticleId = Long.valueOf(jsonObject.getString(GlobalConstants.JSON_MINARTICLEID));

                        // load more complete
                        loadMoreListViewContainer.loadMoreFinish((jsonArray == null || (jsonArray != null && jsonArray.size() > 0)), minArticleId > 0);
                    } else {
                        loadMoreListViewContainer.loadMoreFinish(true, false);
                    }
                    mArticleAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.delete_like_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            for (int i = 0; i < mArticleList.size(); i++) {
                                if (articleId.equals(mArticleList.get(i).getArticleId())) {
                                    // 此时,喜欢成功,将我的喜欢的缓存清空
                                    youngCache.remove(YoungCache.CACHE_MINE_LIKE);
                                    EventBus.getDefault().post(new ArticleChangeEvent(mArticleList.get(i).getArticleId(), ArticleChangeEvent.ARTICLE_NOT_LIKE));
                                }
                            }
                        }
                    }

                    mArticleAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.add_like_article:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            for (int i = 0; i < mArticleList.size(); i++) {
                                if (articleId.equals(mArticleList.get(i).getArticleId())) {
                                    // 此时,喜欢成功,将我的喜欢的缓存清空
                                    youngCache.remove(YoungCache.CACHE_MINE_LIKE);
                                    EventBus.getDefault().post(new ArticleChangeEvent(mArticleList.get(i).getArticleId(), ArticleChangeEvent.ARTICLE_LIKE));
                                }
                            }
                        }
                    }

                    mArticleAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.add_article_comment:
                // 发送评论成功
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    String articleId = msg.getData().getString("key");

                    if (status == 0) {
                        // 此时,代表添加评论成功
                        // 此时,添加评论成功,评论页表需要刷新
                        youngCache.remove(YoungCache.CACHE_MINE_COMMENT);
                        EventBus.getDefault().post(new ArticleChangeEvent(articleId, ArticleChangeEvent.ARTICLE_COMMENT));
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActionClick(final int action, final int position, final Article article) {
        if (action == OnActionClickListener.ACTION_LIKE) {
            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                if (article.getHasLiked()) {
                    // 文章已经被喜欢,所以此时执行取消喜欢
                    Log.d(TAG, "has liked");
                    mEventLogic.cancelAll();
                    mEventLogic.deleteLikeArticle(article.getArticleId(), null);
                } else {
                    // 文章没有被喜欢,所以此时执行喜欢
                    mEventLogic.cancelAll();
                    mEventLogic.addLikeArticle(article.getArticleId());
                }
            } else {
                // 跳到登录页
                Intent intent = new Intent(SimiliarTopicActivity.this, RegisterAndLoginActivity.class);
                startActivity(intent);
            }
        } else if (action == OnActionClickListener.ACTION_COMMENT) {
            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                // 显示评论框
                showCommentDialog(article.getArticleId(), "", null);
            } else {
                // 跳到登录页
                Intent intent = new Intent(SimiliarTopicActivity.this, RegisterAndLoginActivity.class);
                startActivity(intent);
            }
        } else if (action == OnActionClickListener.ACTION_SHARE) {
            // 首先组合基本的分享单元

            String targetUrl = GlobalConstants.SHARE_ARTICLE + article.getArticleId();
            String shareContent = article.getContent();
            String imageUrl = null;
            if (article.getImgs().size() > 0) {
                imageUrl = article.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL);
            }

            YoungShareBoard shareBoard = new YoungShareBoard(this);
            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE);

            /**
             * 针对微博
             */

            mController.setShareContent(StringUtil.buildWeiboShareArticle(shareContent, targetUrl));
            mController.setShareMedia(new UMImage(this, imageUrl));
        }
    }

    public void onEvent(LikeCommentNumberEvent likeCommentNumberEvent) {

        if (mArticleList != null) {
            for (int i = 0; i < mArticleList.size(); i++) {
                if (mArticleList.get(i).getArticleId().equals(likeCommentNumberEvent.getArticleId())) {
                    mArticleList.get(i).setLikeCount(likeCommentNumberEvent.getLikeNum());
                    mArticleList.get(i).setCommentCount(likeCommentNumberEvent.getCommentNum());
                    mArticleAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 处理预加载
     */
    public void onEvent(ArticleChangeEvent articleChangeEvent) {
        Log.d(TAG, "article id :" + articleChangeEvent.getArticleId());
        if (mArticleList != null) {
            for (int i = 0; i < mArticleList.size(); i++) {
                if (mArticleList.get(i).getArticleId().equals(articleChangeEvent.getArticleId())) {
                    if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_LIKE) {
                        boolean hasLike = mArticleList.get(i).getHasLiked();
                        hasLike = true;
                        mArticleList.get(i).setHasLiked(hasLike);

                        int likeCount = mArticleList.get(i).getLikeCount();
                        likeCount++;
                        mArticleList.get(i).setLikeCount(likeCount);

                        mArticleAdapter.notifyDataSetChanged();
                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_NOT_LIKE) {
                        boolean hasLike = mArticleList.get(i).getHasLiked();
                        hasLike = false;
                        mArticleList.get(i).setHasLiked(hasLike);

                        int likeCount = mArticleList.get(i).getLikeCount();
                        likeCount--;
                        mArticleList.get(i).setLikeCount(likeCount);
                        mArticleAdapter.notifyDataSetChanged();
                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_COMMENT) {
                        int commentCount = mArticleList.get(i).getCommentCount();
                        commentCount++;
                        mArticleList.get(i).setCommentCount(commentCount);

                        mArticleAdapter.notifyDataSetChanged();

                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_NOT_COMMENT) {
                        int commentCount = mArticleList.get(i).getCommentCount();
                        commentCount--;
                        mArticleList.get(i).setCommentCount(commentCount);

                        mArticleAdapter.notifyDataSetChanged();
                    } else if (articleChangeEvent.getChange() == ArticleChangeEvent.ARTICLE_DELETE) {
                        mArticleList.remove(i);
                        mArticleAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void showCommentDialog(String articleId, String name, String originCommentId) {
        CommentDialog dialog = new CommentDialog(SimiliarTopicActivity.this, articleId, name, originCommentId);
        dialog.setOnSendClickListener(this);
        dialog.show();
    }

    @Override
    public void onSendClick(String articleId, String originCommentId, String content) {
        mEventLogic.addArticleComment(articleId, content, originCommentId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
