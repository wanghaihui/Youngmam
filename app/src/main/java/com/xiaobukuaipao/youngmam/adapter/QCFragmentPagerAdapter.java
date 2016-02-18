package com.xiaobukuaipao.youngmam.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.xiaobukuaipao.youngmam.domain.Category;
import com.xiaobukuaipao.youngmam.fragment.QuestionCategoryFragment;

import java.util.ArrayList;

/**
 * Created by xiaobu1 on 15-9-25.
 */
public class QCFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private Activity activity;

    private ArrayList<Category> questionCategories;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public QCFragmentPagerAdapter(FragmentManager fm, ArrayList<Category> questionCategories) {
        super(fm);
        this.questionCategories = questionCategories;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return questionCategories.get(position).getName();
    }

    @Override
    public int getCount() {
        return questionCategories.size();
    }

    @Override
    public Fragment getItem(int position) {
        return QuestionCategoryFragment.newInstance(questionCategories.get(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
