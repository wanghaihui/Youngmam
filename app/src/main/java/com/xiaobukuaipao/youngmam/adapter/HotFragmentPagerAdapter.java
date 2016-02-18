package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.xiaobukuaipao.youngmam.fragment.FindCategoryFragment;
import com.xiaobukuaipao.youngmam.fragment.FollowFragment;

/**
 * Created by xiaobu1 on 15-7-3.
 */
public class HotFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_SIZE = 2;

    public HotFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // 主要实现这两个方法
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FollowFragment();
            case 1:
                return new FindCategoryFragment();
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
