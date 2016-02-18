package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-10.
 */
public class TopicLikeEvent extends LikeEvent {

    public TopicLikeEvent(String articleId, int type) {
        super(articleId, type);
    }

}
