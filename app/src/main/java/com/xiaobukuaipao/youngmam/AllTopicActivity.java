package com.xiaobukuaipao.youngmam;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.adapter.TopicFragmentPagerAdapter;
import com.xiaobukuaipao.youngmam.domain.NavigationCategory;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.PagerSlidingTabStrip;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;

/**
 * Created by xiaobu1 on 15-4-25.
 * 所有专题列表
 */
public class AllTopicActivity extends BaseHttpFragmentActivity {
    private static final String TAG = AllTopicActivity.class.getSimpleName();

    private ArrayList<NavigationCategory> topicCategoryList;

    private PagerSlidingTabStrip tabStrip;
    private ViewPager viewPager;

    private TopicFragmentPagerAdapter pagerAdapter;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    public void initViews() {
        setContentView(R.layout.activity_all_topic);
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        dm = getResources().getDisplayMetrics();

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // 设置数据
        initDatas();

        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_all_topic));

        setBackClickListener(this);
    }


    private void initDatas() {
        topicCategoryList = new ArrayList<NavigationCategory>();
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

    }

    public void executeHttpRequest() {
        // 得到专题列表顶部的Category分类
        mEventLogic.getTopicCategory();
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_topic_category:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                topicCategoryList.add(new NavigationCategory(jsonArray.getJSONObject(i)));
                            }

                            pagerAdapter = new TopicFragmentPagerAdapter(getSupportFragmentManager(), topicCategoryList);
                            viewPager.setAdapter(pagerAdapter);
                            tabStrip.setViewPager(viewPager);
                            setTabsValue();
                        }

                    }
                }
                break;

            default:
                break;
        }
    }

}
