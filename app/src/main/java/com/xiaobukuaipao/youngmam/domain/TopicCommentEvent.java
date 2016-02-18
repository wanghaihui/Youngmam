package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-12.
 */
public class TopicCommentEvent extends CommentEvent {

    public TopicCommentEvent(String articleId, int count, int type) {
        super(articleId, count, type);
    }

}
