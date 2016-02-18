package com.xiaobukuaipao.youngmam.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaobukuaipao.youngmam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-4-28.
 */
public class YearPickerView extends ListView implements OnItemClickListener, DatePickerDialog.OnDateChangedListener {
    /**
     * Year适配器
     */
    private YearAdapter mAdapter;

    private int mChildSize;
    private final DatePickerController mController;
    // 选中的View
    private TextViewWithCircularIndicator mSelectedView;
    private int mViewSize;

    public YearPickerView(Context context, DatePickerController datePickerController) {
        super(context);

        mController = datePickerController;
        mController.registerOnDateChangedListener(this);

        Resources resources = context.getResources();

        // 布局
        setLayoutParams(new AbsListView.LayoutParams(resources.getDimensionPixelOffset(R.dimen.year_label_width), AbsListView.LayoutParams.WRAP_CONTENT));

        mViewSize = resources.getDimensionPixelOffset(R.dimen.year_label_height);
        mChildSize = resources.getDimensionPixelOffset(R.dimen.year_label_height);

        // 显示或隐藏ListView顶部和底部的过渡阴影
        setVerticalFadingEdgeEnabled(true);
        // 阴影长度
        setFadingEdgeLength(mChildSize / 3);

        init(context);

        setOnItemClickListener(this);
        setSelector(new StateListDrawable());
        setDividerHeight(0);
        setVerticalScrollBarEnabled(false);

        // 日期改变
        onDateChanged();

    }

    private void init(Context context) {
        ArrayList<String> years = new ArrayList<String>();
        for (int year = mController.getMinYear(); year <= mController.getMaxYear(); year++) {
            years.add(String.format("%d", year));
        }

        mAdapter = new YearAdapter(context, R.layout.year_label_text_view, years);
        setAdapter(mAdapter);
    }

    /**
     * 得到第一个位置的偏移
     */
    public int getFirstPositionOffset() {
        final View firstChild = getChildAt(0);
        if (firstChild == null) {
            return 0;
        }

        return firstChild.getTop();
    }


    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
        TextViewWithCircularIndicator clickedView = (TextViewWithCircularIndicator) view;

        if (clickedView != null) {
            if (clickedView != mSelectedView) {

                if (mSelectedView != null) {
                    mSelectedView.drawIndicator(false);
                    mSelectedView.requestLayout();
                }

                clickedView.drawIndicator(true);
                clickedView.requestLayout();
                mSelectedView = clickedView;

            }

            mController.onYearSelected(getYearFromTextView(clickedView));
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 日期发生改变
     */
    public void onDateChanged() {
        mAdapter.notifyDataSetChanged();

        // 设置选择中心
        postSetSelectionCentered(mController.getSelectedDay().year - mController.getMinYear());
    }

    public void postSetSelectionCentered(int position) {
        postSetSelectionFromTop(position, mViewSize / 2 - mChildSize / 2);
    }

    public void postSetSelectionFromTop(final int position, final int y) {
        post(new Runnable() {
            @Override
            public void run() {
                setSelectionFromTop(position, y);
                requestLayout();
            }
        });
    }


    /**
     * 得到year
     */
    private static int getYearFromTextView(TextView view) {
        return Integer.valueOf(view.getText().toString());
    }

    private class YearAdapter extends ArrayAdapter<String> {
        /**
         * 构造函数
         * @param context
         * @param resource
         * @param years
         */
        public YearAdapter(Context context, int resource, List<String> years) {
            super(context, resource, years);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewWithCircularIndicator textView = (TextViewWithCircularIndicator) super.getView(position, convertView, parent);
            // 请求布局
            textView.requestLayout();
            // 得到year
            int year = getYearFromTextView(textView);

            // 此年是否被选中
            boolean selected = mController.getSelectedDay().year == year;

            textView.drawIndicator(selected);

            if (selected) {
                mSelectedView = textView;
            }

            return textView;
        }
    }
}
