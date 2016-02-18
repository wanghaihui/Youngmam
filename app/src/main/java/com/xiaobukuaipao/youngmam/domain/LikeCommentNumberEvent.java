package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-6-13.
 */
public class LikeCommentNumberEvent {

    private String articleId;

    private int likeNum;
    private int commentNum;

    public LikeCommentNumberEvent(String articleId, int likeNum, int commentNum) {
        this.articleId = articleId;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }
    public String getArticleId() {
        return this.articleId;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
    public int getLikeNum() {
        return this.likeNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }
    public int getCommentNum() {
        return this.commentNum;
    }
}
