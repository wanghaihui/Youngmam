package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.xiaobukuaipao.youngmam.fragment.FollowFindFragment;
import com.xiaobukuaipao.youngmam.fragment.MessageFragment;
import com.xiaobukuaipao.youngmam.fragment.MineFragment;
import com.xiaobukuaipao.youngmam.fragment.SelectFragment;

/**
 * Created by xiaobu1 on 15-4-23.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_SIZE = 4;

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // 主要实现这两个方法
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SelectFragment();
            case 1:
                return new FollowFindFragment();
            case 2:
                return new MessageFragment();
            case 3:
                return new MineFragment();
            default:
                return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 防止销毁fragment
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return TAB_SIZE;
    }
}
