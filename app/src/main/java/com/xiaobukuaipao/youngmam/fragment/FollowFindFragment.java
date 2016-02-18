package com.xiaobukuaipao.youngmam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SearchActivity;
import com.xiaobukuaipao.youngmam.adapter.HotFragmentPagerAdapter;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;

/**
 * Created by xiaobu1 on 15-10-14.
 */
public class FollowFindFragment extends BaseHttpFragment implements  ViewPager.OnPageChangeListener {
    public static final int PAGER_FIRST = 0;
    public static final int PAGER_SECOND = 1;

    // ActionBar
    private RelativeLayout actionBar;
    private FrameLayout rightBar;

    private ViewPager viewPager;
    // 页面适配器
    private HotFragmentPagerAdapter mPagerAdapter;

    private TextView btnHot;
    private TextView btnNew;

    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_follow_find, container, false);
        this.view = view;
        mEventLogic = new YoungEventLogic(this);
        return view;
    }

    @Override
    public void initUIAndData() {
        // 添加ActionBar
        actionBar = (RelativeLayout) view.findViewById(R.id.action_bar);
        rightBar = (FrameLayout) view.findViewById(R.id.right_layout);

        mPagerAdapter = new HotFragmentPagerAdapter(this.getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(mPagerAdapter);

        btnHot = (TextView) view.findViewById(R.id.btn_hot);

        btnNew = (TextView) view.findViewById(R.id.btn_new);

        setHotPage();

        setUIListeners();
    }

    private void setUIListeners() {
        rightBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowFindFragment.this.getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        btnHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(PAGER_FIRST);
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(PAGER_SECOND);
            }
        });
    }

    private void setHotPage() {
        btnHot.setTextColor(getResources().getColor(R.color.color_ff4c51));
        btnHot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.follow_pressed), null, null, null);
        btnHot.setBackgroundResource(R.drawable.label_normal);


        btnNew.setTextColor(getResources().getColor(R.color.white));
        btnNew.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.find_normal), null, null, null);
        btnNew.setBackgroundColor(getResources().getColor(R.color.color_ff4c51));
    }

    private void setNewPage() {
        btnHot.setTextColor(getResources().getColor(R.color.white));
        btnHot.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.follow_normal), null, null, null);
        btnHot.setBackgroundColor(getResources().getColor(R.color.color_ff4c51));

        btnNew.setTextColor(getResources().getColor(R.color.color_ff4c51));
        btnNew.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.find_pressed), null, null, null);
        btnNew.setBackgroundResource(R.drawable.label_normal);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * page页选择
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        if (position == PAGER_FIRST) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setHotPage();
                }
            }, 333);

        } else if (position == PAGER_SECOND) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setNewPage();
                }
            }, 333);

        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
