package com.xiaobukuaipao.youngmam.calendar;

/**
 * Created by xiaobu1 on 15-4-28.
 */
abstract interface DatePickerController {

    // 得到一周的第一天
    public abstract int getFirstDayOfWeek();
    // 得到最大的年
    public abstract int getMaxYear();
    // 得到最小的年
    public abstract int getMinYear();

    // 得到选择的天
    public abstract SimpleMonthAdapter.CalendarDay getSelectedDay();

    // 月中的天被选择
    public abstract void onDayOfMonthSelected(int year, int month, int day);

    // 年被选择
    public abstract void onYearSelected(int year);

    // 注册日期改变监听器
    public abstract void registerOnDateChangedListener(DatePickerDialog.OnDateChangedListener onDateChangedListener);


}
