package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xiaobu1 on 15-4-11.
 */

/**
 * Youngmam Adapter基类
 * @param <T>
 */
public abstract class YmamBaseAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> datas;
    protected final int mItemLayoutId;

    public YmamBaseAdapter(Context context, List<T> datas, int mItemLayoutId) {
        this.context = context;
        this.datas = datas;
        this.mItemLayoutId = mItemLayoutId;
    }

    public YmamBaseAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
        this.mItemLayoutId = 0;
    }

    /**
     * 得到数据集合
     * @return
     */
    public List<T> getDatas() {
        if (datas != null) {
            return datas;
        }
        return null;
    }

    /**
     * 得到集合中的数据个数
     * @return
     */
    @Override
    public int getCount() {
        return datas.size();
    }

    /**
     * 得到指定位置的数据
     * @param position
     * @return
     */
    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    /**
     * 得到指定位置的Position Id
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();
    }

    protected abstract  void convert(ViewHolder viewHolder, T item, int position);

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.getViewHolder(context, position, convertView, parent, mItemLayoutId);
    }
}
