package com.xiaobukuaipao.youngmam.listener;

import com.xiaobukuaipao.youngmam.domain.Article;

/**
 * Created by xiaobu1 on 15-5-20.
 * 文章Action接口
 */
public interface OnActionClickListener {

    public static final int ACTION_LIKE = 0;
    public static final int ACTION_COMMENT = 1;
    public static final int ACTION_SHARE = 2;

    public void onActionClick(int action, int position, Article article);
}
