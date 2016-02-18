package com.xiaobukuaipao.youngmam.utils;

import android.content.Context;

import com.xiaobukuaipao.youngmam.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xiaobu1 on 15-5-16.
 */
public class TimeUtil {

    public final static int TYPE_5MIN = 0;
    public final static int TYPE_1DAY = 1;
    public final static int TYPE_2DAY = 2;
    public final static int TYPE_3DAY = 3;
    public final static int TYPE_1WEEK = 4;
    public final static int TYPE_OTHER = 5;

    // 1秒=1000毫秒
    private static long sec = 1000;
    private static long min = sec * 60;
    private static long hour = min * 60;
    private static long day = hour * 24;

    /**
     * 计算时间间隔
     * @param time
     */
    public static String handleTime(Context context, long time) {
        // 获得系统当前的时间
        long currentTimeMillis = System.currentTimeMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Calendar calendar = dateFormat.getCalendar();
        calendar.setTime(new Date(currentTimeMillis));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        long timeInMillis = calendar.getTimeInMillis();

        if (time > currentTimeMillis - 5 * min) {
            // 5 minute
            dateFormat.applyPattern(context.getResources().getString(R.string.time_interval_five_mins));
        } else if (time > timeInMillis) {
            // today
            dateFormat.applyPattern(context.getResources().getString(R.string.time_interval_today));
        } else if (time > timeInMillis - day) {
            // yesterday
            dateFormat.applyPattern(context.getResources().getString(R.string.time_interval_yesterday));
        } else if (time > timeInMillis - 2 * day) {
            // one more day
            dateFormat.applyPattern(context.getResources().getString(R.string.time_interval_one_more_day));
        } else if (time > timeInMillis - 6 * day) {
            // one week
            dateFormat.applyPattern(context.getResources().getString(R.string.time_interval_one_week));
        } else {
            // out of one week
            dateFormat.applyPattern(context.getResources().getString(R.string.time_interval_out_one_week));
        }

        String format = dateFormat.format(new Date(time));
        return format;
    }

    public static String dtFormat(Date date, String dateFormat){
        return getFormat(dateFormat).format(date);
    }

    private static final DateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }
}
