package com.xiaobukuaipao.youngmam.imagechooser;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.ViewHolder;
import com.xiaobukuaipao.youngmam.adapter.YmamBaseAdapter;
import com.xiaobukuaipao.youngmam.widget.PopupWindowForListView;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-16.
 */
public class ImageDirPopupWindow extends PopupWindowForListView<ImageDirectory> {
    private ListView mDirListView;

    private OnImageDirSelectedListener onImageDirSelectedListener;

    public void setOnImageDirSelectedListener(OnImageDirSelectedListener onImageDirSelectedListener) {
        this.onImageDirSelectedListener = onImageDirSelectedListener;
    }

    public ImageDirPopupWindow(View convertView, int width, int height, List<ImageDirectory> datas) {
        super(convertView, width, height, true, datas);
    }

    @Override
    public void initViews() {
        mDirListView = (ListView) findViewById(R.id.id_dir_list);

        mDirListView.setAdapter(new YmamBaseAdapter<ImageDirectory>(context, mDatas, R.layout.item_dir_list) {
            @Override
            public void convert(ViewHolder viewHolder, ImageDirectory item, final int position) {
                if (item.getImages() != null && item.getImages().size() > 0) {
                    ImageLoader.getInstance(3, ImageLoader.Type.LIFO)
                            .loadImage(item.getImages().get(0).path, (ImageView) viewHolder.getView(R.id.image_item));
                } else {
                    ((ImageView) viewHolder.getView(R.id.image_item)).setImageResource(R.mipmap.image_picker_default);
                }

                viewHolder.setText(R.id.dir_name, item.getDirName());
                viewHolder.setText(R.id.image_count, String.valueOf(item.getImagesCount()) + "张");
            }
        });
    }

    @Override
    public void initEvents() {
        mDirListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onImageDirSelectedListener != null) {
                    onImageDirSelectedListener.onSelected(mDatas.get(position));
                    dismiss();
                }
            }
        });

    }

    @Override
    public void init() {

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {

    }


    public interface OnImageDirSelectedListener {
        void onSelected(ImageDirectory directory);
    }

}
