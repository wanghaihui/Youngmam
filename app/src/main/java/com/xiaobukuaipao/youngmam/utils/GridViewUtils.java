package com.xiaobukuaipao.youngmam.utils;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import com.xiaobukuaipao.youngmam.view.NestedGridView;

public final class GridViewUtils {
    /**
     * 存储宽度
     */
    static SparseIntArray mGvWidth = new SparseIntArray();

    /**
     * 计算GridView的高度
     *
     * @param gridView 要计算的GridView
     */
    public static void updateGridViewLayoutParams(Context context, NestedGridView gridView, int maxColumn) {
        int childs = gridView.getAdapter().getCount();

        if (childs > 0) {
            int columns = childs < maxColumn ? childs % maxColumn : maxColumn;
            gridView.setNumColumns(columns);
            int width = 0;
            int cacheWidth = mGvWidth.get(columns);
            if (cacheWidth != 0) {
                width = cacheWidth;
            } else {
                width =  (DisplayUtil.getScreenWidth(context) - maxColumn * DisplayUtil.dip2px(context, 10)) * columns / maxColumn;
            }

            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.width = width;
            gridView.setLayoutParams(params);

            if (mGvWidth.get(columns) == 0) {
                mGvWidth.append(columns, width);
            }
        }
    }
}
