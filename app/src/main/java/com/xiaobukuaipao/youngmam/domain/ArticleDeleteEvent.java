package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-10-29.
 */
public class ArticleDeleteEvent {
    protected String articleId;

    public ArticleDeleteEvent(String articleId) {
        this.articleId = articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getArticleId() {
        return this.articleId;
    }
}
