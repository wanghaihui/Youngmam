package com.xiaobukuaipao.youngmam;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by xiaobu1 on 15-4-21.
 */
public class AppActivityManager {

    private static final String TAG = AppActivityManager.class.getSimpleName();

    // Activity栈
    private static Stack<Activity> activityStack;

    private static AppActivityManager instance = new AppActivityManager();

    private AppActivityManager() {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
    }

    // Single Instance
    public static AppActivityManager getInstance() {
        return instance;
    }

    /**
     * 得到当前的Activity
     */
    public Activity currentActivity() {
        if (activityStack == null || activityStack.size() == 0) {
            return null;
        }

        Activity activity = activityStack.lastElement();

        return activity;
    }

    /**
     * Push Activity
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }

        activityStack.add(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for(Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                popActivity(activity);
            }
        }
    }

    /**
     * Pop Activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 退出所有Activity, 当前Activity除外
     */
    public void popAllActivityExceptMain(Class cls) {

        // 此时, main在栈底
        while (true) {

            Activity activity = currentActivity();

            if (activity == null) {
                break;
            }

            if (activity.getClass().equals(cls)) {
                break;
            }

            // 弹出Activity
            popActivity(activity);
        }

    }

    /**
     * 退出所有Activity
     */
    public void popAllActivity() {
        while (!activityStack.isEmpty()) {
            Activity activity = currentActivity();
            popActivity(activity);
        }

    }

}
