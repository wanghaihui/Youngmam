package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-5-22.
 */
public class ArticleChangeEvent {

    public static final int ARTICLE_LIKE = 0;
    public static final int ARTICLE_NOT_LIKE = 1;
    public static final int ARTICLE_COMMENT = 2;
    public static final int ARTICLE_NOT_COMMENT = 3;
    public static final int ARTICLE_DELETE = 4;

    private String articleId;
    private int change;

    public ArticleChangeEvent(String articleId, int change) {
        this.articleId = articleId;
        this.change = change;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getArticleId() {
        return this.articleId;
    }

    public void setChange(int change) {
        this.change = change;
    }
    public int getChange() {
        return this.change;
    }
}
