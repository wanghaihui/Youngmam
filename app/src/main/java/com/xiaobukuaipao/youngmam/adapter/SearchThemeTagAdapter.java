package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SearchDetailActivity;
import com.xiaobukuaipao.youngmam.domain.Label;
import com.xiaobukuaipao.youngmam.domain.Tag;
import com.xiaobukuaipao.youngmam.domain.Theme;

import java.util.List;

/**
 * Created by xiaobu1 on 15-7-21.
 */
public class SearchThemeTagAdapter extends BaseAdapter {

    public static final int TYPE_COUNT = 3;

    public static final int TYPE_INVALID = -1;
    public static final int TYPE_TAG = 0;
    public static final int TYPE_THEME = 1;

    private Context context;
    private List<Tag> data;

    private LayoutInflater inflater;

    // 普通tag
    public static final int TYPE_SEARCH_TAG = 1;
    // 专题
    public static final int TYPE_SEARCH_THEME = 4;

    public SearchThemeTagAdapter(Context context, List<Tag> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int type = data.get(position).getType();

        if (type == TYPE_SEARCH_TAG) {
            return TYPE_TAG;
        } else if (type == TYPE_SEARCH_THEME) {
            return TYPE_THEME;
        } else {
            return TYPE_INVALID;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TagHolder tagHolder = null;
        TagHolder themeHolder = null;
        InvalidViewHolder invalidViewHolder = null;

        int type = getItemViewType(position);

        if (convertView == null) {
            inflater = LayoutInflater.from(context);

            switch (type) {
                case TYPE_TAG:
                    convertView = inflater.inflate(R.layout.item_select_theme, parent, false);
                    tagHolder = new TagHolder();

                    tagHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                    tagHolder.title = (TextView) convertView.findViewById(R.id.title);
                    tagHolder.desc = (TextView) convertView.findViewById(R.id.desc);

                    convertView.setTag(tagHolder);
                    break;
                case TYPE_SEARCH_THEME:
                    convertView = inflater.inflate(R.layout.item_select_theme, parent, false);
                    themeHolder = new TagHolder();

                    themeHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                    themeHolder.title = (TextView) convertView.findViewById(R.id.title);
                    themeHolder.desc = (TextView) convertView.findViewById(R.id.desc);

                    convertView.setTag(themeHolder);

                    break;
                case TYPE_INVALID:
                    convertView = inflater.inflate(R.layout.item_invalid, parent, false);

                    invalidViewHolder = new InvalidViewHolder();
                    invalidViewHolder.content = (TextView) convertView.findViewById(R.id.comment);

                    convertView.setTag(invalidViewHolder);

                    break;
            }
        } else {
            switch (type) {
                case TYPE_TAG:
                    tagHolder = (TagHolder) convertView.getTag();
                    break;
                case TYPE_SEARCH_THEME:
                    themeHolder = (TagHolder) convertView.getTag();
                    break;
                case TYPE_INVALID:
                    invalidViewHolder = (InvalidViewHolder) convertView.getTag();
                    break;
            }
        }

        switch (type) {
            case TYPE_TAG:
                Picasso.with(context)
                        .load(data.get(position).getImgUrl())
                        .placeholder(R.mipmap.default_loading)
                        .into(tagHolder.avatar);
                tagHolder.title.setText(data.get(position).getName());
                tagHolder.desc.setText(context.getResources().getString(R.string.str_iamatag));

                ((View) tagHolder.avatar.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Theme theme = new Theme(null, null, new Label(data.get(position).getTagId(), data.get(position).getName()));

                        Intent intent = new Intent(context, SearchDetailActivity.class);
                        intent.putExtra("tag_id", theme.getTag().getId());
                        intent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                        context.startActivity(intent);
                    }
                });
                break;

            case TYPE_SEARCH_THEME:
                Picasso.with(context)
                        .load(data.get(position).getImgUrl())
                        .placeholder(R.mipmap.default_loading)
                        .into(themeHolder.avatar);
                themeHolder.title.setText(data.get(position).getName());
                themeHolder.desc.setText(data.get(position).getDesc());
                break;

            case TYPE_INVALID:
                invalidViewHolder.content.setText(context.getResources().getString(R.string.str_invalid_tag));
                break;
        }

        return convertView;
    }

    public static class TagHolder {
        ImageView avatar;
        TextView title;
        TextView desc;
    }

    public static class InvalidViewHolder {
        TextView content;
    }
}
