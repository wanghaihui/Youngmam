package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-10.
 */
public class LikeEvent {
    public static final int TYPE_ADD_LIKE = 1;
    public static final int TYPE_DELETE_LIKE = 2;

    protected String articleId;
    protected int type;

    public LikeEvent(String articleId, int type) {
        this.articleId = articleId;
        this.type = type;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getArticleId() {
        return this.articleId;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }

}
