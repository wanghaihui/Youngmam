package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.xiaobukuaipao.youngmam.domain.NavigationCategory;
import com.xiaobukuaipao.youngmam.fragment.FindFragment;

import java.util.ArrayList;

/**
 * Created by xiaobu1 on 15-10-14.
 */
public class FindFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<NavigationCategory> categories;

    public FindFragmentPagerAdapter(FragmentManager fm, ArrayList<NavigationCategory> categories) {
        super(fm);
        this.categories = categories;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categories.get(position).getName();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Fragment getItem(int position) {
        return FindFragment.newInstance(categories.get(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
