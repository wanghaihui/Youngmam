package com.xiaobukuaipao.youngmam.http;

import android.net.Uri;

import com.android.volley.Request.Method;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class YoungEventLogic extends BaseEventLogic {

    public YoungEventLogic(Object subscriber) {
        super(subscriber);
    }

    /**
     * 手机号注册
     */
    public void phoneRegister(String mobile, String passwd, String vcode) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("passwd", passwd);
        params.put("vcode", vcode);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.phone_register, ApiConstants.PHONE_REGISTER,
                Method.POST, params, null, new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.phone_register);
    }

    /**
     * 手机号登录
     */
    public void login(String mobile, String passwd) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("passwd", passwd);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.login, ApiConstants.LOGIN,
                Method.POST, params, null, new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.login);
    }

    /**
     * 上传头像
     */
    public void uploadAvatar(String avatarPath) {
        HttpResultMultiPartRequest httpResultMultiPartRequest = new HttpResultMultiPartRequest(R.id.upload_avatar,
                ApiConstants.UPLOAD_AVATAR, Method.POST, new HttpResultParser(), this, null);
        httpResultMultiPartRequest.addFile("name", "avatar");
        httpResultMultiPartRequest.addFile("filename", avatarPath);
        sendRequest(httpResultMultiPartRequest, R.id.upload_avatar);
    }

    /**
     * 设置用户基本信息
     */
    public void setBasicInfo(String headUrl, String name, String childStatus) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("headUrl", headUrl);
        params.put("name", name);
        params.put("childStatus", childStatus);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.set_basic_info, ApiConstants.SET_BASIC_INFO,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);

        sendRequest(httpResultRequest, R.id.set_basic_info);
    }

    /**
     * 设置孩子基本信息
     */
    public void setBabyInfo(String childGender, String birthTime) {
        Map<String, String> params = new HashMap<String, String>();

        if (!StringUtil.isEmpty(childGender)) {
            params.put("childGender", childGender);
        }
        if (!StringUtil.isEmpty(birthTime)) {
            params.put("birthTime", birthTime);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.set_baby_info, ApiConstants.SET_BABY_INFO,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);

        sendRequest(httpResultRequest, R.id.set_baby_info);
    }

    /**
     * 重置密码
     */
    public void resetPswd(String mobile, String passwd, String vcode) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("passwd", passwd);
        params.put("vcode", vcode);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.reset_pswd, ApiConstants.RESET_PSWD,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.reset_pswd);
    }

    /**
     * 获取当前活动信息--此时,不传活动ID
     */
    public void getCurrentActivity() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_current_activity, ApiConstants.GET_CURRENT_ACTIVITY,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_current_activity);
    }

    /**
     * 获取当前Banner中的活动信息
     */
    public void getBannerActivities() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_banner_activities, ApiConstants.GET_BANNER_ACTIVITIES,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_banner_activities);
    }

    /**
     * 获取首页达人列表
     */
    public void getOnboardExpert() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_onboard_expert, ApiConstants.GET_ONBOARD_EXPERT,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_onboard_expert);
    }

    /**
     * 获取精选话题
     */
    public void getOnboardTheme() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_onboard_theme, ApiConstants.GET_ONBOARD_THEME,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_onboard_theme);
    }

    /**
     * 获取参与活动的相关文章
     */
    public void getActivityArticle(String activityId, String minArticleId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_activity_article, ApiConstants.GET_ACTIVITY_ARTICLE
                + "?activityId=" + activityId + "&minArticleId=" + minArticleId,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_activity_article);
    }

    /**
     * 获取专题摘要信息
     */
    public void getTopicSummary(String topicIds) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_topic_summary, ApiConstants.GET_TOPIC_SUMMARY
                + (topicIds == null ? "" : ("?topicIds=" + topicIds)),
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_topic_summary);
    }

    /**
     * 获取专题列表
     */
    public void getTopicList(long minId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_topic_list, ApiConstants.GET_TOPIC_LIST
                + "?minId=" + minId
                + (limit > 0 ? ("&limit=" + limit) : ""),
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_topic_list);
    }

    /**
     * 获得专题详细信息
     */
    public void getTopic(String topicId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_topic, ApiConstants.GET_TOPIC
                + "?topicId=" + topicId,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_topic);
    }

    /**
     * 得到所有的专题
     */
    public void getAllTopic(String minTopicId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_all_topic, ApiConstants.GET_ALL_TOPIC
                + "?minTopicId=" + minTopicId,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_all_topic);
    }

    /**
     * 获取编辑标签摘要信息
     */
    public void getEditorChoiceTag(String editorTagIds) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_editor_choice_tag, ApiConstants.GET_EDITOR_CHOICE_TAG
                + (editorTagIds == null ? "" : ("?editorTagIds=" + editorTagIds)),
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_editor_choice_tag);
    }

    /**
     * 获取编辑标签下的文章
     */
    public void getEditorChoiceArticle(String editorTagId, String minArticleId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_editor_choice_article, ApiConstants.GET_EDITOR_CHOICE_ARTICLE
                + "?editorTagId=" + editorTagId + "&minArticleId=" + minArticleId,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_editor_choice_article);
    }

    /**
     * 获取热门精选文章列表
     */
    public void getHotArticle(String minHotId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_hot_article, ApiConstants.GET_HOT_ARTICLE
                + "?minHotId=" + minHotId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_hot_article);
    }

    /**
     * 获取全部文章
     */
    public void getAllArticle(String minArticleId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_all_article, ApiConstants.GET_ALL_ARTICLE
                + "?minArticleId=" + minArticleId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_all_article);
    }

    /**
     * 获取文章详情
     */
    public void getArticle(String articleId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_article, ApiConstants.GET_ARTICLE
                + "?articleId=" + articleId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_article);
    }

    /**
     * 获取喜欢这篇文章的人的列表
     */
    public void getArticleLike(String articleId, String minLikeId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_article_like, ApiConstants.GET_ARTICLE_LIKE
                + "?articleId=" + articleId + "&minLikeId=" + minLikeId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_article_like);
    }

    /**
     * 获取文章的评论列表
     */
    public void getComment(String articleId, String businessType, String minCommentId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_comment, ApiConstants.GET_COMMENT
                + "?articleId=" + articleId + "&businessType=" + businessType + "&minCommentId=" + minCommentId + (limit > 0 ? ("&limit=" + limit) : ""),
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_comment);
    }

    /**
     * 上传文章图片
     */
    @Deprecated
    public void uploadArticleImage(String articleImagePath) {
        HttpResultMultiPartRequest httpResultMultiPartRequest = new HttpResultMultiPartRequest(R.id.upload_article_path,
                ApiConstants.UPLOAD_ARTICLE_PATH, Method.POST, new HttpResultParser(), this, null);
        httpResultMultiPartRequest.addFile("name", "image");
        httpResultMultiPartRequest.addFile("filename", articleImagePath);
        sendRequest(httpResultMultiPartRequest, R.id.upload_article_path);
    }

    /**
     * 图片上传统一接口
     */
    public void uploadImage(String image, int isSaveOriginalImg, String desc) {
        HttpResultMultiPartRequest httpResultMultiPartRequest = new HttpResultMultiPartRequest(R.id.upload_image,
                ApiConstants.UPLOAD_IMAGE, Method.POST, new HttpResultParser(), this, null);
        httpResultMultiPartRequest.addFile("name", "image");
        httpResultMultiPartRequest.addFile("filename", image);
        httpResultMultiPartRequest.addMultipartParam("isSaveOriginalImg", "multipart/form-data", String.valueOf(isSaveOriginalImg));
        httpResultMultiPartRequest.addMultipartParam("desc", "multipart/form-data", desc);
        sendRequest(httpResultMultiPartRequest, R.id.upload_image);
    }

    /**
     * 得到热门标签
     */
    public void getHotTag(int businessType, String tag) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_hot_tag, ApiConstants.GET_HOT_TAG
                + "?businessType=" + businessType
                + (StringUtil.isEmpty(tag) ? "" : "&tag=" + tag ) ,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_hot_tag);
    }

    public void getRecommendTag() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_recommend_tag, ApiConstants.GET_HOT_TAG,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_recommend_tag);
    }

    /**
     * 保存文章
     */
    public void saveArticle(String content, String imgIds, String tagIds) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("content", content);
        params.put("imgIds", imgIds);
        params.put("tagIds", tagIds);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.save_article, ApiConstants.SAVE_ARTICLE,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.save_article);
    }

    /**
     * 发布问题
     */
    public void createQuestion(String content, String imgIds, String tagIds) {
        Map<String, String> params = new HashMap<String, String>();
        if (!StringUtil.isEmpty(content)) {
            params.put("content", content);
        }
        if (!StringUtil.isEmpty(imgIds)) {
            params.put("imgIds", imgIds);
        }
        if (!StringUtil.isEmpty(tagIds)) {
            params.put("tagIds", tagIds);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.create_question, ApiConstants.CREATE_QUESTION,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.create_question);
    }

    /**
     * 搜索标签
     */
    public void getSearchTag(String tag, int businessType) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_search_tag, ApiConstants.GET_SEARCH_TAG
                + "?tag=" + Uri.encode(tag) + "&businessType=" + businessType, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_search_tag);
    }

    /**
     * 获取用户基本信息
     */
    public void getUserBase(String pageOwner) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_user_base, ApiConstants.GET_USER_BASE
                + "?pageOwner=" + pageOwner,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_user_base);
    }

    /**
     * 获取用户孩子的基本信息
     */
    public void getUserChild() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_user_child, ApiConstants.GET_USER_CHILD,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_user_child);
    }

    /**
     * 获取用户自己发布的文章列表
     */
    public void getPublishedArticle(String minArticleId, String userId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_publish_article, ApiConstants.GET_PUBLISH_ARTICLE
                + "?minArticleId=" + minArticleId
                + (StringUtil.isEmpty(userId) ? "" : ("&userId=" + userId)),
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_publish_article);
    }

    /**
     * 取消喜欢某篇文章
     */
    public void deleteLikeArticle(String articleId, String likeId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        if (likeId != null) {
            params.put("likeId", likeId);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.delete_like_article, ApiConstants.DELETE_LIKE_ARTICLE,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.delete_like_article);
    }

    /**
     * 喜欢某篇文章
     */
    public void addLikeArticle(String articleId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.add_like_article, ApiConstants.ADD_LIKE_ARTICLE,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.add_like_article);
    }

    /**
     * 删除文章
     */
    public void deleteArticle(String articleId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.delete_article, ApiConstants.DELETE_ARTICLE,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.delete_article);
    }

    /**
     * 删除问题
     */
    public void deleteQuestion(String articleId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.delete_question, ApiConstants.DELETE_QUESTION,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.delete_question);
    }

    /**
     * 获取用户发布的文章,喜欢和评论数
     */
    public void getUserStatis(String userId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_user_statis, ApiConstants.GET_USER_STATIS
                + "?userId=" + userId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_user_statis);
    }

    /**
     * 获得自己喜欢过的文章
     */
    public void getBylikeArticle(String minLikeId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_bylike_article, ApiConstants.GET_BYLIKE_ARTICLE
                + "?minLikeId=" + minLikeId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_bylike_article);
    }

    /**
     * 获取用户自己发表的评论
     */
    public void getMycommentArticle(String minCommentId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_mycomment_article, ApiConstants.GET_MYCOMMENT_ARTICLE
                + "?minCommentId=" + minCommentId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_mycomment_article);
    }

    /**
     * 添加评论
     */
    public void addArticleComment(String articleId, String content, String originCommentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("content", content);
        if (originCommentId != null) {
            params.put("originCommentId", originCommentId);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.add_article_comment, ApiConstants.ADD_ARTICLE_COMMENT,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.add_article_comment);
    }

    /**
     * 统一的添加评论接口
     */
    public void addComment(String articleId, String businessType, String content, String imgIds, String originCommentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("businessType", businessType);
        if (content != null) {
            params.put("content", content);
        }
        if (imgIds != null) {
            params.put("imgIds", imgIds);
        }
        if (originCommentId != null) {
            params.put("originCommentId", originCommentId);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.add_comment, ApiConstants.ADD_COMMENT,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.add_comment);
    }

    /**
     * 获得消息列表
     */
    public void getMessage(String minMsgId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_message, ApiConstants.GET_MESSAGE
                + "?minMsgId=" + minMsgId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_message);
    }

    /**
     * 设置用户的设备Token
     */
    public void setPushToken(String identifier, String token, String baiduUserId, String baiduChannelId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("identifier", identifier);
        if (token != null) {
            params.put("token", token);
        }
        params.put("baiduUserId", baiduUserId);
        params.put("baiduChannelId", baiduChannelId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.set_push_token, ApiConstants.SET_PUSH_TOKEN,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.set_push_token);
    }

    /**
     * 删除评论
     */
    public void deleteComment(String articleId, String commentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("commentId", commentId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.delete_comment, ApiConstants.DELETE_COMMENT,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, commentId);
        sendRequest(httpResultRequest, R.id.delete_comment);
    }

    /**
     * 获取未读消息数
     */
    public void getUnreadCount() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_unread_count, ApiConstants.GET_UNREAD_COUNT,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_unread_count);
    }

    /**
     * 按用户查看他所发布的文章列表
     */
    public void getOtherArticle(String minArticleId, String userId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_publish_article, ApiConstants.GET_USER_ARTICLE
                + "?minArticleId=" + minArticleId + "&userId=" + userId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_publish_article);
    }

    /**
     * 按用户查看他所喜欢的文章列表
     */
    public void getUserlikeArticle(String userId, String minLikeId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_user_like_article, ApiConstants.GET_USER_LIKE_ARTICLE
                + "?userId=" + userId + "&minLikeId=" + minLikeId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_user_like_article);
    }

    /**
     * 搜索文章
     */
    public void searchArticle(String wd, String start) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_search_article, ApiConstants.GET_SEARCH_ARTICLE
                + "?wd=" + Uri.encode(wd) + "&start=" + start, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_search_article);
    }

    /**
     * 根据Tag搜索文章
     */
    public void searchArticleByTag(String tagId, String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_search_article_by_tag, ApiConstants.GET_SEARCH_ARTICLE_BY_TAG
                + "?tagId=" + tagId + "&minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_search_article_by_tag);
    }

    /**
     * 根据Tag搜索问答
     */
    public void searchQuestionByTag(String tagId, String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_search_question_by_tag, ApiConstants.GET_SEARCH_QUESTION_BY_TAG
                + "?tagId=" + tagId + "&minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_search_question_by_tag);
    }

    /**
     * 小登录--更新T票
     */
    public void tlogin() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.tlogin, ApiConstants.TLOGIN,
                Method.POST, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.tlogin);
    }

    /**
     * 获取短信验证码
     */
    public void sendVcode(String mobile) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.send_vcode, ApiConstants.SEND_VCODE,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.send_vcode);
    }

    /**
     * 退出登录
     */
    public void logout() {
        Map<String, String> params = new HashMap<String, String>();

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.passport_logout, ApiConstants.LOGOUT,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.passport_logout);
    }

    /**
     * 微信登录
     */
    public void weixinLogin(String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.weixin_login, ApiConstants.WEIXIN_LOGIN,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.weixin_login);
    }

    public void weixinLogin2(String openId, String unionId, String nickName, String headUrl) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("openId", openId);
        params.put("unionId", unionId);
        params.put("nickName", nickName);
        params.put("headUrl", headUrl);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.weixin_login2, ApiConstants.WEIXIN_LOGIN2,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.weixin_login2);
    }

    /**
     * 微博登录
     */
    public void weiboLogin(String uid, String accessToken) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", uid);
        params.put("accessToken", accessToken);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.weibo_login, ApiConstants.WEIBO_LOGIN,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.weibo_login);
    }

    /**
     * QQ登录
     */
    public void qqLogin(String openId, String nickName, String headUrl) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("openId", openId);
        params.put("nickName", nickName);
        params.put("headUrl", headUrl);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.qq_login, ApiConstants.QQ_LOGIN,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.qq_login);
    }

    /**
     * 发送意见反馈
     */
    public void sendFeedback(String contact, String content) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("contact", contact);
        params.put("content", content);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.add_feedback, ApiConstants.ADD_FEEDBACK,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.add_feedback);
    }

    /**
     * 得到全部的商品礼物列表
     */
    public void getAllGift(String minItemId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_all_gift, ApiConstants.GET_ALL_GIFT
                + "?minItemId=" + minItemId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_all_gift);
    }

    /**
     * 获得用户的总积分
     */
    public void getAllCredit() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_all_credit, ApiConstants.GET_ALL_CREDIT,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_all_credit);
    }

    /**
     * 得到全部话题
     */
    public void getAllTheme(String minLastArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_all_theme, ApiConstants.GET_ALL_THEME
                + "?minLastArticleId=" + minLastArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_all_theme);
    }

    /**
     * 获取商品详情
     */
    public void getBonusDetail(String itemId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_bonus_detail, ApiConstants.GET_BONUS_DETAIL
                + "?itemId=" + itemId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_bonus_detail);
    }

    /**
     * 获取全部达人列表
     */
    public void getAllExpert(String minLastArticleId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_all_expert, ApiConstants.GET_ALL_EXPERT
                + "?minLastArticleId=" + minLastArticleId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_all_expert);
    }

    /**
     * 获取热门标签--第一版
     */
    public void getHotTags() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_hot_tags, ApiConstants.GET_HOT_TAGS
                , Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_hot_tags);
    }

    /**
     * 获取用户的收件地址
     */
    public void getGiftAddress(String txId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_gift_address, ApiConstants.GET_GIFT_ADDRESS
                + (StringUtil.isEmpty(txId) ? "" : ("?txId=" + txId)), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_gift_address);
    }

    /**
     * 设置用户的收件地址
     */
    public void setGiftAddress(String txId, String name, String mobile, String addr) {
        Map<String, String> params = new HashMap<String, String>();
        if (!StringUtil.isEmpty(txId)) {
            params.put("txId", txId);
        }
        params.put("name", name);
        params.put("mobile", mobile);
        params.put("addr", addr);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.set_gift_address, ApiConstants.SET_GIFT_ADDRESS,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.set_gift_address);
    }

    /**
     * 获取用户的我的积分
     */
    public void getPointDetail(String deviceType, String appVersion) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_point_detail, ApiConstants.GET_POINT_DETAIL
                + "?deviceType=" + deviceType + "&appVersion=" + appVersion, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_point_detail);
    }

    /**
     * 获取新专题
     */
    public void getSpecialTopic(String specialId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_special_topic, ApiConstants.GET_SPECIAL_TOPIC
                + "?specialId=" + specialId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_special_topic);
    }

    /**
     * 进行礼品兑换
     */
    public void exchangeGift(String itemId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("itemId", itemId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.exchange_gift, ApiConstants.EXCHANGE_GIFT,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.exchange_gift);
    }

    /**
     * 获取我的礼品列表
     */
    public void getMineGift(String minTxId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_mine_gift, ApiConstants.GET_MINE_GIFT
                + "?minTxId=" + minTxId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_mine_gift);
    }

    /**
     * 分享文章拿积分
     */
    public void shareBonus(String articleId, int businessType) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("businessType", String.valueOf(businessType));

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.share_bonus, ApiConstants.SHARE_BONUS,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.share_bonus);
    }

    /**
     * 评价得积分
     */
    public void appPraiseBonus(String deviceType, String appVersion) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("deviceType", deviceType);
        params.put("appVersion", appVersion);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.app_praise_bonus, ApiConstants.APP_PRAISE_BONUS,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.app_praise_bonus);
    }

    /**
     * 用户签到
     */
    public void checkIn() {
        Map<String, String> params = new HashMap<String, String>();

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.check_in, ApiConstants.CHECK_IN,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.check_in);
    }

    /**
     * 获取问题列表接口, 按时间倒序
     */
    public void getAllQuestions(String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_all_questions, ApiConstants.GET_ALL_QUESTIONS
                + "?minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_all_questions);
    }

    /**
     * 水印取分组
     */
    public void getWatermarkGroup(int type) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_watermark_group, ApiConstants.GET_STICKER_GROUP
                + "?type=" + type,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_watermark_group);
    }

    /**
     * 贴纸取分组
     */
    public void getStickerGroup(int type) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_sticker_group, ApiConstants.GET_STICKER_GROUP
                + "?type=" + type,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_sticker_group);
    }

    /**
     * 按Group取水印
     */
    public void getWatermarkById(String groupId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_watermark_by_id, ApiConstants.GET_STICKER_BY_ID
                + "?groupId=" + groupId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_watermark_by_id);
    }

    /**
     * 按Group取贴纸
     */
    public void getStickerById(String groupId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_sticker_by_id, ApiConstants.GET_STICKER_BY_ID
                + "?groupId=" + groupId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_sticker_by_id);
    }

    /**
     * 获取最新问题的摘要列表
     */
    public void getNewQuestions(String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_new_questions, ApiConstants.GET_NEW_QUESTIONS
                + "?minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_new_questions);
    }

    /**
     * 获取精选问题的摘要列表
     */
    public void getBestQuestions(String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_best_questions, ApiConstants.GET_BEST_QUESTIONS
                + "?minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_best_questions);
    }

    /**
     * 获取某人发布过的问题列表
     */
    public void getUserQuestions(String userId, String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_user_questions, ApiConstants.GET_USER_QUESTIONS
                + "?userId=" + userId
                + (StringUtil.isEmpty(minArticleId) ? "" : ("&minArticleId=" + minArticleId))
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_user_questions);
    }

    /**
     * 获取首页的编辑精选
     */
    public void getBestFeed(String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_best_feed, ApiConstants.GET_BEST_FEED
                + "?minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_best_feed);
    }

    /**
     * 得到关注页的推荐列表
     */
    public void getRecommendFeed(String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_recommend_feed, ApiConstants.GET_RECOMMEND_FEED
                + "?minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_recommend_feed);
    }

    /**
     * 统一的点赞接口, 点赞文章, 专题, web页面, 问答
     */
    public void addLike(String articleId, String businessType) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("businessType", businessType);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.add_like, ApiConstants.ADD_LIKE,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.add_like);
    }

    /**
     * 删除Like
     */
    public void deleteLike2(String articleId, String businessType, String commentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("businessType", businessType);
        if (commentId != null) {
            params.put("commentId", commentId);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.delete_like2, ApiConstants.DELETE_LIKE_2,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, articleId);
        sendRequest(httpResultRequest, R.id.delete_like2);
    }

    /**
     * 得到专题列表的分类
     */
    public void getTopicCategory() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_topic_category, ApiConstants.GET_TOPIC_CATEGORY,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_topic_category);
    }

    /**
     * 按category取专题列表
     */
    public void getTopicByCategory(String category, String minId, int limit) {
            HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_topic_by_category, ApiConstants.GET_TOPIC_BY_CATEGORY
                    + "?category=" + category
                    + "&minId=" + minId
                    + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
            sendRequest(httpResultRequest, R.id.get_topic_by_category);
    }

    /**
     * 点赞回复
     */
    public void addLikeComment(String articleId, String businessType, String commentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("businessType", businessType);
        if (commentId != null) {
            params.put("commentId", commentId);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.add_like_comment, ApiConstants.ADD_LIKE_COMMENT,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, commentId);
        sendRequest(httpResultRequest, R.id.add_like_comment);
    }

    /**
     * 删除点赞回复
     */
    public void deleteLikeComment(String articleId, String businessType, String commentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        params.put("businessType", businessType);
        if (commentId != null) {
            params.put("commentId", commentId);
        }

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.delete_like_comment, ApiConstants.DELETE_LIKE_COMMENT,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, commentId);
        sendRequest(httpResultRequest, R.id.delete_like_comment);
    }

    /**
     * 首页的两个推荐话题
     * @param limit
     */
    public void getRecommendTheme(int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_recommend_theme, ApiConstants.GET_RECOMMEND_THEME
                + "?limit=" + limit, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_recommend_theme);
    }

    /**
     * 得到顶部Banner广告位
     */
    public void getOnboardBanner(int area) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_onboard_banner, ApiConstants.GET_ONBOARD_BANNER
                + "?area=" + area, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_onboard_banner);
    }

    /**
     * 用于获取推荐达人
     */
    public void getRecommendExpert(int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_recommend_expert, ApiConstants.GET_RECOMMEND_EXPERT
                + "?limit=" + limit, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_recommend_expert);
    }

    /**
     * 取好友的Feed列表
     */
    public void getFriendFeed(String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_friend_feed, ApiConstants.GET_FRIEND_FEED
                + "?minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_friend_feed);
    }

    /**
     * 获取发现页的顶部分类
     */
    public void getFeedCategory() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_feed_category, ApiConstants.GET_FEED_CATEGORY,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_feed_category);
    }

    /**
     * 发现页的分类列表
     */
    public void getFeedByCategory(String category, String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_feed_by_category, ApiConstants.GET_FEED_BY_CATEGORY
                + "?category=" + category
                + "&minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_feed_by_category);
    }

    /**
     * 按TagName取问答列表接口
     */
    public void getQuestionByTagName(String tagName, String minArticleId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_question_by_tagname, ApiConstants.GET_QUESTION_BY_TAGNAME
                + "?tagName=" + Uri.encode(tagName)
                + "&minArticleId=" + minArticleId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_question_by_tagname);
    }

    /**
     * 取问答详情
     */
    public void getQuestionDetail(String articleId) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_question_detail, ApiConstants.GET_QUESTION_DETAIL
                + "?articleId=" + articleId, Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_question_detail);
    }

    /**
     * 加关注
     */
    public void addFollow(String toId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("toId", toId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.add_follow, ApiConstants.ADD_FOLLOW,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, toId);
        sendRequest(httpResultRequest, R.id.add_follow);
    }

    /**
     * 取消关注
     */
    public void deleteFollow(String toId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("toId", toId);

        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.delete_follow, ApiConstants.DELETE_FOLLOW,
                Method.POST, params, new HashMap<String, String>(), new HttpResultParser(), this, toId);
        sendRequest(httpResultRequest, R.id.delete_follow);
    }

    /**
     * 取被关注者列表
     */
    public void getFollowing(String host, String minUserId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_following, ApiConstants.GET_FOLLOWING
                + "?host=" + host
                + "&minUserId=" + minUserId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_following);
    }

    /**
     * 取粉丝列表
     */
    public void getFollowers(String host, String minUserId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_following, ApiConstants.GET_FOLLOWERS
                + "?host=" + host
                + "&minUserId=" + minUserId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_following);
    }

    /**
     * 取消息列表
     */
    public void getMessageByType(int groupType, String minMsgId, int limit) {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.get_message_by_type, ApiConstants.GET_MESSAGE_BY_TYPE
                + "?groupType=" + groupType
                + "&minMsgId=" + minMsgId
                + (limit > 0 ? ("&limit=" + limit) : ""), Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.get_message_by_type);
    }

    /**
     * 消息全部已读
     */
    public void readAllMessage() {
        HttpResultRequest httpResultRequest = new HttpResultRequest(R.id.read_all_message, ApiConstants.READ_ALL_MESSAGE,
                Method.GET, null, new HashMap<String, String>(), new HttpResultParser(), this, null);
        sendRequest(httpResultRequest, R.id.read_all_message);
    }
}
