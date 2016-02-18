package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.Category;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-25.
 */
public class CategoryAdapter extends YmamBaseAdapter<Category> {

    public CategoryAdapter(Context context, List<Category> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);

    }

    @Override
    public void convert(final ViewHolder viewHolder, final Category item, final int position) {
        viewHolder.setImageResource(R.id.item_cate_img, item.getImgId());
        viewHolder.setText(R.id.item_cate_txt, item.getName());

        final TextView cateTxt = viewHolder.getView(R.id.item_cate_txt);
        switch (Integer.valueOf(item.getId())) {
            case 1:
                cateTxt.setTextColor(context.getResources().getColor(R.color.color_ff3c81));
                break;
            case 2:
                cateTxt.setTextColor(context.getResources().getColor(R.color.color_ffb400));
                break;
            case 3:
                cateTxt.setTextColor(context.getResources().getColor(R.color.color_ff5b45));
                break;
            case 4:
                cateTxt.setTextColor(context.getResources().getColor(R.color.color_ff4c51));
                break;
            case 5:
                cateTxt.setTextColor(context.getResources().getColor(R.color.color_6dcdc2));
                break;
            case 6:
                cateTxt.setTextColor(context.getResources().getColor(R.color.color_ff8a00));
                break;
        }
    }
}
