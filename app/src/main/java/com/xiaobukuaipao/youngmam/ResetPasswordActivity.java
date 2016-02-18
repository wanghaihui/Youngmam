package com.xiaobukuaipao.youngmam;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.form.FormEditText;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-4-28.
 */
public class ResetPasswordActivity extends BaseHttpFragmentActivity {
    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    /**
     * 注册基本信息
     */
    private FormEditText mPhone;
    private FormEditText mPswd;
    private FormEditText mVcode;
    private ImageButton reset;

    private TextView mGetVerifyCode;

    // 60秒倒计时
    private int mTime = 60;
    private Handler timerHandler = new Handler();
    private Runnable mTimerRunnable = new Runnable() {

        public void run() {
            mTime--;
            if (mTime > 0) {
                timerHandler.postDelayed(mTimerRunnable, 1000);
                mGetVerifyCode.setText(getString(R.string.retry_send_num,mTime));
            } else {
                mGetVerifyCode.setText(ResetPasswordActivity.this.getResources().getString(R.string.retry_send));
                if (!mGetVerifyCode.isEnabled()) {
                    mGetVerifyCode.setEnabled(true);
                }
            }
        };

    };

    /**
     * 初始化View
     */
    public void initViews() {
        setContentView(R.layout.activity_reset_password);

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        mPhone = (FormEditText) findViewById(R.id.phone);
        mPhone.addTextChangedListener(resetPasswordWatcher);
        mPswd = (FormEditText) findViewById(R.id.pswd);
        mPswd.addTextChangedListener(resetPasswordWatcher);
        mVcode = (FormEditText) findViewById(R.id.verify_code);
        mVcode.addTextChangedListener(resetPasswordWatcher);

        reset = (ImageButton) findViewById(R.id.btn_reset_passowrd);

        mGetVerifyCode = (TextView) findViewById(R.id.get_verify_code);

        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (!StringUtil.isEmpty(sp.getString(RegisterAndLoginActivity.MOBILE, ""))) {
            mPhone.setText(sp.getString(RegisterAndLoginActivity.MOBILE, ""));
        } else {
            Log.d(TAG, "phone is null");
        }

        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getString(R.string.str_reset_password));

        setBackClickListener(this);
    }

    private void setUIListeners() {

        mGetVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhone.testValidity()) {
                    // 说明电话号码合法
                    mEventLogic.sendVcode(mPhone.getText().toString());
                }

                if (!StringUtil.isEmpty(mPhone.getText().toString())) {
                    if (mGetVerifyCode.isEnabled()) {
                        mGetVerifyCode.setEnabled(false);
                        // 执行发送验证码请求
                        timerHandler.post(mTimerRunnable);
                        mTime = 60;
                    }
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhone.testValidity() && mPswd.testValidity() && mVcode.testValidity()) {
                    // 此时说明电话号码合法,密码合法,验证码合法
                    mEventLogic.resetPswd(mPhone.getText().toString(), mPswd.getText().toString(),
                            mVcode.getText().toString());
                    showProgress(getString(R.string.reseting));
                }
            }
        });
    }

    private TextWatcher resetPasswordWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mPhone.getText().toString().length() == 11 &&
                    !StringUtil.isEmpty(mPswd.getText().toString()) && !StringUtil.isEmpty(mVcode.getText().toString())) {
                reset.setImageResource(R.drawable.btn_login);
            } else {
                reset.setImageResource(R.mipmap.btn_login_unpress);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {

            case R.id.send_vcode:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    if (status == 0) {
                        Log.d(TAG, "获取验证码成功");
                    } else {
                        showToast(message);
                    }
                }
                break;

            case R.id.reset_pswd:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    if (status == 0) {

                        finish();
                    } else if (status == 10) {
                        // 此手机号已注册
                        showToast(message + ", 请直接登录");

                    } else if (status == 2) {
                        // 服务器异常
                        showToast(message);
                    } else if (status == 15) {
                        showToast(message);
                    }
                }
                break;
            default:
                break;
        }
    }
}
