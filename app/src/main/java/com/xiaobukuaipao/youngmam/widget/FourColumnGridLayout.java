package com.xiaobukuaipao.youngmam.widget;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.SizeImage;
import com.xiaobukuaipao.youngmam.fragment.PhotoViewFragment;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-10-14.
 */
public class FourColumnGridLayout extends ViewGroup {
    private static final String TAG = FourColumnGridLayout.class.getSimpleName();

    // 图片之间的间隔
    private int gap = 5;

    // 行
    private int rows;
    // 列
    private int columns;

    // 图片数据
    private List<SizeImage> datas;

    // 图片布局的总宽度
    private int totalWidth;

    private FragmentManager fragmentManager;

    public FourColumnGridLayout(Context context) {
        super(context);
    }

    public FourColumnGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 此值可调节--以后可以扩展成属性来进行通用--待扩展
        totalWidth = DisplayUtil.getScreenWidth(context) - DisplayUtil.dip2px(context, 40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    /**
     * 布局子View
     */
    private void layoutChildrenView() {
        int childrenCount = datas.size();

        // 单个图片的宽度和高度--长宽相等
        int singleWidth = (totalWidth - gap * (4 - 1)) / 4;
        int singleHeight = singleWidth;

        // 根据子View的数量确定高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = singleHeight * rows + gap * (rows - 1);
        setLayoutParams(params);

        for (int i=0; i < childrenCount; i++) {
            ImageView childrenView = (ImageView) getChildAt(i);
            // 设置图片
            Glide.with(getContext())
                    .load(datas.get(i).getUrl())
                    .placeholder(R.mipmap.default_loading)
                    .into(childrenView);

            int[] position = findPosition(i);
            int left = (singleWidth + gap) * position[1];
            int top = (singleHeight + gap) * position[0];
            int right = left + singleWidth;
            int bottom = top + singleHeight;

            childrenView.layout(left, top, right, bottom);
        }
    }

    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i=0; i < rows; i++) {
            for (int j=0; j < columns; j++) {
                if ((i * columns + j) == childNum) {
                    position[0] = i;
                    position[1] = j;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 设置图片数据
     * @param datas
     */
    public void setImageDatas(List<SizeImage> datas, FragmentManager fragmentManager) {
        if (datas == null || datas.isEmpty()) {
            return;
        }

        this.fragmentManager = fragmentManager;

        // 初始化布局--确定行和列
        generateChildrenLayout(datas.size());
        // 重用View的处理
        if (this.datas == null) {
            int i = 0;
            while (i < datas.size()) {
                ImageView iv = generateImageView(i);
                addView(iv, generateDefaultLayoutParams());
                i++;
            }
        } else {
            int oldViewCount = this.datas.size();
            int newViewCount = datas.size();
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount - 1, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i=0; i < (newViewCount - oldViewCount); i++) {
                    ImageView iv = generateImageView(oldViewCount + i);
                    addView(iv, generateDefaultLayoutParams());
                }
            }
        }

        this.datas = datas;
        layoutChildrenView();
    }

    private void generateChildrenLayout(int length) {
        columns = 4;
        rows = (int) Math.ceil((double) length / 4);
    }

    private ImageView generateImageView(final int position) {
        final ImageView iv = new ImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Log.d(TAG, "positon : " + position);
        iv.setTag(R.id.image_tag_position, String.valueOf(position));

        if (fragmentManager != null) {
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<String> imageUrls = new ArrayList<String>();
                    for (int i = 0; i < FourColumnGridLayout.this.datas.size(); i++) {
                        imageUrls.add(FourColumnGridLayout.this.datas.get(i).getUrl());
                    }

                    Log.d(TAG, "position :  " + (String) v.getTag(R.id.image_tag_position));

                    PhotoViewFragment fragment = new PhotoViewFragment(imageUrls, Integer.valueOf((String) v.getTag(R.id.image_tag_position)));
                    fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
                    fragment.show(fragmentManager, "viewpager");
                }
            });
        }

        return iv;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public int getGap() {
        return this.gap;
    }

}
