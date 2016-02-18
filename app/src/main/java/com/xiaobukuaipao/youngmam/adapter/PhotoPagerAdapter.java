package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.domain.ImageModel;
import com.xiaobukuaipao.youngmam.widget.JazzyViewPager;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-17.
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private List<ImageModel> mPhotoList;
    private JazzyViewPager viewPager;

    public PhotoPagerAdapter(JazzyViewPager viewPager) {
        this.viewPager = viewPager;
    }

    /**
     * 实例化
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView photoView = new ImageView(container.getContext());

        Glide.with(container.getContext())
                .load(mPhotoList.get(position).path)
                .crossFade()
                .into(photoView);

        photoView.setTag(position);

        container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        viewPager.setObjectForPosition(photoView, position);

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

    public void setPhotoLists(List<ImageModel> mPhotos) {
        mPhotoList = mPhotos;
    }

}
