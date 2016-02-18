package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.calendar.DatePickerDialog;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xiaobu1 on 15-4-28.
 */
public class MamRegister2Activity extends BaseHttpFragmentActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = MamRegister2Activity.class.getSimpleName();

    public static final String TAG_DATEPICKER = "date_picker";

    public static final int GENDER_BOY = 1;
    public static final int GENDER_GIRL = 2;

    /**
     * 默认男孩
     */
    private int gender = 1;

    private TextView mBoy;
    private TextView mGirl;

    private TextView mBirthday;

    /**
     * 当前的毫秒数
     */
    private long mBirthTime = 0;

    /**
     * 完成
     */
    private ImageButton mComplete;

    /**
     * 孩子状态
     */
    private int childStatus;

    private RelativeLayout babyGender;
    private LinearLayout babyGenderLayout;

    /**
     * 是否来自于设置界面
     */
    private boolean sourceSetting = false;

    /**
     * 初始化View
     */
    public void initViews() {
        setContentView(R.layout.activity_mam_register2);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        getIntentDatas();

        babyGender = (RelativeLayout) findViewById(R.id.baby_gender);
        babyGenderLayout = (LinearLayout) findViewById(R.id.baby_gender_layout);

        mBoy = (TextView) findViewById(R.id.boy);
        mGirl = (TextView) findViewById(R.id.girl);

        mBirthday = (TextView) findViewById(R.id.birthday);

        mComplete = (ImageButton) findViewById(R.id.complete);

        if (childStatus == MamRegister1Activity.CHILD_STATUS_PREGNANT) {
            babyGender.setVisibility(View.GONE);
            babyGenderLayout.setVisibility(View.GONE);
        } else {
            babyGender.setVisibility(View.VISIBLE);
            babyGenderLayout.setVisibility(View.VISIBLE);
        }

        initDatas();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getString(R.string.str_mam_basic_info));

        setBackClickListener(this);
    }

    private void getIntentDatas() {
        childStatus = getIntent().getIntExtra("child_status", MamRegister1Activity.CHILD_STATUS_CHILD);
        sourceSetting = getIntent().getBooleanExtra("source_setting", false);
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childStatus == MamRegister1Activity.CHILD_STATUS_CHILD) {
                    datePickerDialog.setYearRange(1985, 2028);
                } else {
                    datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), 2028);
                }
                datePickerDialog.show(getSupportFragmentManager(), TAG_DATEPICKER);
            }
        });

        mBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoy.setEnabled(false);
                mGirl.setEnabled(true);
                gender = GENDER_BOY;
            }
        });

        mGirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoy.setEnabled(true);
                mGirl.setEnabled(false);
                gender = GENDER_GIRL;
            }
        });

        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sourceSetting) {
                    Intent intent = new Intent();
                    if (childStatus == MamRegister1Activity.CHILD_STATUS_PREGNANT) {
                        intent.putExtra("gender", 0);
                    } else {
                        intent.putExtra("gender", gender);
                    }
                    if (mBirthTime > 0) {
                        intent.putExtra("birth_time", String.valueOf(mBirthTime));
                    } else {
                        Toast.makeText(MamRegister2Activity.this, "请选择出生日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (mBirthTime > 0) {
                        mEventLogic.setBabyInfo(String.valueOf(gender), String.valueOf(mBirthTime));
                    } else {
                        Toast.makeText(MamRegister2Activity.this, "请选择出生日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        // 在这里获得选择的年月日
        Log.d(TAG, "year : " + year + ", month : " + month + ", day : " + day);

        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("-");
        sb.append(month + 1);
        sb.append("-");
        sb.append(day);

        if (!StringUtil.isEmpty(sb.toString())) {
            mComplete.setImageResource(R.drawable.btn_login);
        } else {
            mComplete.setImageResource(R.mipmap.btn_login_unpress);
        }

        mBirthday.setText(sb.toString());

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(sb.toString());
            mBirthTime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.set_baby_info:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    if (status == 0) {
                        AppActivityManager.getInstance().popAllActivity();

                        Intent intent = new Intent(MamRegister2Activity.this, HuaYoungActivity.class);
                        startActivity(intent);
                    } else {
                        showToast(message);
                    }
                }
                break;
            default:
                break;
        }
    }
}
