package com.xiaobukuaipao.youngmam.calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xiaobukuaipao.youngmam.R;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by xiaobu1 on 15-5-8.
 */
public class DatePickerDialog extends DialogFragment implements View.OnClickListener, DatePickerController {
    private static final String TAG = DatePickerDialog.class.getSimpleName();

    private static final String KEY_SELECTED_YEAR = "year";
    private static final String KEY_SELECTED_MONTH = "month";
    private static final String KEY_SELECTED_DAY = "day";
    private static final String KEY_VIBRATE = "vibrate";

    private static final int MAX_YEAR = 2037;
    private static final int MIN_YEAR = 1902;

    private OnDateSetListener mCallBack;
    private final Calendar mCalendar = Calendar.getInstance();

    private HashSet<OnDateChangedListener> mListeners = new HashSet<OnDateChangedListener>();

    private LinearLayout mCalendarView;

    private int mMaxYear = MAX_YEAR;
    private int mMinYear = MIN_YEAR;

    private YearPickerView mYearPickerView;

    private DayPickerView mDayPickerView;

    private Vibrator mVibrator;

    private int mWeekStart = mCalendar.getFirstDayOfWeek();

    /**
     * 是否震动
     */
    private boolean mVibrate = true;

    private String mSelectYear;
    private String mSelectDay;

    private boolean mDelayAnimation = true;

    public static final int ANIMATION_DELAY = 500;

    // OK按钮
    private Button mBtnOk;

    public DatePickerDialog() {
        // Empty constructor required for dialog fragment. DO NOT REMOVE
    }

    public static DatePickerDialog newInstance(OnDateSetListener onDateSetListener, int year, int month, int day) {
        return newInstance(onDateSetListener, year, month, day, true);
    }

    public static DatePickerDialog newInstance(OnDateSetListener onDateSetListener, int year, int month, int day, boolean vibrate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        datePickerDialog.initialize(onDateSetListener, year, month, day, vibrate);
        return datePickerDialog;
    }

    public void initialize(OnDateSetListener onDateSetListener, int year, int month, int day, boolean vibrate) {
        if (year > MAX_YEAR) {
            throw new IllegalArgumentException("year end must < " + MAX_YEAR);
        }

        if (year < MIN_YEAR) {
            throw new IllegalArgumentException("year end must > " + MIN_YEAR);
        }

        mCallBack = onDateSetListener;
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);

        mVibrate = vibrate;
    }

    public void setYearRange(int minYear, int maxYear) {
        if (maxYear < minYear) {
            throw new IllegalArgumentException("Year end must be larger than year start");
        }

        if (maxYear > MAX_YEAR) {
            throw new IllegalArgumentException("max year end must < " + MAX_YEAR);
        }

        if (minYear < MIN_YEAR) {
            throw new IllegalArgumentException("min year end must > " + MIN_YEAR);
        }

        mMinYear = minYear;
        mMaxYear = maxYear;

        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fragment的生命周期
     */
    // onCreate在onAttach之后执行, 故此时Fragment已经关联到Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // 关联的Activity
        Activity activity = getActivity();
        // 隐藏软键盘
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // 振动器
        mVibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if (bundle != null) {
            mCalendar.set(Calendar.YEAR, bundle.getInt(KEY_SELECTED_YEAR));
            mCalendar.set(Calendar.MONTH, bundle.getInt(KEY_SELECTED_MONTH));
            mCalendar.set(Calendar.DAY_OF_MONTH, bundle.getInt(KEY_SELECTED_DAY));
            mVibrate = bundle.getBoolean(KEY_VIBRATE);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle bundle) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = layoutInflater.inflate(R.layout.date_picker_dialog, null);

        Activity activity = getActivity();
        mYearPickerView = new YearPickerView(activity, this);
        mDayPickerView = new DayPickerView(activity, this);

        mCalendarView = (LinearLayout) view.findViewById(R.id.animator);
        mCalendarView.addView(mYearPickerView);
        mCalendarView.addView(mDayPickerView);

        mBtnOk = (Button) view.findViewById(R.id.done);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneButtonClick();
            }
        });

        return view;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    public void onClick(View v) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    public int getFirstDayOfWeek() {
        return mWeekStart;
    }

    public int getMaxYear() {
        return mMaxYear;
    }

    public int getMinYear() {
        return mMinYear;
    }

    public SimpleMonthAdapter.CalendarDay getSelectedDay() {
        return new SimpleMonthAdapter.CalendarDay(mCalendar);
    }

    public void onDayOfMonthSelected(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        updatePickers();

    }

    public void onYearSelected(int year) {
        adjustDayInMonthIfNeeded(mCalendar.get(Calendar.MONTH), year);
        mCalendar.set(Calendar.YEAR, year);
        updatePickers();
        updateMonthOfView();
    }

    public void registerOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        mListeners.add(onDateChangedListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    static abstract interface OnDateChangedListener {
        public abstract void onDateChanged();
    }

    public static abstract interface OnDateSetListener {
        public abstract void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    private void adjustDayInMonthIfNeeded(int month, int year) {
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = Utils.getDaysInMonth(month, year);
        if (day > daysInMonth) {
            mCalendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
        }
    }

    private void updatePickers() {
        Iterator<OnDateChangedListener> iterator = mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDateChanged();
        }
    }

    private void updateMonthOfView() {
        mDayPickerView.onDateChanged();
    }

    private void onDoneButtonClick() {
        if (mCallBack != null) {
            mCallBack.onDateSet(this, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        dismiss();
    }
}
