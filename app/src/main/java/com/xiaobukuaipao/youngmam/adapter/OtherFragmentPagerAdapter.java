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
import com.xiaobukuaipao.youngmam.widget.PagerSlidingTabStrip;

/**
 * Created by xiaobu1 on 15-5-26.
 */
public class OtherFragmentPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    // 简单Map
    private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
    // Tab titles
    // Tab resources
    private final int[] resourcesId = { R.drawable.indicator_photo, R.drawable.indicator_question };

    // 监听器
    private ScrollTabHolder listener;

    private String userId;

    public OtherFragmentPagerAdapter(FragmentManager fm, String userId) {
        super(fm);
        mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
        this.userId = userId;
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
                ScrollTabHolderFragment fragmentPublish = (ScrollTabHolderFragment) PublishFragment.instance(position, userId);
                mScrollTabHolders.put(position, fragmentPublish);

                if (listener != null) {
                    fragmentPublish.setScrollTabHolder(listener);
                }

                return fragmentPublish;
            case 1:
                ScrollTabHolderFragment fragmentLike = (ScrollTabHolderFragment) AskedQuestionFragment.instance(position, userId);
                mScrollTabHolders.put(position, fragmentLike);

                if (listener != null) {
                    fragmentLike.setScrollTabHolder(listener);
                }
                return fragmentLike;
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
