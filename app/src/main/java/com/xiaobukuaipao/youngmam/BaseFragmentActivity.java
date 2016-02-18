package com.xiaobukuaipao.youngmam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

/**
 * Created by xiaobu1 on 15-4-21.
 */
// 抽象的内容--ActionBar
public abstract  class BaseFragmentActivity extends FragmentActivity {
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();

    protected static final String STATE_RELOGIN = "relogin";

    // ActionBar
    protected YoungActionBar actionBar;

    protected boolean hasComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 处理保存状态问题
        handleSavedInstanceState(savedInstanceState);

        AppActivityManager.getInstance().pushActivity(this);

        initViews();
    }

    protected abstract void initViews();

    @Override
    protected void onDestroy() {
        AppActivityManager.getInstance().popActivity(this);
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    //////////////////////////////////////////////////////////////////
    // ActionBar
    public void setBackClickListener(final Activity activity) {
        actionBar.getLeftFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void handleSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_RELOGIN)) {
                Log.d(TAG, "handle saved instance state : relogin");
                AppActivityManager.getInstance().popAllActivity();
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_RELOGIN, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 处理软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!hasComment) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    hideKeyboard(v.getWindowToken());
                }
            }
        } else {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                hideInput(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 隐藏输入框
     */
    protected void hideInput(MotionEvent ev) {
        // For children
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     * @param view
     */
    protected void showKeyboard(View view) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(view, 0);
    }

    /**
     * 隐藏软键盘
     */
    protected void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    protected void setHasComment(boolean hasComment) {
        this.hasComment = hasComment;
    }
}
