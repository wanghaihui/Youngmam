package com.xiaobukuaipao.youngmam.listener;

import com.xiaobukuaipao.youngmam.domain.Comment;

/**
 * Created by xiaobu1 on 15-10-12.
 */
public interface OnLikeCommentClickListener {
    public static final int TYPE_LIKE_COMMENT = 1;
    public static final int TYPE_UNLIKE_COMMENT = 2;

    void onLikeCommentClick(Comment comment, int type);

}
