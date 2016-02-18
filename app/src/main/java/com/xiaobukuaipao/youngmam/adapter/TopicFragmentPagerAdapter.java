package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.xiaobukuaipao.youngmam.domain.NavigationCategory;
import com.xiaobukuaipao.youngmam.fragment.TopicCategoryFragment;

import java.util.ArrayList;

/**
 * Created by xiaobu1 on 15-10-10.
 */
public class TopicFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<NavigationCategory> topicCategories;

    public TopicFragmentPagerAdapter(FragmentManager fm, ArrayList<NavigationCategory> topicCategories) {
        super(fm);
        this.topicCategories = topicCategories;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return topicCategories.get(position).getName();
    }

    @Override
    public int getCount() {
        return topicCategories.size();
    }

    @Override
    public Fragment getItem(int position) {
        return TopicCategoryFragment.newInstance(topicCategories.get(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
