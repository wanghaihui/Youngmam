package com.xiaobukuaipao.youngmam.listener;

import com.xiaobukuaipao.youngmam.domain.Article;

/**
 * Created by xiaobu1 on 15-10-14.
 */
public interface OnActionClickListener2 {

    public static final int ACTION_REPLY = 0;
    public static final int ACTION_SHARE = 1;

    public void onActionClick2(int action, int position, Article article);
}
