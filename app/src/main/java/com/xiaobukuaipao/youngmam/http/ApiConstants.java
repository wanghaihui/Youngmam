package com.xiaobukuaipao.youngmam.http;

/**
 * Created by xiaobu1 on 15-5-11.
 */
public final class ApiConstants {

    public static final boolean DEBUG = false;

    public static final String PREFIX = "http://";
    public static final String DOMAIN = DEBUG ? "192.168.1.107" : "api.youngmam.com";
    public static final int PORT = DEBUG ? 8101 : 8888;

    public static final String SERVER_ADDRESS = PREFIX + DOMAIN + ":" + PORT + "/";

    /**
     * 手机注册
     */
    public static final String PHONE_REGISTER = SERVER_ADDRESS + "passport/1/register";

    /**
     * 手机号登录
     */
    public static final String LOGIN = SERVER_ADDRESS + "passport/1/login";

    /**
     * 上传头像
     */
    public static final String UPLOAD_AVATAR = SERVER_ADDRESS + "user/1/avatar/upload";

    /**
     * 基本信息
     */
    public static final String SET_BASIC_INFO = SERVER_ADDRESS + "user/1/base/set";

    /**
     * 孩子基本信息
     */
    public static final String SET_BABY_INFO = SERVER_ADDRESS + "user/1/child/set";

    /**
     * 重置密码
     */
    public static final String RESET_PSWD = SERVER_ADDRESS + "passport/1/passwd/set";

    /**
     * 获得当前活动
     */
    public static final String GET_CURRENT_ACTIVITY = SERVER_ADDRESS + "activity/1/get";

    /**
     * 得到当前Banner中的活动
     */
    public static final String GET_BANNER_ACTIVITIES = SERVER_ADDRESS + "banner/1/onboard/get";

    /**
     * 获取首页达人列表
     */
    public static final String GET_ONBOARD_EXPERT = SERVER_ADDRESS + "user/1/expert/onboard/get";

    /**
     * 获取精选话题
     */
    public static final String GET_ONBOARD_THEME = SERVER_ADDRESS + "theme/1/onboard/get";

    /**
     * 获取参与活动的文章
     */
    public static final String GET_ACTIVITY_ARTICLE = SERVER_ADDRESS + "activity/1/article/get";

    /**
     * 获取专题摘要信息
     */
    public static final String GET_TOPIC_SUMMARY = SERVER_ADDRESS + "topic/1/summary/get";

    /**
     * 获取专题列表
     */
    public static final String GET_TOPIC_LIST = SERVER_ADDRESS + "guide/1/get_list";

    /**
     * 获得专题详细信息
     */
    public static final String GET_TOPIC = SERVER_ADDRESS + "topic/1/get";

    /**
     * 得到所有的专题
     */
    public static final String GET_ALL_TOPIC = SERVER_ADDRESS + "topic/1/all/get";

    /**
     * 获取编辑标签摘要信息
     */
    public static final String GET_EDITOR_CHOICE_TAG = SERVER_ADDRESS + "editor-choice/1/tag/get";

    /**
     * 获取编辑标签下的文章
     */
    public static final String GET_EDITOR_CHOICE_ARTICLE = SERVER_ADDRESS + "editor-choice/1/article/get";

    /**
     * 获取热门精选文章列表
     */
    public static final String GET_HOT_ARTICLE = SERVER_ADDRESS + "article/1/hot/get";

    /**
     * 获取全部文章列表
     */
    public static final String GET_ALL_ARTICLE = SERVER_ADDRESS + "article/1/all/get";

    /**
     * 获取文章详情
     */
    public static final String GET_ARTICLE = SERVER_ADDRESS + "article/1/get";

    /**
     * 获取喜欢文章的人
     */
    public static final String GET_ARTICLE_LIKE = SERVER_ADDRESS + "article/1/like/get";

    /**
     * 获取文章的评论列表
     */
    public static final String GET_COMMENT = SERVER_ADDRESS + "comment/1/getlist";

    /**
     * 上传文章图片
     */
    public static final String UPLOAD_ARTICLE_PATH = SERVER_ADDRESS + "article/1/image/upload";

    /**
     * 图片上传统一接口
     */
    public static final String UPLOAD_IMAGE = SERVER_ADDRESS + "upload/1/image/upload";

    /**
     * 获得热门标签
     */
    public static final String GET_HOT_TAG = SERVER_ADDRESS + "dict/1/tag/suggest/get";

    /**
     * 获得搜索的标签
     */
    public static final String GET_SEARCH_TAG = SERVER_ADDRESS + "dict/1/tag/suggest/get";

    /**
     * 保存文章
     */
    public static final String SAVE_ARTICLE = SERVER_ADDRESS + "article/1/save";

    /**
     * 创建问题
     */
    public static final String CREATE_QUESTION = SERVER_ADDRESS + "question/1/create";

    /**
     * 获取用户基本信息
     */
    public static final String GET_USER_BASE = SERVER_ADDRESS + "user/1/userpage/get";

    /**
     * 获取用户孩子的基本信息
     */
    public static final String GET_USER_CHILD = SERVER_ADDRESS + "user/1/child/get";

    /**
     * 获取用户自己发布的文章列表
     */
    public static final String GET_PUBLISH_ARTICLE = SERVER_ADDRESS + "article/1/mine/get";

    /**
     * 取消喜欢文章
     */
    public static final String DELETE_LIKE_ARTICLE = SERVER_ADDRESS + "article/1/like/delete";

    /**
     * 喜欢某篇文章
     */
    public static final String ADD_LIKE_ARTICLE = SERVER_ADDRESS + "article/1/like/add";

    /**
     * 删除文章
     */
    public static final String DELETE_ARTICLE = SERVER_ADDRESS + "article/1/delete";

    /**
     * 删除问题
     */
    public static final String DELETE_QUESTION = SERVER_ADDRESS + "question/1/delete";

    /**
     * 获取用户发布的文章,喜欢和评论数
     */
    public static final String GET_USER_STATIS = SERVER_ADDRESS + "user/1/statis/get";

    /**
     * 获取自己喜欢过的文章
     */
    public static final String GET_BYLIKE_ARTICLE = SERVER_ADDRESS + "article/1/bylike/get";

    /**
     * 获取用户自己发表的评论
     */
    public static final String GET_MYCOMMENT_ARTICLE = SERVER_ADDRESS + "article/1/mycomment/get";

    /**
     * 添加评论
     */
    public static final String ADD_ARTICLE_COMMENT = SERVER_ADDRESS + "article/1/comment/add";

    /**
     * 添加评论--统一接口
     */
    public static final String ADD_COMMENT = SERVER_ADDRESS + "comment/1/add";

    /**
     * 获得消息列表
     */
    public static final String GET_MESSAGE = SERVER_ADDRESS + "message/1/get";

    /**
     * 设置用户设备token
     */
    public static final String SET_PUSH_TOKEN = SERVER_ADDRESS + "message/1/push-token/set";

    /**
     * 删除评论
     */
    public static final String DELETE_COMMENT = SERVER_ADDRESS + "comment/1/delete";

    /**
     * 获取未读消息数
     */
    public static final String GET_UNREAD_COUNT = SERVER_ADDRESS + "message/1/new-msg-counts/get";

    /**
     * 得到他人发布的文章列表
     */
    public static final String GET_USER_ARTICLE = SERVER_ADDRESS + "article/1/byuser/get";

    /**
     * 得到他人喜欢的文章列表
     */
    public static final String GET_USER_LIKE_ARTICLE = SERVER_ADDRESS + "article/1/byuserlike/get";

    /**
     * 搜索文章
     */
    public static final String GET_SEARCH_ARTICLE = SERVER_ADDRESS + "article/1/search/get";

    /**
     * 根据Tag搜索文章
     */
    public static final String GET_SEARCH_ARTICLE_BY_TAG = SERVER_ADDRESS + "article/2/bytag/get";

    /**
     * 根据Tag搜索问答
     */
    public static final String GET_SEARCH_QUESTION_BY_TAG = SERVER_ADDRESS + "question/1/bytag/get";

    /**
     * 小登录--更新T票
     */
    public static final String TLOGIN = SERVER_ADDRESS + "passport/1/tlogin";

    /**
     * 获取短信验证码
     */
    public static final String SEND_VCODE = SERVER_ADDRESS + "passport/1/vcode/send";

    /**
     * 退出登录
     */
    public static final String LOGOUT = SERVER_ADDRESS + "passport/1/logout";

    /**
     * 微信登录
     */
    public static final String WEIXIN_LOGIN = SERVER_ADDRESS + "passport/1/weixin/login";

    /**
     * 微信登录2
     */
    public static final String WEIXIN_LOGIN2 = SERVER_ADDRESS + "passport/1/weixin/login2";

    /**
     * 微博登录
     */
    public static final String WEIBO_LOGIN = SERVER_ADDRESS + "passport/1/weibo/login";

    /**
     * QQ登录
     */
    public static final String QQ_LOGIN = SERVER_ADDRESS + "passport/1/qq/login";

    /**
     * 用户反馈
     */
    public static final String ADD_FEEDBACK = SERVER_ADDRESS + "user/1/feedback/add";

    /**
     * 获取全部商品列表
     */
    public static final String GET_ALL_GIFT = SERVER_ADDRESS + "bonus/1/item/all/get";

    /**
     * 获得用户的总积分
     */
    public static final String GET_ALL_CREDIT = SERVER_ADDRESS + "bonus/1/user/point/get";

    /**
     * 获取全部话题
     */
    public static final String GET_ALL_THEME = SERVER_ADDRESS + "theme/1/all/get";

    /**
     * 获得商品详情
     */
    public static final String GET_BONUS_DETAIL = SERVER_ADDRESS + "bonus/1/item/get";

    /**
     * 获取全部达人列表
     */
    public static final String GET_ALL_EXPERT = SERVER_ADDRESS + "user/1/expert/all/get";

    /**
     * 获取热门标签
     */
    public static final String GET_HOT_TAGS = SERVER_ADDRESS + "dict/1/tag/hot/get";

    /**
     * 获取用户的收件地址
     */
    public static final String GET_GIFT_ADDRESS = SERVER_ADDRESS + "user/1/deliver-addr/get";

    /**
     * 设置用户的收件地址
     */
    public static final String SET_GIFT_ADDRESS = SERVER_ADDRESS + "user/1/deliver-addr/set";

    /**
     * 获取用户的我的积分
     */
    public static final String GET_POINT_DETAIL = SERVER_ADDRESS + "bonus/1/user/point-detail/get";

    /**
     * 获取新专题
     */
    public static final String GET_SPECIAL_TOPIC = SERVER_ADDRESS + "special/1/get";

    /**
     * 兑换礼品
     */
    public static final String EXCHANGE_GIFT = SERVER_ADDRESS + "bonus/1/tx/add";

    /**
     * 我的礼品列表
     */
    public static final String GET_MINE_GIFT = SERVER_ADDRESS + "bonus/1/tx/get_my_tx_list";

    /**
     * 分享文章拿积分
     */
    public static final String SHARE_ARTICLE_BONUS = SERVER_ADDRESS + "bonus/1/share-article/add";

    /**
     * 分享特殊专题得积分
     */
    public static final String SHARE_SPECIAL_TOPIC_BONUS = SERVER_ADDRESS + "bonus/1/share-special/add";

    /**
     * 分享Webpage得积分
     */
    public static final String SHARE_WEBPAGE_BONUS = SERVER_ADDRESS + "bonus/1/share-webpage/add";

    /**
     * 分享活动得积分
     */
    public static final String SHARE_LATEST_ACTIVITY_BONUS = SERVER_ADDRESS + "bonus/1/share-activity/add";

    /**
     * 分享Tag得积分
     */
    public static final String SHARE_TAG_BONUS = SERVER_ADDRESS + "bonus/1/share-tag/add";

    /**
     * 评价拿积分
     */
    public static final String APP_PRAISE_BONUS = SERVER_ADDRESS + "bonus/1/app-praise/add";

    /**
     * 用户签到
     */
    public static final String CHECK_IN = SERVER_ADDRESS + "user/1/checkin";

    /**
     * 获取问题列表接口, 按时间倒序
     */
    public static final String GET_ALL_QUESTIONS = SERVER_ADDRESS + "question/1/all/get";

    /**
     * 贴纸水印取分组
     */
    public static final String GET_STICKER_GROUP = SERVER_ADDRESS + "sticker/1/group/getlist";

    /**
     * 按Group取贴纸水印
     */
    public static final String GET_STICKER_BY_ID = SERVER_ADDRESS + "sticker/1/getlist";

    /**
     * 获取首页的最新问题摘要列表
     */
    public static final String GET_NEW_QUESTIONS = SERVER_ADDRESS + "question/1/newest-brief/get";

    /**
     * 获取精选问题的摘要列表
     */
    public static final String GET_BEST_QUESTIONS = SERVER_ADDRESS + "question/1/best-brief/get";

    /**
     * 获取某人发布过的问题列表
     */
    public static final String GET_USER_QUESTIONS = SERVER_ADDRESS + "question/1/byuser/get";

    /**
     * 获取首页的编辑精选
     */
    public static final String GET_BEST_FEED = SERVER_ADDRESS + "feed/1/best/getlist";

    /**
     * 关注页的推荐列表
     */
    public static final String GET_RECOMMEND_FEED = SERVER_ADDRESS + "feed/1/recommendFeed/getlist";

    /**
     * 点赞文章, 专题, Web页面, 问题
     */
    public static final String ADD_LIKE = SERVER_ADDRESS + "like/1/likebody/add";

    /**
     * 删除Like2
     */
    public static final String DELETE_LIKE_2 = SERVER_ADDRESS + "like/1/deleteByArticleIdOrCommentId";

    /**
     * 得到专题顶部的Category分类
     */
    public static final String GET_TOPIC_CATEGORY = SERVER_ADDRESS + "guide/1/category/get";

    /**
     * 按category取专题列表
     */
    public static final String GET_TOPIC_BY_CATEGORY = SERVER_ADDRESS + "guide/1/bycategory/get_list";

    /**
     * 点赞回复
     */
    public static final String ADD_LIKE_COMMENT = SERVER_ADDRESS + "like/1/likeComment/add";

    /**
     * 删除点赞回复
     */
    public static final String DELETE_LIKE_COMMENT = SERVER_ADDRESS + "like/1/deleteByArticleIdOrCommentId";

    /**
     * 热门话题
     */
    public static final String GET_RECOMMEND_THEME = SERVER_ADDRESS + "theme/1/recommend/get";

    /**
     * 得到Banner广告位
     */
    public static final String GET_ONBOARD_BANNER = SERVER_ADDRESS + "banner/1/onboard/get";

    /**
     * 用于获取推荐达人
     */
    public static final String GET_RECOMMEND_EXPERT = SERVER_ADDRESS + "user/1/expert/recommend/get";

    /**
     * 取好友的Feed列表
     */
    public static final String GET_FRIEND_FEED = SERVER_ADDRESS + "feed/1/friendFeed/getlist";

    /**
     * 得到发现页的顶部分类
     */
    public static final String GET_FEED_CATEGORY = SERVER_ADDRESS + "feed/1/category/get";

    /**
     * 得到发现页的分类列表
     */
    public static final String GET_FEED_BY_CATEGORY = SERVER_ADDRESS + "feed/1/bycategory/getlist";

    /**
     * 按TagName取问答列表接口
     */
    public static final String GET_QUESTION_BY_TAGNAME = SERVER_ADDRESS + "question/1/bytagname/get";

    /**
     * 取问答详情
     */
    public static final String GET_QUESTION_DETAIL = SERVER_ADDRESS + "question/1/detail/get";

    /**
     * 加关注
     */
    public static final String ADD_FOLLOW = SERVER_ADDRESS + "buddy/1/follow/add";

    /**
     * 取消关注
     */
    public static final String DELETE_FOLLOW = SERVER_ADDRESS + "buddy/1/follow/delete";

    /**
     * 取被关注者列表
     */
    public static final String GET_FOLLOWING = SERVER_ADDRESS + "buddy/1/stars/getlist";

    /**
     * 取粉丝列表
     */
    public static final String GET_FOLLOWERS = SERVER_ADDRESS + "buddy/1/fans/getlist";

    /**
     * 取消息列表
     */
    public static final String GET_MESSAGE_BY_TYPE = SERVER_ADDRESS + "message/1/by-group-type/get";

    /**
     * 消息全部已读
     */
    public static final String READ_ALL_MESSAGE = SERVER_ADDRESS + "message/1/read-all";

    /**
     * 福利社的URL
     */
    public static final String WELFARE_URL = SERVER_ADDRESS + "market/homepage";

    /**
     * 分享得积分
     */
    public static final String SHARE_BONUS = SERVER_ADDRESS + "bonus/1/share/add";
}
