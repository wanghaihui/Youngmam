package com.xiaobukuaipao.youngmam.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.FindFragmentPagerAdapter;
import com.xiaobukuaipao.youngmam.domain.NavigationCategory;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * Created by xiaobu1 on 15-10-14.
 *
 * Fragment多次嵌套使用ViewPager, 一定要注意使用getChildFragmentManager来处理
 */
public class FindCategoryFragment extends BaseHttpFragment {
    private static final String TAG = FindCategoryFragment.class.getSimpleName();

    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    private ArrayList<NavigationCategory> categoryList;

    private PagerSlidingTabStrip tabStrip;
    private ViewPager viewPager;

    private FindFragmentPagerAdapter pagerAdapter;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_category, container, false);
        this.view = view;
        mEventLogic = new YoungEventLogic(this);
        return view;
    }

    @Override
    public void initUIAndData() {
        dm = getResources().getDisplayMetrics();

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);

        tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);

        // 设置数据
        initDatas();

        setUIListeners();

        /**
         * 获取发现页的顶部分类
         */
        mEventLogic.getFeedCategory();
    }

    private void initDatas() {
        categoryList = new ArrayList<NavigationCategory>();
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

        tabStrip.setTypeface(Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/FZLTXIHK.TTF"), Typeface.NORMAL);

        // 设置Tab Indicator的颜色
        tabStrip.setIndicatorColor(Color.parseColor("#ff4c51"));

        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColor(Color.parseColor("#ff4c51"));

    }

    private void setUIListeners() {

    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_feed_category:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        JSONArray jsonArray = jsonObject.getJSONArray(GlobalConstants.JSON_DATA);

                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                categoryList.add(new NavigationCategory(jsonArray.getJSONObject(i)));
                            }

                            pagerAdapter = new FindFragmentPagerAdapter(this.getChildFragmentManager(), categoryList);
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
