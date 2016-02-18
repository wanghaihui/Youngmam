package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.fragment.AskedQuestionFragment;
import com.xiaobukuaipao.youngmam.fragment.PublishFragment;
import com.xiaobukuaipao.youngmam.parallax.ScrollTabHolder;
import com.xiaobukuaipao.youngmam.parallax.ScrollTabHolderFragment;
import com.xiaobukuaipao.youngmam.widget.PagerSlidingTabStrip.IconTabProvider;

/**
 * Created by xiaobu1 on 15-4-29.
 */
public class MineFragmentPagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider {
    private static final String TAG = MineFragmentPagerAdapter.class.getSimpleName();

    // 简单Map
    private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
    // Tab resources
    private final int[] resourcesId = { R.drawable.indicator_photo, R.drawable.indicator_question };

    // 监听器
    private ScrollTabHolder listener;

    public MineFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
    }

    public void setTabHolderScrollingContent(ScrollTabHolder listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return resourcesId.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ScrollTabHolderFragment fragmentPublish = (ScrollTabHolderFragment) PublishFragment.instance(position, null);
                mScrollTabHolders.put(position, fragmentPublish);

                if (listener != null) {
                    fragmentPublish.setScrollTabHolder(listener);
                }

                return fragmentPublish;
            case 1:
                ScrollTabHolderFragment fragmentAskedQuestion = (ScrollTabHolderFragment) AskedQuestionFragment.instance(position, null);
                mScrollTabHolders.put(position, fragmentAskedQuestion);

                if (listener != null) {
                    fragmentAskedQuestion.setScrollTabHolder(listener);
                }

                return fragmentAskedQuestion;
            default:
                return null;
        }
    }

    public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
        return mScrollTabHolders;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getPageIconResId(int position) {
        return resourcesId[position];
    }

}
