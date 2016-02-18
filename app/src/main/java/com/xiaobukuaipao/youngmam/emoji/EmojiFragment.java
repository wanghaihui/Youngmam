package com.xiaobukuaipao.youngmam.emoji;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-8-10.
 */
public class EmojiFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private static final String TAG = EmojiFragment.class.getSimpleName();

    private ImageButton emojiKeyboard;
    private ImageButton emojiBackspace;

    private OnKeyboardBackspaceClickedListener onKeyboardBackspaceClickedListener;

    private View[] mEmojiTabs;
    private int mEmojiTabLastSelectedIndex = -1;

    public static void input(EditText editText, Emoji emoji) {
        if (editText == null || emoji == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (start < 0) {
            editText.append(emoji.getEmoji());
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), emoji.getEmoji(), 0, emoji.getEmoji().length());
        }
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 填充View
        View view = inflater.inflate(R.layout.fragment_emoji, container, false);
        final ViewPager emojiPager = (ViewPager) view.findViewById(R.id.emoji_pager);
        emojiPager.setOnPageChangeListener(this);

        EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter(getFragmentManager());

        emojiPager.setAdapter(emojiPagerAdapter);

        mEmojiTabs = new View[2];

        mEmojiTabs[0] = view.findViewById(R.id.emoji_expression);
        mEmojiTabs[1] = view.findViewById(R.id.emoji_yantext);

        for (int i=0; i < mEmojiTabs.length; i++) {
            final int position = i;
            mEmojiTabs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emojiPager.setCurrentItem(position);
                }
            });
        }

        emojiKeyboard = (ImageButton) view.findViewById(R.id.emoji_keyboard);
        emojiKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onKeyboardBackspaceClickedListener != null) {
                    onKeyboardBackspaceClickedListener.onKeyboardBackspaceClicked(view, OnKeyboardBackspaceClickedListener.TYPE_KEYBOARD);
                }
            }
        });

        emojiBackspace = (ImageButton) view.findViewById(R.id.emoji_backspace);
        emojiBackspace.setOnTouchListener(new RepeatListener(1000, 50, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onKeyboardBackspaceClickedListener != null) {
                    onKeyboardBackspaceClickedListener.onKeyboardBackspaceClicked(view, OnKeyboardBackspaceClickedListener.TYPE_BACKSPACE);
                }
            }
        }));

        onPageSelected(0);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof OnKeyboardBackspaceClickedListener) {
            onKeyboardBackspaceClickedListener = (OnKeyboardBackspaceClickedListener) getActivity();
        } else {
            throw new IllegalArgumentException(activity + "must implement interface " + OnKeyboardBackspaceClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        onKeyboardBackspaceClickedListener = null;
        super.onDetach();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mEmojiTabLastSelectedIndex == position) {
            return;
        }

        switch (position) {
            case 0:
            case 1:
                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
                    mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
                }
                mEmojiTabs[position].setSelected(true);
                mEmojiTabLastSelectedIndex = position;
                break;
        }
    }

    public interface OnKeyboardBackspaceClickedListener {
        public static final int TYPE_KEYBOARD = 0;
        public static final int TYPE_BACKSPACE = 1;

        void onKeyboardBackspaceClicked(View view, int type);
    }

    private static class EmojiPagerAdapter extends FragmentStatePagerAdapter {
        private static final int TAB_SIZE = 2;

        public EmojiPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // 主要实现这两个方法
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EmojiGridFragment.newInstance(People.DATA);
                case 1:
                    return YanGridFragment.newInstance(YanText.DATA);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return TAB_SIZE;
        }
    }

    /**
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically(循环的,周期的) runs a clickListener, emulating(模仿) keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     *
     * Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     */
    public static class RepeatListener implements View.OnTouchListener {
        // 正常的间隔
        private final int normalInterval;
        private final View.OnClickListener onClickListener;
        private Handler handler = new Handler();
        // 初始间隔
        private int initialInterval;

        private View downView;

        // 线程
        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }

                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                onClickListener.onClick(downView);
            }
        };

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param onClickListener The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener onClickListener) {
            if (onClickListener == null) {
                throw new IllegalArgumentException("Null Runnable");
            }

            if (initialInterval < 0 || normalInterval < 0) {
                throw new IllegalArgumentException("Negative Interval");
            }

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.onClickListener = onClickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    downView.setBackgroundColor(downView.getContext().getResources().getColor(R.color.color_f3f3f3));
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    onClickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    downView.setBackgroundColor(downView.getContext().getResources().getColor(R.color.color_d9d9d9));
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
            }

            return false;
        }
    }
}
