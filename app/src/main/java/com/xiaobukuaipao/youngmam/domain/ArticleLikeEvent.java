package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-20.
 */
public class ArticleLikeEvent extends  LikeEvent {

    public ArticleLikeEvent(String articleId, int type) {
        super(articleId, type);
    }
}
