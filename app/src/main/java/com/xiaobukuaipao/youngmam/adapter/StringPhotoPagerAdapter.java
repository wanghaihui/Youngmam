package com.xiaobukuaipao.youngmam.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.photoview.PhotoView;
import com.xiaobukuaipao.youngmam.photoview.PhotoViewAttacher;
import com.xiaobukuaipao.youngmam.widget.JazzyViewPager;

import java.util.List;

/**
 * Created by xiaobu1 on 15-6-5.
 */
public class StringPhotoPagerAdapter extends PagerAdapter {

    private List<String> mPhotoList;
    private JazzyViewPager viewPager;

    private OnViewPagerPhotoClickListener onViewPagerPhotoClickListener;

    private OnViewPagerPhotoLongClickListener onViewPagerPhotoLongClickListener;

    public void setOnViewPagerPhotoClickListener(OnViewPagerPhotoClickListener onViewPagerPhotoClickListener) {
        this.onViewPagerPhotoClickListener = onViewPagerPhotoClickListener;
    }

    public void setOnViewPagerPhotoLongClickListener(OnViewPagerPhotoLongClickListener onViewPagerPhotoLongClickListener) {
        this.onViewPagerPhotoLongClickListener = onViewPagerPhotoLongClickListener;
    }

    public StringPhotoPagerAdapter(JazzyViewPager viewPager) {
        this.viewPager = viewPager;
    }

    /**
     * 实例化
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoView photoView = new PhotoView(container.getContext());

        Glide.with(container.getContext())
                .load(mPhotoList.get(position))
                .into(photoView);

        photoView.setTag(position);

        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        viewPager.setObjectForPosition(photoView, position);

        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                onViewPagerPhotoClickListener.onViewPagerPhotoClick();
            }
        });

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mPhotoList.get(position).startsWith("http") || mPhotoList.get(position).startsWith("https")) {
                    onViewPagerPhotoLongClickListener.onViewPagerPhotoLongClick(mPhotoList.get(position));
                }
                return true;
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

    public interface OnViewPagerPhotoClickListener {
        void onViewPagerPhotoClick();
    }

    public interface OnViewPagerPhotoLongClickListener {
        void onViewPagerPhotoLongClick(String url);
    }

}
