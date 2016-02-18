package com.xiaobukuaipao.youngmam.domain;

/**
 * Created by xiaobu1 on 15-4-24.
 */
public class Fresh {

    private String image;

    private String avatar;

    private String name;

    private String time;

    private String intruduce;

    private int likeNum;

    private int commentNum;

    public Fresh(String image, String avatar, String name, String time, String intruduce, int likeNum, int commentNum) {
        this.image = image;
        this.avatar = avatar;
        this.name = name;
        this.time = time;
        this.intruduce = intruduce;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
    }

    public String getImage() {
        return this.image;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getName() {
        return this.name;
    }

    public String getTime() {
        return this.time;
    }

    public String getIntruduce() {
        return this.intruduce;
    }

    public int getLikeNum() {
        return this.likeNum;
    }

    public int getCommentNum() {
        return this.commentNum;
    }

}
