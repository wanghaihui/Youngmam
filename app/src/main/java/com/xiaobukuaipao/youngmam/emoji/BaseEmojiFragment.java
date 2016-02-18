package com.xiaobukuaipao.youngmam.emoji;

import android.support.v4.app.Fragment;

/**
 * Created by xiaobu1 on 15-8-13.
 */
public class BaseEmojiFragment extends Fragment {

    protected OnEmojiClickedListener onEmojiClickedListener;

    public interface OnEmojiClickedListener {
        void onEmojiClicked(Emoji emoji);
    }
}
