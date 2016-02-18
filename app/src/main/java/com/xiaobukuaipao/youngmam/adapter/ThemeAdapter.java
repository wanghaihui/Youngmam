package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.SizeImage;
import com.xiaobukuaipao.youngmam.domain.Theme;
import com.xiaobukuaipao.youngmam.widget.NineGridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-6-24.
 */
public class ThemeAdapter extends YmamBaseAdapter<Theme> {

    public ThemeAdapter(Context context, List<Theme> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Theme item, final int position) {
        ((TextView) viewHolder.getView(R.id.tag_name)).setText(item.getTag().getTitle());

        final NineGridLayout nineGridLayout = viewHolder.getView(R.id.nine_grid_layout);

        if (item.getImgs() != null && item.getImgs().size() > 0) {
            ArrayList<SizeImage> images = new ArrayList<SizeImage>();

            int length = item.getImgs().size();

            for(int i=0; i < length; i++) {
                images.add(new SizeImage(item.getImgs().getJSONObject(i)));
            }

            nineGridLayout.setVisibility(View.VISIBLE);
            nineGridLayout.setImageDatas(images, null);
        }
    }
}
