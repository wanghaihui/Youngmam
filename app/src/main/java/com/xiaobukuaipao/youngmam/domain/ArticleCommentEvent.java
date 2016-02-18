package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-13.
 */
public class ArticleCommentEvent extends CommentEvent {

    public ArticleCommentEvent(String articleId, int count, int type) {
        super(articleId, count, type);
    }

}
