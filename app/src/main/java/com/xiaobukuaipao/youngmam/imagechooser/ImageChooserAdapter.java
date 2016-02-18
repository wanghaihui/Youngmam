package com.xiaobukuaipao.youngmam.imagechooser;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.ViewHolder;
import com.xiaobukuaipao.youngmam.adapter.YmamBaseAdapter;
import com.xiaobukuaipao.youngmam.domain.ImageModel;
import com.xiaobukuaipao.youngmam.imagechooser.ImageLoader.Type;
import com.xiaobukuaipao.youngmam.utils.SDCardUtil;
import com.xiaobukuaipao.youngmam.utils.VersionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择适配器
 */
public class ImageChooserAdapter extends YmamBaseAdapter<ImageModel> {

    private static final String TAG = ImageChooserAdapter.class.getSimpleName();
    // 硬盘缓存路径
    private String diskCachePath;
    // 版本号
    private int appVersion;

    // 选择的图片
    private List<ImageModel> mSelectedImage;

    private final AdapterEventListener mEventListener;

    /**
     * 显示类型
     */
    private int showStyle;

    public ImageChooserAdapter(Context context, List<ImageModel> datas, int mItemLayoutId,
                               AdapterEventListener eventListener, List<ImageModel> mSelectedImage, int showStyle) {
        super(context, datas, mItemLayoutId);

        diskCachePath = SDCardUtil.getDiskCachePath(context);
        appVersion = VersionUtil.getAppVersion(context);

        this.mEventListener = eventListener;
        this.mSelectedImage = mSelectedImage;

        this.showStyle = showStyle;
    }

    @Override
    public void convert(final ViewHolder viewHolder, final ImageModel item, final int position) {
        // 加载图片
        final ImageView image = (ImageView) viewHolder.getView(R.id.image_item);
        final ImageButton select = (ImageButton) viewHolder.getView(R.id.image_item_select);

        select.setTag(position);

        if (showStyle == ImageChooserActivity.SHOW_STYLE_MULTIPLE) {
            select.setVisibility(View.GONE);
        } else if (showStyle == ImageChooserActivity.SHOW_STYLE_VIEW) {
            select.setVisibility(View.GONE);
        }

        if (item.isFunction) {
            image.setImageResource(R.mipmap.camera);
            image.setTag("is_function");
            image.setScaleType(ImageView.ScaleType.CENTER);
            select.setVisibility(View.GONE);
        } else {
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageLoader.getInstance(3, Type.LIFO).loadImage(item.path, image);

            image.setColorFilter(null);

            // 图片设置点击事件
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = (Integer) v.getTag();

                    if (datas.get(position).isSelected) {
                        datas.get(position).isSelected = false;
                    } else {
                        if (getSelected().size() >= 9) {
                            Toast.makeText(v.getContext(), R.string.reached_max_size, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        datas.get(position).isSelected = true;
                    }

                    v.setSelected(datas.get(position).isSelected);

                    boolean selected = true;
                    for(int i=0; i < mSelectedImage.size(); i++) {
                        if ((mSelectedImage.get(i).path).equals(item.path)) {
                            mSelectedImage.remove(i);
                            image.setColorFilter(null);
                            selected = false;
                        }
                    }

                    if (selected) {
                        // 如果未选择该照片
                        mSelectedImage.add(item);
                        image.setColorFilter(Color.parseColor("#4C000000"));
                    }

                    /*if (mSelectedImage.contains(item)) {
                        // 如果已经选择了该照片
                        mSelectedImage.remove(item);
                        image.setColorFilter(null);
                    } else {
                        mSelectedImage.add(item);
                        image.setColorFilter(Color.parseColor("#4C000000"));
                    }*/

                    mEventListener.onItemSelectedStatusChange(position, datas.get(position).isSelected);
                }
            });

            if (mSelectedImage.contains(item)) {
                image.setColorFilter(Color.parseColor("#7F000000"));
            }

            /**
             * 新的方式
             */
            select.setSelected(datas.get(position).isSelected);
        }

    }

    /**
     * 得到已经选中的图片个数
     */
    public ArrayList<ImageModel> getSelected() {
        ArrayList<ImageModel> dataS = new ArrayList<ImageModel>();
        for (int i=0; i < datas.size(); i++) {
            if (datas.get(i).isSelected) {
                dataS.add(datas.get(i));
            }
        }

        return dataS;
    }

    interface AdapterEventListener {
        void onItemSelectedStatusChange(int position, boolean isSelected);
    }

}
