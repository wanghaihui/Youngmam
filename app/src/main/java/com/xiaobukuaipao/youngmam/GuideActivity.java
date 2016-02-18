package com.xiaobukuaipao.youngmam;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.youngmam.adapter.GuidePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-22.
 */
public class GuideActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    public static final String LOGIN_FIRST = "login_first";

    private ViewPager viewPager;
    private GuidePagerAdapter guidePagerAdapter;
    // View数据
    private List<View> views;

    // 底部小圆点
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        viewPager = (ViewPager) findViewById(R.id.guide_pager);

        views = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(this);
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.view_guide_find, null));
        views.add(inflater.inflate(R.layout.view_guide_share, null));
        views.add(inflater.inflate(R.layout.view_guide_gift, null));
        views.add(inflater.inflate(R.layout.view_guide_start_young, null));

        // 初始化Adapter
        guidePagerAdapter = new GuidePagerAdapter(views, this);
        viewPager.setAdapter(guidePagerAdapter);

        // 绑定回调
        viewPager.setOnPageChangeListener(this);

        // 初始化底部小原点
        initDots();
    }

    private void initDots() {
        LinearLayout mDotsLayout = (LinearLayout) findViewById(R.id.guide_dots_layout);
        dots = new ImageView[views.size()];
        // 循环取得小点图片
        for (int i=0; i < views.size(); i++) {
            dots[i] = (ImageView) mDotsLayout.getChildAt(i);
            dots[i].setEnabled(false);
        }

        currentIndex = 0;
        // 设置为白色,即选中状态
        dots[currentIndex].setEnabled(true);
    }

    /**
     * 设置当前的点
     */
    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1 || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);

        currentIndex = position;
    }


    @Override
    public void	onPageScrollStateChanged(int state) {

    }

    @Override
    public void	onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void	onPageSelected(int position) {
        // 设置底部小点选中状态
        setCurrentDot(position);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
