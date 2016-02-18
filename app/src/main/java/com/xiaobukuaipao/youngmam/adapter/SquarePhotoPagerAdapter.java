package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.fragment.PhotoViewFragment;
import com.xiaobukuaipao.youngmam.view.SquaredImageView;
import com.xiaobukuaipao.youngmam.widget.ContentHeightViewPager;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-27.
 */
public class SquarePhotoPagerAdapter extends PagerAdapter {

    private FragmentActivity activity;
    private List<String> mPhotoList;
    private ContentHeightViewPager viewPager;

    public SquarePhotoPagerAdapter(FragmentActivity activity, ContentHeightViewPager viewPager) {
        this.activity = activity;
        this.viewPager = viewPager;
    }

    /**
     * 实例化
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final SquaredImageView photoView = new SquaredImageView(container.getContext());

        Glide.with(container.getContext())
                .load(mPhotoList.get(position))
                .centerCrop()
                .into(photoView);

        photoView.setTag(position);

        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        viewPager.setObjectForPosition(photoView, position);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoViewFragment fragment = new PhotoViewFragment(mPhotoList, position);
                fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
                fragment.show(activity.getSupportFragmentManager(), "viewpager");
            }
        });

        return photoView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object obj) {
        container.removeView(viewPager.findViewFromObject(position));
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    public void setPhotoLists(List<String> mPhotos) {
        mPhotoList = mPhotos;
    }

}
