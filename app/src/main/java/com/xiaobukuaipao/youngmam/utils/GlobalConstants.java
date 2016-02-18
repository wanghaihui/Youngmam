package com.xiaobukuaipao.youngmam.utils;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class GlobalConstants {

    public static final String YOUNGMAM_USERGUIDE = "youngmam_userguide";

    /**
     * 官网
     */
    public static final String HUAYOUNGMAM = "http://www.youngmam.com";

    /**
     * 分享相关的接口
     */

    public static final String SHARE_ARTICLE = "http://www.youngmam.com/web/share/article?articleId=";
    public static final String SHARE_QUESTION = HUAYOUNGMAM + "/web/share/question?articleId=";
    public static final String SHARE_ACTIVITY = "http://www.youngmam.com/web/share/activity?activityId=";
    public static final String SHARE_TOPIC = "http://www.youngmam.com/web/share/topic?topicId=";
    public static final String SHARE_THEME = "http://www.youngmam.com/web/share/tagId?tagId=";

    public static final String SHARE_REGISTER = "http://www.youngmam.com/web/invite_friend/add_phone?app_user=";

    /**
     * 关于我们
     */
    public static final String ABOUT = "http://www.youngmam.com/about";

    /**
     * 积分细则
     */
    public static final String CREDIT_RULE = "http://www.youngmam.com/intergal/index.html";

    /**
     * 微信AppId
     */
    public static final String WECHAT_APPID = "wx3b3c56b444524afc";
    public static final String WECHAT_APPSECRET = "f6260f7706487d89194e106a2169734f";

    /**
     * 微博基本参数
     */
    public static final String WEIBO_APP_KEY = "70266070";
    public static final String WEIBO_REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    public static final String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    /**
     * QQ基本参数
     */
    public static final String QQ_APPID = "1104496033";
    public static final String QQ_APPKEY = "qfjCsZSxYBtbikTP";

    public static final String QQ_SCOPE = "all";

    public static final String JSON_STATUS = "status";
    public static final String JSON_MSG = "msg";

    public static final String JSON_DATA = "data";


    /**
     * 友盟
     */
    public static final String UMENG_APPKEY = "5559c28567e58e0667000037";
    public static final String UMENG_DESCRIPTOR = "com.umeng.share";



    /**
     * 活动
     */
    public static final String JSON_POSTERURL = "posterUrl";
    public static final String JSON_USERCOUNT = "userCount";
    public static final String JSON_DESC = "desc";
    public static final String JSON_IMGS = "imgs";
    public static final String JSON_IMG_DESC = "imgDesc";
    public static final String JSON_URL = "url";
    public static final String JSON_ORIGINALIMGURL = "originalImgUrl";
    public static final String JSON_ACTIVITYID = "activityId";
    public static final String JSON_MINARTICLEID = "minArticleId";
    public static final String JSON_TAG = "tag";

    /**
     * 达人
     */
    public static final String JSON_EXPERTDESC = "expertDesc";
    public static final String JSON_EXPERTNAME = "expertName";
    public static final String JSON_EXPERTTYPE = "expertType";
    public static final String JSON_FLAG = "flag";
    public static final String JSON_LASTARTICLEID = "lastArticleId";
    public static final String JSON_LASTIMGS = "lastImgs";

    /**
     * 文章
     */
    public static final String JSON_ARTICLEID = "articleId";
    public static final String JSON_CONTENT = "content";
    public static final String JSON_COMMENTCOUNT = "commentCount";
    public static final String JSON_CREATETIME = "createTime";
    public static final String JSON_HASLIKED = "hasLiked";
    public static final String JSON_IMGIDS = "imgIds";
    public static final String JSON_LIKECOUNT = "likeCount";
    public static final String JSON_TAGS = "tags";
    public static final String JSON_USERBASE = "userBase";
    public static final String JSON_HEADURL = "headUrl";
    public static final String JSON_NAME = "name";
    public static final String JSON_MOBILE = "mobile";
    public static final String JSON_ADDR = "addr";

    /**
     * 专题
     */
    public static final String JSON_IMGURL = "imgUrl";
    public static final String JSON_ORDER = "order";
    public static final String JSON_TITLE = "title";
    public static final String JSON_TOPICDESC = "topicDesc";
    public static final String JSON_TOPICID = "topicId";
    public static final String JSON_ARTICLE = "article";
    public static final String JSON_ITEMDESC = "itemDesc";
    public static final String JSON_ITEMS = "items";
    public static final String JSON_MINTOPICID = "minTopicId";
    public static final String JSON_BEGINTIME = "beginTime";
    public static final String JSON_TOPICARTICLEIDS = "topicArticleIds";
    public static final String JSON_UPDATETIME = "updateTime";

    /**
     * 编辑标签
     */
    public static final String JSON_EDITORTAGID = "editorTagId";

    /**
     * 喜欢文章的人
     */
    public static final String JSON_USERID = "userId";
    public static final String JSON_CHILDSTATUS = "childStatus";

    /**
     * 图片相关
     */
    public static final String JSON_IMGID = "imgId";
    public static final String JSON_IMGWIDTH = "imgWidth";
    public static final String JSON_IMGHEIGHT = "imgHeight";

    /**
     * 评论
     */
    public static final String JSON_COMMENTID = "commentId";
    public static final String JSON_ORIGINCOMMENTID = "originCommentId";
    public static final String JSON_ORIGINUSERBASE = "originUserBase";
    public static final String JSON_MINCOMMENTID = "minCommentId";

    /**
     * 标签
     */
    public static final String JSON_TAGID = "tagId";

    /**
     * User
     */
    public static final String JSON_GENDER = "gender";
    public static final String JSON_BIRTHTIME = "birthTime";

    /**
     * 通知相关
     */
    public static final String JSON_ACTORUSERBASE = "actorUserBase";
    public static final String JSON_MSGID = "msgId";
    public static final String JSON_OBJECTID = "objectId";
    public static final String JSON_READSTATUS = "readStatus";
    public static final String JSON_RECEIVERID = "receiverId";
    public static final String JSON_TYPE = "type";
    public static final String JSON_PAGE = "page";
    public static final String JSON_MINMSGID = "minMsgId";

    /**
     * 个人中心--mine
     */
    public static final String JSON_MINLIKEID = "minLikeId";
    public static final String JSON_ARTICLECOUNT = "articleCount";

    /**
     * 获取未读消息数
     */
    public static final String JSON_UNREADCOUNT = "unreadCount";

    /**
     * 搜索文章的翻页
     */
    public static final String JSON_START = "start";

    /**
     * 热门翻页
     */
    public static final String JSON_MINHOTID = "minHotId";

    /**
     * Cookie
     */
    public static final String SET_COOKIE_KEY = "Set-Cookie";
    public static final String COOKIE_KEY = "Cookie";
    public static final String COOKIE_UID = "uid";
    public static final String COOKIE_P = "p";
    public static final String COOKIE_T = "t";
    public static final String COOKIE_LOGIN_TYPE = "login_type";

    /**
     * Banner活动
     */
    public static final String JSON_OBJECT = "object";
    public static final String JSON_BUSINESSTYPE = "businessType";
    public static final String JSON_POSITION = "position";

    /**
     * 积分商城
     */
    public static final String JSON_BONUSPOINT = "bonusPoint";
    public static final String JSON_COST = "cost";
    public static final String JSON_COUNT = "count";
    public static final String JSON_ITEMID = "itemId";
    public static final String JSON_PRIVILEGE = "privilege";
    public static final String JSON_TXCOUNT = "txCount";
    public static final String JSON_MINITEMID = "minItemId";

    /**
     * 话题
     */
    public static final String JSON_MINLASTARTICLEID = "minLastArticleId";

    /**
     * 我的积分
     */
    public static final String JSON_FINALPOINT = "finalPoint";
    public static final String JSON_TODAYPOINT = "todayPoint";
    public static final String JSON_TODAYENTRIES = "todayEntries";
    public static final String JSON_GROUP1 = "group1";
    public static final String JSON_GROUP2 = "group2";
    public static final String JSON_GROUP3 = "group3";

    public static final String JSON_DONE = "done";
    public static final String JSON_LIMIT = "limit";
    public static final String JSON_POINT = "point";

    /**
     * H5
     */
    public static final String JSON_REDIRECTURL = "redirectUrl";

    /**
     * 新专题
     */
    public static final String JSON_HEADIMG = "headImg";
    public static final String JSON_HEADIMGHEIGHT = "headImgHeight";
    public static final String JSON_HEADIMGWIDTH = "headImgWidth";
    public static final String JSON_CARDS = "cards";
    public static final String JSON_MARKER = "marker";
    public static final String JSON_TEXT = "text";
    public static final String JSON_HEADLINE1 = "headline1";
    public static final String JSON_HEAD = "head";
    public static final String JSON_ITEMLIST = "itemList";
    public static final String JSON_NUM = "num";
    public static final String JSON_PRICE = "price";
    public static final String JSON_PHONE = "phone";
    public static final String JSON_BUSINESSHOURS = "businessHours";
    public static final String JSON_TARGETURL = "targetUrl";

    public static final String JSON_BUSINESSID = "businessId";

    public static final String JSON_ADDRID = "addrId";
    public static final String JSON_TXID = "txId";
    public static final String JSON_ITEM = "item";
    public static final String JSON_TX = "tx";
    public static final String JSON_MINTXID = "minTxId";

    public static final String JSON_ID = "id";
    public static final String JSON_POSTERMARKER = "posterMarker";
    public static final String JSON_DISPLAYTITLE = "displayTitle";
    public static final String JSON_CATEGORY = "category";

    public static final String JSON_MINID = "minId";

    public static final String JSON_ACTORID = "actorId";

    public static final String JSON_ACTIVITY = "activity";

    public static final String JSON_CHILDRENCOMMENTS = "childrenComments";
    public static final String JSON_ROOTCOMMENTID = "rootCommentId";

    public static final String JSON_GROUPID = "groupId";
    public static final String JSON_GROUPNAME = "groupName";

    public static final String JSON_ALIGNMENT = "alignment";
    public static final String JSON_GROUP = "group";
    public static final String JSON_IMG = "img";
    public static final String JSON_MARGINX = "marginX";
    public static final String JSON_MARGINY = "marginY";
    public static final String JSON_SMALLIMG = "smallImg";
    public static final String JSON_SMALLIMGID = "smallImgId";
    public static final String JSON_STICKERID = "stickerId";
    public static final String JSON_STICKERNAME = "stickerName";

    public static final String JSON_CATEGORYOBJ = "categoryObj";
    public static final String JSON_RGBCOLOR = "rgbColor";

    public static final String JSON_LIKEID = "likeId";

    public static final String JSON_HASFOLLOWED = "hasFollowed";

    public static final String JSON_FANSCOUNT = "fansCount";
    public static final String JSON_FOLLOWCOUNT = "followCount";

    public static final String JSON_MINUSERID = "minUserId";

    public static final String JSON_NEWFANSCOUNT = "newFansCount";
    public static final String JSON_NEWCOMMENTCOUNT = "newCommentCount";
    public static final String JSON_NEWLIKECOUNT = "newLikeCount";
    public static final String JSON_NEWSYSNOTICECOUNT = "newSysNoticeCount";

    public static final String JSON_GROUPTYPE = "groupType";

    public static final String JSON_SMALLPOSTERURL = "smallPosterUrl";


}
