package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-12.
 */
public class CommentEvent {

    public static final int TYPE_ADD_COMMENT = 1;
    public static final int TYPE_DELETE_COMMENT = 2;

    protected String articleId;
    protected int count;
    protected int type;

    public CommentEvent(String articleId, int count, int type) {
        this.articleId = articleId;
        this.count = count;
        this.type = type;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getArticleId() {
        return this.articleId;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return this.count;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }
}
