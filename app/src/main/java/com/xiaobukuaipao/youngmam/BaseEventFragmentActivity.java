package com.xiaobukuaipao.youngmam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.view.Gravity;

import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.adapter.Article2Adapter;
import com.xiaobukuaipao.youngmam.domain.Article;
import com.xiaobukuaipao.youngmam.domain.ArticleCommentEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleDeleteEvent;
import com.xiaobukuaipao.youngmam.domain.ArticleLikeEvent;
import com.xiaobukuaipao.youngmam.domain.CommentEvent;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.domain.LikeEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener;
import com.xiaobukuaipao.youngmam.listener.OnActionClickListener2;
import com.xiaobukuaipao.youngmam.listener.OnFollowClickListener;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-10-15.
 */
public abstract class BaseEventFragmentActivity extends BaseHttpFragmentActivity implements OnFollowClickListener,
        OnActionClickListener, OnActionClickListener2, YoungShareBoard.OnShareSuccessListener {

    protected long minArticleId = -1;

    protected Article2Adapter article2Adapter;
    protected List<Article> articleList;

    // EventBus
    protected EventBus eventBus;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);

    @Override
    public void initViews() {
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        articleList = new ArrayList<Article>();
        article2Adapter = new Article2Adapter(this, articleList);
        article2Adapter.setOnFollowClickListener(this);
        article2Adapter.setOnActionClickListener(this);
        article2Adapter.setOnActionClickListener2(this);
    }

    @Override
    public void onActionClick(final int action, final int position, final Article article) {
        if (action == OnActionClickListener.ACTION_LIKE) {
            SharedPreferences sp = this.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                if (article.getHasLiked()) {
                    // 文章已经被喜欢,所以此时执行取消喜欢
                    mEventLogic.deleteLike2(article.getArticleId(), String.valueOf(article.getBusinessType()), null);
                } else {
                    // 文章没有被喜欢,所以此时执行喜欢
                    mEventLogic.addLike(article.getArticleId(), String.valueOf(article.getBusinessType()));
                }
            } else {
                // 跳到登录页
                Intent intent = new Intent(this, RegisterAndLoginActivity.class);
                startActivity(intent);
            }
        } else if (action == OnActionClickListener.ACTION_COMMENT) {
            SharedPreferences sp = this.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                // 评论文章
                Intent intent = new Intent(this, FreshDetailsActivity.class);
                intent.putExtra("article_id", article.getArticleId());
                intent.putExtra("comment_flag", true);
                startActivity(intent);
            } else {
                // 跳到登录页
                Intent intent = new Intent(this, RegisterAndLoginActivity.class);
                startActivity(intent);
            }
        } else if (action == OnActionClickListener.ACTION_SHARE) {
            // 首先组合基本的分享单元
            String targetUrl = GlobalConstants.SHARE_ARTICLE + article.getArticleId();
            String shareContent = StringUtil.isEmpty(article.getContent()) ? "" : article.getContent();
            String imageUrl = null;
            if (article.getImgs() != null && article.getImgs().size() > 0) {
                imageUrl = article.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL);
            }

            YoungShareBoard shareBoard = new YoungShareBoard(this);
            shareBoard.setShareParams(article.getArticleId(), article.getBusinessType());
            shareBoard.setOnShareSuccessListener(this);
            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE);

            /**
             * 针对微博
             */
            mController.setShareContent(StringUtil.buildWeiboShareArticle(shareContent, targetUrl));
            mController.setShareMedia(new UMImage(this, imageUrl));
        }
    }

    @Override
    public void onActionClick2(final int action, final int position, final Article article) {
        if (action == OnActionClickListener2.ACTION_REPLY) {
            SharedPreferences sp = this.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) > 0) {
                // 评论文章
                Intent intent = new Intent(this, QuestionDetailsActivity.class);
                intent.putExtra("article_id", article.getArticleId());
                intent.putExtra("comment_flag", true);
                startActivity(intent);
            } else {
                // 跳到登录页
                Intent intent = new Intent(this, RegisterAndLoginActivity.class);
                startActivity(intent);
            }
        } else if (action == OnActionClickListener2.ACTION_SHARE) {
            // 首先组合基本的分享单元
            String targetUrl = GlobalConstants.SHARE_QUESTION + article.getArticleId();
            String shareContent = StringUtil.isEmpty(article.getContent()) ? "" : article.getContent();
            String imageUrl = null;
            if (article.getImgs() != null && article.getImgs().size() > 0) {
                imageUrl = article.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL);
            }

            YoungShareBoard shareBoard = new YoungShareBoard(this);
            shareBoard.setShareParams(article.getArticleId(), article.getBusinessType());
            shareBoard.setOnShareSuccessListener(this);
            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            setShareContent(mController, targetUrl, shareContent, imageUrl, BaseHttpFragmentActivity.SHARE_TYPE_QUESTION);

            /**
             * 针对微博
             */
            mController.setShareContent(StringUtil.buildWeiboShareQuestion(shareContent, targetUrl));
            mController.setShareMedia(new UMImage(this, imageUrl));
        }
    }

    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.add_follow:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String userId = msg.getData().getString("key");

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {
                            EventBus.getDefault().post(new FollowEvent(userId, FollowEvent.TYPE_ADD_FOLLOW));
                        }
                    }
                }
                break;
            case R.id.delete_follow:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String userId = msg.getData().getString("key");

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {
                            EventBus.getDefault().post(new FollowEvent(userId, FollowEvent.TYPE_DELETE_FOLLOW));
                        }
                    }
                }
                break;

            case R.id.delete_like2:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            for (int i = 0; i < articleList.size(); i++) {
                                if (articleId.equals(articleList.get(i).getArticleId())) {
                                    EventBus.getDefault().post(new ArticleLikeEvent(articleList.get(i).getArticleId(), ArticleLikeEvent.TYPE_DELETE_LIKE));
                                }
                            }
                        }
                    }
                }
                break;

            case R.id.add_like:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    String articleId = msg.getData().getString("key");

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        if (articleId != null) {
                            for (int i = 0; i < articleList.size(); i++) {
                                if (articleId.equals(articleList.get(i).getArticleId())) {
                                    EventBus.getDefault().post(new ArticleLikeEvent(articleList.get(i).getArticleId(), ArticleLikeEvent.TYPE_ADD_LIKE));
                                }
                            }
                        }

                        // 1
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }

                    }
                }
                break;

            case R.id.share_bonus:
                // 发送评论成功
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 3
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onFollowClick(String toId, int type) {
        if (type == OnFollowClickListener.TYPE_FOLLOW_ADD) {
            // 此时, 添加关注
            mEventLogic.addFollow(toId);
        } else if (type == OnFollowClickListener.TYPE_FOLLOW_DELETE) {
            // 此时, 取消关注
            mEventLogic.deleteFollow(toId);
        }
    }

    public void onEvent(ArticleLikeEvent articleLikeEvent) {
        if (articleLikeEvent.getType() == LikeEvent.TYPE_ADD_LIKE) {
            // 此时, 喜欢上
            for (int i=0; i < articleList.size(); i++) {
                if (articleList.get(i).getArticleId().equals(articleLikeEvent.getArticleId())) {
                    boolean hasLike = articleList.get(i).getHasLiked();
                    hasLike = !hasLike;
                    articleList.get(i).setHasLiked(hasLike);

                    int likeCount = articleList.get(i).getLikeCount();
                    likeCount++;
                    articleList.get(i).setLikeCount(likeCount);
                }
            }
        } else if (articleLikeEvent.getType() == LikeEvent.TYPE_DELETE_LIKE) {
            // 此时, 取消喜欢
            for (int i=0; i < articleList.size(); i++) {
                if (articleList.get(i).getArticleId().equals(articleLikeEvent.getArticleId())) {
                    boolean hasLike = articleList.get(i).getHasLiked();
                    hasLike = !hasLike;
                    articleList.get(i).setHasLiked(hasLike);

                    int likeCount = articleList.get(i).getLikeCount();
                    likeCount--;
                    articleList.get(i).setLikeCount(likeCount);
                }
            }
        }

        article2Adapter.notifyDataSetChanged();
    }

    public void onEvent(ArticleCommentEvent articleCommentEvent) {
        if (articleCommentEvent.getType() == CommentEvent.TYPE_DELETE_COMMENT) {
            for (int i=0; i < articleList.size(); i++) {
                if (articleList.get(i).getArticleId().equals(articleCommentEvent.getArticleId())) {
                    articleList.get(i).setCommentCount(articleList.get(i).getCommentCount() - articleCommentEvent.getCount());
                }
            }
        } else if (articleCommentEvent.getType() == CommentEvent.TYPE_ADD_COMMENT) {
            for (int i=0; i < articleList.size(); i++) {
                if (articleList.get(i).getArticleId().equals(articleCommentEvent.getArticleId())) {
                    articleList.get(i).setCommentCount(articleList.get(i).getCommentCount() + articleCommentEvent.getCount());
                }
            }
        }

        article2Adapter.notifyDataSetChanged();
    }

    public void onEvent(FollowEvent followEvent) {
        if (followEvent.getType() == FollowEvent.TYPE_ADD_FOLLOW) {
            // 此时, 添加关注
            for (int i=0; i < articleList.size(); i++) {
                if (articleList.get(i).getUserBase().getUserId().equals(followEvent.getUserId())) {
                    boolean hasFollowed = articleList.get(i).getUserBase().isHasFollowed();
                    hasFollowed = !hasFollowed;
                    articleList.get(i).getUserBase().setHasFollowed(hasFollowed);
                }
            }

            article2Adapter.notifyDataSetChanged();
        } else if (followEvent.getType() == FollowEvent.TYPE_DELETE_FOLLOW) {
            // 此时, 取消关注
            for (int i=0; i < articleList.size(); i++) {
                if (articleList.get(i).getUserBase().getUserId().equals(followEvent.getUserId())) {
                    boolean hasFollowed = articleList.get(i).getUserBase().isHasFollowed();
                    hasFollowed = !hasFollowed;
                    articleList.get(i).getUserBase().setHasFollowed(hasFollowed);
                }
            }

            article2Adapter.notifyDataSetChanged();
        }
    }

    public void onEvent(ArticleDeleteEvent articleDeleteEvent) {
        for (int i=0; i < articleList.size(); i++) {
            if (articleList.get(i).getUserBase().getUserId().equals(articleDeleteEvent.getArticleId())) {
                articleList.remove(i);
            }
        }
        article2Adapter.notifyDataSetChanged();
    }

    @Override
    public void onShareSuccess(String articleId, int shareType) {
        mEventLogic.shareBonus(articleId, shareType);
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
