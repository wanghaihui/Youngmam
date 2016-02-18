package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.ImageModel;
import com.xiaobukuaipao.youngmam.imagechooser.ImageLoader;
import com.xiaobukuaipao.youngmam.utils.StringUtil;

import java.util.List;

/**
 * Created by xiaobu1 on 15-5-5.
 */
public class PublishPhotoAdapter extends YmamBaseAdapter<ImageModel> {
    private static final String TAG = PublishPhotoAdapter.class.getSimpleName();

    private OnPublishImageDeleteListener onPublishImageDeleteListener;

    public void setOnPublishImageDeleteListener(OnPublishImageDeleteListener onPublishImageDeleteListener) {
        this.onPublishImageDeleteListener = onPublishImageDeleteListener;
    }

    public PublishPhotoAdapter(Context context, List<ImageModel> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final ImageModel item, final int position) {
        final ImageView imageView = (ImageView) viewHolder.getView(R.id.image_item);
        final ImageButton imageButton = (ImageButton) viewHolder.getView(R.id.image_item_delete);

        if (item.isFunction) {
            imageView.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.GONE);
            imageView.setTag("is_function");
            imageView.setImageResource(R.drawable.btn_publish_add);

            if (position == 9) {
                imageView.setVisibility(View.GONE);
            }

        } else {

            imageView.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.VISIBLE);

            if (!StringUtil.isEmpty(item.path)) {
                ImageLoader.getInstance(1, ImageLoader.Type.LIFO).loadImage(item.path, imageView);
            }

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPublishImageDeleteListener.onPublishImageDelete(position);
                }
            });

        }
    }

    public interface OnPublishImageDeleteListener {
        void onPublishImageDelete(int position);
    }

}
