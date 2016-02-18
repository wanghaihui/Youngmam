package com.xiaobukuaipao.youngmam.message;

/**
 * Created by xiaobu1 on 15-5-20.
 */
public class YoungMessage {
    // 对文章的评论
    public static final int MSG_TYPE_ARTICLE_COMMENT = 101;
    // 对文章评论的回复
    public static final int MSG_TYPE_ARTICLE_COMMENT_REPLY = 102;
    // 对问题的评论
    public static final int MSG_TYPE_QUESTION_COMMENT = 103;
    // 对问题评论的回复
    public static final int MSG_TYPE_QUESTION_COMMENT_REPLY = 104;
    // 专题评论被评论
    public static final int MSG_TYPE_SPECIAL_COMMENT_REPLY = 106;
    // H5页面评论被评论
    public static final int MSG_TYPE_H5_WEBPAGE_COMMENT_REPLY = 108;

    // 文章被喜欢
    public static final int MSG_TYPE_ARTICLE_LIKE = 201;
    // 文章评论被喜欢
    public static final int MSG_TYPE_ARTICLE_COMMENT_LIKE = 211;
    // 问题评论被喜欢
    public static final int MSG_TYPE_QUESTION_COMMENT_LIKE = 212;
    // 专题评论被喜欢
    public static final int MSG_TYPE_SPECIAL_COMMENT_LIKE = 213;
    // H5页面评论被喜欢
    public static final int MSG_TYPE_H5_WEBPAGE_COMMENT_LIKE = 214;

    // 新增粉丝消息
    public static final int MSG_TYPE_NEW_FANS_APPLY = 601;

    // 下面是系统消息
    // Notify只发通知,不放到消息列表中
    // 活动上线
    public static final int NOTIFY_TYPE_ACTIVITY_ACTIVE = 10001;
    // 新专题上线
    public static final int NOTIFY_TYPE_SPECIAL_PUBLISH = 10002;
    // Web活动上线
    public static final int NOTIFY_TYPE_WEB_ACTIVITY_ACTIVE = 10003;
    // Webpage专题上线
    public static final int NOTIFY_TYPE_WEBPAGE_PUBLISH = 10004;
    // Url专题上线
    public static final int NOTIFY_TYPE_URL_PUBLISH = 10005;
    // 话题上线
    public static final int NOTIFY_TYPE_THEME_PUBLISH = 10006;
    // 好文章推送
    public static final int NOTIFY_TYPE_ARTICLE_PUBLISH = 10007;
    // 重要系统消息
    public static final int NOTIFY_TYPE_COMMON_MSG = 10008;

    // 通知消息
    public static final int COMMON_MSG = 20001;

}
