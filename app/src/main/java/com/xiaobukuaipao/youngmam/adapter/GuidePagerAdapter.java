package com.xiaobukuaipao.youngmam.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xiaobukuaipao.youngmam.GuideActivity;
import com.xiaobukuaipao.youngmam.HuaYoungActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SplashActivity;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-21.
 */

/**
 * 引导页Adapter
 */
public class GuidePagerAdapter extends PagerAdapter {

    // 界面列表
    private List<View> views;
    private Activity activity;

    public GuidePagerAdapter(List<View> views, Activity activity) {
        this.views = views;
        this.activity = activity;
    }

    /**
     * 销毁position位置的界面
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }

    /**
     * 完成更新
     * @param container
     */
    @Override
    public void	finishUpdate(ViewGroup container) {

    }

    /**
     * 获得当前的界面数
     * @return
     */
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }

        return 0;
    }

    /**
     * 初始化position位置的界面
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(views.get(position), 0);

        if (position == views.size() - 1) {
            Button mStartYoung = (Button) container.findViewById(R.id.start_huayoung_btn);
            mStartYoung.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 设置引导标志位
                    setGuideEnable();
                    startHuayoungmam();
                }
            });
        }

        return views.get(position);
    }

    private void setGuideEnable() {

    }

    /**
     * 开启注册页面
     */
    private void startHuayoungmam() {
        SharedPreferences sp = activity.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        sp.edit().putBoolean(GuideActivity.LOGIN_FIRST, true).commit();

        Intent intent = new Intent(activity, HuaYoungActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 判断是否由对象生成界面
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void	restoreState(Parcelable state, ClassLoader loader) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void	startUpdate(ViewGroup container) {

    }
}
