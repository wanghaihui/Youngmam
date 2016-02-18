package com.xiaobukuaipao.youngmam.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.PhotoPagerAdapter;
import com.xiaobukuaipao.youngmam.domain.ImageModel;
import com.xiaobukuaipao.youngmam.imagechooser.ViewPagerListener;
import com.xiaobukuaipao.youngmam.widget.JazzyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-17.
 */
@SuppressLint("ValidFragment")
public class ViewPagerDialogFragment extends DialogFragment {

    private static final String TAG = ViewPagerDialogFragment.class.getSimpleName();

    private JazzyViewPager viewPager;
    private int mCurrentItem;

    /**
     * 所有的图片列表
     */
    private List<ImageModel> mPicList = new ArrayList<ImageModel>();

    private List<ImageModel> mSelectedImage;

    private PhotoPagerAdapter mPhotoAdapter;

    private RelativeLayout mPagerTitleBar;
    private RelativeLayout mPagerBottomBar;
    private RelativeLayout mBtnBack;
    private Button mBtnNext;
    private ImageButton mSelectView;
    private TextView mSelectNum;

    private ViewPagerListener mViewPagerEventListener;

    private int canSelectedNum;

    public void setCanSelectedNum(int canSelectedNum) {
        this.canSelectedNum = canSelectedNum;
    }

    public ViewPagerDialogFragment(List<ImageModel> picList, int currentItem, List<ImageModel> mSelectedImage) {
        mPicList.addAll(picList);
        mCurrentItem = currentItem;
        this.mSelectedImage = mSelectedImage;

        removeFunctionItems();
    }

    private void removeFunctionItems() {
        for (int i=0; i < mPicList.size(); i++) {
            if (mPicList.get(i).isFunction) {
                if (mCurrentItem > i) {
                    mCurrentItem = mCurrentItem - 1;
                }
                mPicList.remove(i);
                i--;
            }
        }
    }

    /**
     * 必须重写的函数
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View picViewPager = inflater.inflate(R.layout.fragment_viewpager_dialog, null);
        viewPager = (JazzyViewPager) picViewPager.findViewById(R.id.view_pager);

        mPagerTitleBar = (RelativeLayout) picViewPager.findViewById(R.id.photopager_title_bar);
        mPagerBottomBar = (RelativeLayout) picViewPager.findViewById(R.id.photopager_bottom_bar);

        initViewsAndDatas();
        return picViewPager;
    }

    private void initViewsAndDatas() {
        mBtnBack = (RelativeLayout) mPagerTitleBar.findViewById(R.id.picker_back);
        mBtnNext = (Button) mPagerTitleBar.findViewById(R.id.picker_done);

        mSelectView = (ImageButton) mPagerBottomBar.findViewById(R.id.image_select);
        mSelectNum = (TextView) mPagerBottomBar.findViewById(R.id.select_num);

        setPhotoLists(mPicList);
        viewPager.setOnPageChangeListener(mOnPageChangeListener);
        viewPager.setCurrentItem(mCurrentItem);
        viewPager.setPageMargin(0);

        // 此处只处理多选情况
        mSelectView.setSelected(mPicList.get(mCurrentItem).isSelected);
        mSelectView.setOnClickListener(mOnCheckBoxClickedListener);

        updateSelectedNum();

        setUIListeners();
    }

    /**
     * 设置图片列表
     * @param mPhotos
     */
    public void setPhotoLists(List<ImageModel> mPhotos) {
        mPhotoAdapter = new PhotoPagerAdapter(viewPager);
        mPhotoAdapter.setPhotoLists(mPhotos);
        viewPager.setAdapter(mPhotoAdapter);
    }

    public void notifyChange() {
        mPhotoAdapter.notifyDataSetChanged();
    }


    private void setUIListeners() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerEventListener.onDismiss();
                ViewPagerDialogFragment.this.dismiss();
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPagerEventListener.onDone();
                ViewPagerDialogFragment.this.dismiss();
            }
        });
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentItem = position;
            boolean isSelected = mPicList.get(position).isSelected;
            mSelectView.setSelected(isSelected);
            updateSelectedNum();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnClickListener mOnCheckBoxClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = viewPager.getCurrentItem();
            boolean isSelected = mPicList.get(position).isSelected;
            if (!isSelected && getSelectedSize() >= 9) {
                Toast.makeText(v.getContext(), "您最多可选择9张图片", Toast.LENGTH_SHORT).show();
                return;
            }

            mSelectView.setSelected(!isSelected);
            mPicList.get(position).isSelected = !isSelected;

            if (mPicList.get(position).isSelected) {
                mSelectedImage.add(mPicList.get(position));
            } else {
                // mSelectedImage.remove(mPicList.get(position));
                for(int i=0; i < mSelectedImage.size(); i++) {
                    if ((mSelectedImage.get(i).path).equals(mPicList.get(position).path)) {
                        mSelectedImage.remove(i);
                    }
                }
            }

            updateSelectedNum();
        }
    };

    private int getSelectedSize() {
        /*int size = 0;
        for (int i=0; i < mPicList.size(); i++) {
            if (mPicList.get(i).isSelected) {
                size = size + 1;
            }
        }
        return size;*/
        return mSelectedImage.size();
    }

    /**
     * 更新选择的个数
     */
    private void updateSelectedNum() {
        mSelectNum.setText(getSelectedSize() + "/" + 9);
    }

    public void setOnEventListener(ViewPagerListener viewPagerEventListener) {
        mViewPagerEventListener = viewPagerEventListener;
    }

}
