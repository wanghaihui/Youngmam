package com.xiaobukuaipao.youngmam.emoji;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-8-13.
 */
public class YanAdapter extends ArrayAdapter<Emoji> {

    public YanAdapter(Context context, Emoji[] data) {
        super(context, R.layout.yan_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.yan_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (TextView) view.findViewById(R.id.emoji_icon);
            view.setTag(holder);
        }

        Emoji emoji = getItem(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.icon.setText(emoji.getEmoji());
        return view;
    }

    public static class ViewHolder {
        TextView icon;
    }
}
