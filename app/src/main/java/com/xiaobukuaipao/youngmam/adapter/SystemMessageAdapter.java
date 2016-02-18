package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.domain.MamMessage;
import com.xiaobukuaipao.youngmam.utils.TimeUtil;

import java.util.List;

/**
 * Created by xiaobu1 on 15-10-24.
 */
public class SystemMessageAdapter extends YmamBaseAdapter<MamMessage> {

    public SystemMessageAdapter(Context context, List<MamMessage> datas, int mItemLayoutId) {
        super(context, datas, mItemLayoutId);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final MamMessage item, final int position) {

        final ImageView avatar = viewHolder.getView(R.id.avatar);
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        avatar.setImageResource(R.mipmap.ic_launcher);

        viewHolder.setText(R.id.name, context.getResources().getString(R.string.str_huayoung_system_message));
        viewHolder.setText(R.id.content, item.getContent());

        viewHolder.setText(R.id.time, TimeUtil.handleTime(context, item.getCreateTime()));
    }
}
