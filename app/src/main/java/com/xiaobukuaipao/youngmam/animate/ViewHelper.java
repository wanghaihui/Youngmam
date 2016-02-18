package com.xiaobukuaipao.youngmam.animate;

import android.view.View;
import android.util.Log;

import static com.xiaobukuaipao.youngmam.animate.AnimatorProxy.NEEDS_PROXY;
import static com.xiaobukuaipao.youngmam.animate.AnimatorProxy.wrap;

/**
 * Created by xiaobu1 on 15-4-30.
 */
public final class ViewHelper {
    private static final String TAG = ViewHelper.class.getSimpleName();

    private ViewHelper() {}

    public static void setTranslationY(View view, float translationY) {
        if (NEEDS_PROXY) {
            wrap(view).setTranslationY(translationY);
        } else {
            Honeycomb.setTranslationY(view, translationY);
        }
    }

    public static float getTranslationY(View view) {
        Log.d(TAG, "need proxy : " + NEEDS_PROXY);
        return NEEDS_PROXY ? wrap(view).getTranslationY() : Honeycomb.getTranslationY(view);
    }

    private static final class Honeycomb {
        static void setTranslationY(View view, float translationY) {
            view.setTranslationY(translationY);
        }

        static float getTranslationY(View view) {
            return view.getTranslationY();
        }
    }
}
