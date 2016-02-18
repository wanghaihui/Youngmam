package com.xiaobukuaipao.youngmam.filter;

import android.content.Context;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.adapter.ViewHolder;
import com.xiaobukuaipao.youngmam.adapter.YmamBaseAdapter;
import com.xiaobukuaipao.youngmam.domain.StickerGroup;

import java.util.List;

/**
 * Created by xiaobu1 on 15-10-7.
 */
public class StickerGroupAdapter extends YmamBaseAdapter<StickerGroup> {

    public StickerGroupAdapter(Context context, List<StickerGroup> stickerGroups, int mItemLayoutId) {
        super(context, stickerGroups, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final StickerGroup item, final int position) {
        final TextView name = viewHolder.getView(R.id.name);
        name.setText(item.getGroupName());

        if (item.isSelected()) {
            name.setSelected(true);
        } else {
            name.setSelected(false);
        }
    }
}
