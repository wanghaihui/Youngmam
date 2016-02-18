package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.database.SearchTable;

/**
 * Created by xiaobu1 on 15-5-26.
 */
public class SearchCursorAdapter extends CursorAdapter {
    private static final String TAG = SearchCursorAdapter.class.getSimpleName();

    private Context context = null;
    private LayoutInflater inflater = null;

    private OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    // 缺省的构造函数--构建
    public SearchCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 一般都这样写，返回列表行元素，注意这里返回的就是bindView中的view
        ViewHolder viewHolder = null;
        View convertView = null;

        convertView = inflater.inflate(R.layout.item_latest_search, parent, false);
        viewHolder = new ViewHolder();
        convertView.setTag(viewHolder);

        viewHolder.word = (TextView) convertView.findViewById(R.id.search);
        viewHolder.delete = (ImageButton) convertView.findViewById(R.id.delete);

        return convertView;
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.word.setText(cursor.getString(cursor.getColumnIndex(SearchTable.COLUMN_WORD)));

        final long id = cursor.getInt(cursor.getColumnIndex(SearchTable.COLUMN_ID));

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClick(String.valueOf(id));
            }
        });
    }

    private static class ViewHolder {
        TextView word;
        ImageButton delete;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String id);
    }
}
