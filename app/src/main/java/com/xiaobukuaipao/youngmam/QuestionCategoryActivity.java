package com.xiaobukuaipao.youngmam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import com.xiaobukuaipao.youngmam.adapter.QCFragmentPagerAdapter;
import com.xiaobukuaipao.youngmam.domain.Category;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.widget.PagerSlidingTabStrip;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;

/**
 * Created by xiaobu1 on 15-9-25.
 */
public class QuestionCategoryActivity extends BaseHttpFragmentActivity {

    /**
     * 全部的问题分类
     */
    private ArrayList<Category> questionCategories;
    /**
     * 当前的问题分类
     */
    private int position;

    private PagerSlidingTabStrip tabStrip;
    private ViewPager viewPager;

    private QCFragmentPagerAdapter pagerAdapter;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    // 提问问题
    private RelativeLayout bottomLayout;

    public void initViews() {
        setContentView(R.layout.activity_question_category);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        dm = getResources().getDisplayMetrics();

        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pagerAdapter = new QCFragmentPagerAdapter(getSupportFragmentManager(), questionCategories);
        pagerAdapter.setActivity(this);

        viewPager.setAdapter(pagerAdapter);
        tabStrip.setViewPager(viewPager);
        viewPager.setCurrentItem(position);

        setTabsValue();

        setUIListeners();
    }

    private void getIntentDatas() {
        questionCategories = getIntent().getParcelableArrayListExtra("question_category_list");
        position = getIntent().getIntExtra("position", 0);

        if (questionCategories == null) {
            questionCategories = new ArrayList<Category>();
        }
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_question_answer));
        setBackClickListener(this);
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值
     */
    private void setTabsValue() {
        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);

        // 设置Tab是自动填充满屏幕的
        tabStrip.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabStrip.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3, dm));
        // 设置Tab标题文字的大小
        tabStrip.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));

        tabStrip.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/FZLTXIHK.TTF"), Typeface.NORMAL);

        // 设置Tab Indicator的颜色
        tabStrip.setIndicatorColor(Color.parseColor("#ff4c51"));

        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColor(Color.parseColor("#ff4c51"));

    }

    private void setUIListeners() {
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = QuestionCategoryActivity.this.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    Label label = new Label("-1", questionCategories.get(viewPager.getCurrentItem()).getName());
                    Intent intent = new Intent(QuestionCategoryActivity.this, PublishActivity.class);
                    intent.putExtra("type_publish", PublishActivity.TYPE_PUBLISH_QUESTION);
                    intent.putExtra("label", label);
                    startActivity(intent);
                } else {
                    // 跳到登录页
                    Intent intent = new Intent(QuestionCategoryActivity.this, RegisterAndLoginActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            default:
                break;
        }
    }
}
