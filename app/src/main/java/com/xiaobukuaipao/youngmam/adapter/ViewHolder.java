package com.xiaobukuaipao.youngmam.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xiaobu1 on 15-4-10.
 */
// SparseArray是android里为<Interger,Object>这样的Hashmap而专门写的类,目的是提高效率，其核心是折半查找函数（binarySearch）
public class ViewHolder {

    private View mConvertView;
    private int position;
    public final SparseArray<View> views;


    public ViewHolder(Context context, int position, ViewGroup parent, int itemLayoutId) {
        this.position = position;
        views = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getViewHolder(Context context, int position, View convertView, ViewGroup parent, int itemLayoutId) {
        if (convertView == null) {
            return new ViewHolder(context, position, parent, itemLayoutId);
        }
        return (ViewHolder) convertView.getTag();
    }

    /**
     * 得到View
     * @return T
     */
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public int getPosition() {
        return position;
    }


    /**
     * 下面是对常用View的统一处理
     */

    /**
     * 为TextView设置字符串
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    /**
     * 为ImageView设置图片
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

}
