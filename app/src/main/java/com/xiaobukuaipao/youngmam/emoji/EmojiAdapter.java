package com.xiaobukuaipao.youngmam.emoji;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;

/**
 * Created by xiaobu1 on 15-8-10.
 */
public class EmojiAdapter extends ArrayAdapter<Emoji> {
    public EmojiAdapter(Context context, Emoji[] data) {
        super(context, R.layout.emoji_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = View.inflate(getContext(), R.layout.emoji_item, null);
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
