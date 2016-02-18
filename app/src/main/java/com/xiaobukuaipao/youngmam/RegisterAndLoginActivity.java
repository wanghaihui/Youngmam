package com.xiaobukuaipao.youngmam;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.database.MultiDatabaseHelper;
import com.xiaobukuaipao.youngmam.form.FormEditText;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiaobu1 on 15-4-28.
 */
public class RegisterAndLoginActivity extends BaseHttpFragmentActivity {

    private static final String TAG = RegisterAndLoginActivity.class.getSimpleName();

    public static final String MOBILE = "mobile";

    private TextView loginTextView;
    private ImageView loginGirl;
    private ImageButton btnLogin;
    private LinearLayout mLoginTable;

    private TextView registerTextView;
    private ImageView registerGirl;
    private ImageButton btnRegister;
    private LinearLayout mRegisterTable;

    // 忘记密码
    private TextView mForgetPswd;

    /**
     * 注册基本信息
     */
    private FormEditText mRegisterPhone;
    private FormEditText mRegisterPswd;
    private FormEditText mRegisterVcode;
    private TextView mVerifyCode;

    /**
     * 登录基本信息
     */
    private FormEditText mLoginPhone;
    private FormEditText mLoginPswd;

    // 微信登录
    private ImageButton weChat;

    // private IWXAPI api;

    // 微博登录
    private ImageButton weiBo;

    /*private WeiboAuth mWeiboAuth;
    private Oauth2AccessToken mAccessToken;
    // 注意:SsoHandler仅当SDK支持SSO时有效
    private SsoHandler mSsoHandler;*/

    // QQ登录
    private ImageButton qq;

    //private Tencent tencent;

    /**
     * 终极解决方案--借助友盟实现第三方登录
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    /**
     * 返回按钮
     */
    private ImageButton mBackLogin;

    /**
     * 是否安装微信
     */
    private boolean isWeixinInstall = false;
    /**
     * 是否安装QQ
     */
    private boolean isQQInstall = false;

    // 60秒倒计时
    private int mTime = 60;
    private Handler timerHandler = new Handler();
    private Runnable mTimerRunnable = new Runnable() {

        public void run() {
            mTime--;
            if (mTime > 0) {
                timerHandler.postDelayed(mTimerRunnable, 1000);
                mVerifyCode.setText(getString(R.string.retry_send_num,mTime));
            } else {
                mVerifyCode.setText(RegisterAndLoginActivity.this.getResources().getString(R.string.retry_send));
                if (!mVerifyCode.isEnabled()) {
                    mVerifyCode.setEnabled(true);
                }
            }
        };

    };

    /**
     * 初始化View
     */
    public void initViews() {

        setContentView(R.layout.activity_register_and_login);

        loginTextView = (TextView) findViewById(R.id.login_text_view);
        registerTextView = (TextView) findViewById(R.id.register_text_view);

        loginGirl = (ImageView) findViewById(R.id.login_girl);
        registerGirl = (ImageView) findViewById(R.id.register_girl);

        btnLogin = (ImageButton) findViewById(R.id.btn_login);
        btnRegister = (ImageButton) findViewById(R.id.btn_register);

        mLoginTable = (LinearLayout) findViewById(R.id.login_edit_table);
        mRegisterTable = (LinearLayout) findViewById(R.id.register_edit_table);

        mForgetPswd = (TextView) findViewById(R.id.forget_pswd);

        mRegisterPhone = (FormEditText) findViewById(R.id.register_phone);
        mRegisterPhone.addTextChangedListener(registerTextWatcher);
        mRegisterPswd = (FormEditText) findViewById(R.id.register_pswd);
        mRegisterPswd.addTextChangedListener(registerTextWatcher);
        mRegisterVcode = (FormEditText) findViewById(R.id.verify_code);
        mRegisterVcode.addTextChangedListener(registerTextWatcher);

        mLoginPhone = (FormEditText) findViewById(R.id.login_phone);
        mLoginPhone.addTextChangedListener(loginTextWatcher);
        mLoginPswd = (FormEditText) findViewById(R.id.login_pswd);
        mLoginPswd.addTextChangedListener(loginTextWatcher);

        mVerifyCode = (TextView) findViewById(R.id.get_verify_code);

        weChat = (ImageButton) findViewById(R.id.btn_wechat);
        weiBo = (ImageButton) findViewById(R.id.btn_weibo);
        qq = (ImageButton) findViewById(R.id.btn_qq);

        mBackLogin = (ImageButton) findViewById(R.id.back_login_btn);

        /**
         * 微信Api
         */
        /*api = WXAPIFactory.createWXAPI(this, GlobalConstants.WECHAT_APPID, true);
        api.registerApp(GlobalConstants.WECHAT_APPID);*/
        UMWXHandler umwxHandler = new UMWXHandler(this, GlobalConstants.WECHAT_APPID, GlobalConstants.WECHAT_APPSECRET);
        umwxHandler.addToSocialSDK();
        umwxHandler.setRefreshTokenAvailable(false);

        isWeixinInstall = umwxHandler.isClientInstalled();

        /**
         * 微博Api
         */
        /*mWeiboAuth = new WeiboAuth(this, GlobalConstants.WEIBO_APP_KEY, GlobalConstants.WEIBO_REDIRECT_URL,
                GlobalConstants.WEIBO_SCOPE);*/
        // 设置新浪微博SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSinaCallbackUrl(GlobalConstants.WEIBO_REDIRECT_URL);

        /**
         * QQ登录
         */
        //tencent = Tencent.createInstance(GlobalConstants.QQ_APPID, this);
        UMQQSsoHandler umqqSsoHandler = new UMQQSsoHandler(this, GlobalConstants.QQ_APPID, GlobalConstants.QQ_APPKEY);
        umqqSsoHandler.addToSocialSDK();

        isQQInstall = umqqSsoHandler.isClientInstalled();

        setUIListeners();

    }

    /**
     * Login文字监听器
     */
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mLoginPhone.getText().toString().length() == 11 && !StringUtil.isEmpty(mLoginPswd.getText().toString())) {
                btnLogin.setImageResource(R.drawable.btn_login);
            } else {
                btnLogin.setImageResource(R.mipmap.btn_login_unpress);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 注册文字监听器
     */
    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mRegisterPhone.getText().toString().length() == 11 && !StringUtil.isEmpty(mRegisterPswd.getText().toString())
                    && !StringUtil.isEmpty(mRegisterVcode.getText().toString())) {
                btnRegister.setImageResource(R.drawable.btn_register);
            } else {
                btnRegister.setImageResource(R.mipmap.btn_register_unpress);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 设置监听器
     */
    private void setUIListeners() {

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginTextView.setEnabled(false);
                registerTextView.setEnabled(true);
                loginGirl.setVisibility(View.VISIBLE);
                registerGirl.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                btnRegister.setVisibility(View.GONE);
                mLoginTable.setVisibility(View.VISIBLE);
                mRegisterTable.setVisibility(View.GONE);
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTextView.setEnabled(false);
                loginTextView.setEnabled(true);
                registerGirl.setVisibility(View.VISIBLE);
                loginGirl.setVisibility(View.INVISIBLE);
                btnRegister.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);
                mRegisterTable.setVisibility(View.VISIBLE);
                mLoginTable.setVisibility(View.GONE);
            }
        });

        mForgetPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAndLoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(RegisterAndLoginActivity.this, HuaYoungActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(RegisterAndLoginActivity.this, MamRegister1Activity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(RegisterAndLoginActivity.this, "doPhoneRegisterClicked");

                if (mRegisterPhone.testValidity() && mRegisterPswd.testValidity() && mRegisterVcode.testValidity()) {
                    // 此时说明电话号码合法,密码合法,验证码合法
                    mEventLogic.phoneRegister(mRegisterPhone.getText().toString(), mRegisterPswd.getText().toString(),
                            mRegisterVcode.getText().toString());
                    showProgress(getString(R.string.loading));
                }
             }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoginPhone.testValidity() && mLoginPswd.testValidity()) {
                    // 此时说明电话号码,密码合法
                    mEventLogic.login(mLoginPhone.getText().toString(), mLoginPswd.getText().toString());
                    showProgress(getString(R.string.logining));
                }
            }
        });

        /**
         * 获取验证码
         */
        mVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRegisterPhone.testValidity()) {
                    // 说明电话号码合法
                    mEventLogic.sendVcode(mRegisterPhone.getText().toString());
                } else {
                    Log.d(TAG, "phone :" + mRegisterPhone.getText().toString());
                }

                if (!StringUtil.isEmpty(mRegisterPhone.getText().toString())) {
                    if (mVerifyCode.isEnabled()) {
                        mVerifyCode.setEnabled(false);
                        // 执行发送验证码请求
                        timerHandler.post(mTimerRunnable);
                        mTime = 60;
                    }
                }
            }
        });

        /**
         * 微信登录
         */
        weChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!api.isWXAppInstalled()) {
                    //提醒用户没有按照微信
                    Toast.makeText(RegisterAndLoginActivity.this, "请先安装微信", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                // 拉取微信到授权登录界面
                // sendAuth();

                MobclickAgent.onEvent(RegisterAndLoginActivity.this, "doWeixinRegisterClicked");

                // 首先判断是否安装了微信
                deleteOauth();

                if (isWeixinInstall) {
                    wechatLogin();
                } else {
                    Toast.makeText(RegisterAndLoginActivity.this, "请先安装微信", Toast.LENGTH_SHORT).show();
                }
            }
        });



        /**
         * 微博登录
         */
        weiBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mSsoHandler = new SsoHandler(RegisterAndLoginActivity.this, mWeiboAuth);
                mSsoHandler.authorize(new AuthListener());*/
                MobclickAgent.onEvent(RegisterAndLoginActivity.this, "doWeiboRegisterClicked");
                weiboAuthorize();
            }
        });

        /**
         * qq登录
         */
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isQQInstall) {
                    MobclickAgent.onEvent(RegisterAndLoginActivity.this, "doQQRegisterClicked");
                    qqLogin();
                } else {
                    Toast.makeText(RegisterAndLoginActivity.this, "请先安装QQ", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void wechatLogin() {

        mController.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.d(TAG, "oauth verify start");
            }

            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                Log.d(TAG, "oauth verify complete");

                mController.getPlatformInfo(RegisterAndLoginActivity.this, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> stringObjectMap) {
                        // 授权完成
                        if (status == 200 && stringObjectMap != null) {
                            /*Set<String> keys = stringObjectMap.keySet();
                            for(String key : keys) {
                                Log.d(TAG, "key :" + key);
                                Log.d(TAG, "value :" + stringObjectMap.get(key));
                            }*/

                            String openId = (String) stringObjectMap.get("openid");
                            String unionId = (String) stringObjectMap.get("unionid");
                            String nickName = (String) stringObjectMap.get("nickname");
                            String headUrl = (String) stringObjectMap.get("headimgurl");

                            showProgress(getString(R.string.logining));
                            mEventLogic.weixinLogin2(openId, unionId, nickName, headUrl);
                        }
                    }
                });
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }

    private void qqLogin() {
        /*if (!tencent.isSessionValid()) {
            // 此时,腾讯QQ是合法的
            tencent.login(this, GlobalConstants.QQ_SCOPE, listener);
        }*/

        mController.doOauthVerify(this, SHARE_MEDIA.QQ, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {

                final String openId = bundle.getString("openid");

                // 此时,授权完成
                mController.getPlatformInfo(RegisterAndLoginActivity.this, SHARE_MEDIA.QQ, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> stringObjectMap) {
                        if (status == 200 && stringObjectMap != null) {
                            Set<String> keys = stringObjectMap.keySet();
                            for(String key : keys) {
                                Log.d(TAG, "key: " + key);
                                Log.d(TAG, "value: " + stringObjectMap.get(key));
                            }

                            String nickName = (String) stringObjectMap.get("screen_name");
                            String headUrl = (String) stringObjectMap.get("profile_image_url");

                            mEventLogic.qqLogin(openId, nickName, headUrl);
                        }
                    }
                });
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }

    private void deleteOauth() {
        // 删除授权
        mController.deleteOauth(this, SHARE_MEDIA.WEIXIN,
            new SocializeListeners.SocializeClientListener() {

                @Override
                public void onStart() {
                }

                @Override
                public void onComplete(int status, SocializeEntity entity) {
                    if (status == 200) {
                        Log.d(TAG, "微信注销");

                    } else {

                    }
                }
            });
    }
    /*IUiListener listener = new BaseUiListener() {

        public void doComplete(JSONObject values) {
            final String openId = values.getString("openid");

            Log.d(TAG, "openId : " + openId);

            new Thread(){
                @Override
                public void run() {


                    try {
                        org.json.JSONObject json = tencent.request(Constants.GRAPH_BASE, null, Constants.HTTP_GET);

                        String nickName = json.getString("nickname");
                        Log.d(TAG, "nickName : " + nickName);
                        String headUrl = json.getString("figureurl_qq_2");
                        Log.d(TAG, "headUrl : " + headUrl);

                        mEventLogic.qqLogin(openId, nickName, headUrl);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    };*/

    /*private class BaseUiListener implements IUiListener {

        public void onComplete(Object response) {
            doComplete((org.json.JSONObject) response);
        }

        protected void doComplete(org.json.JSONObject values) {
        }

        @Override
        public void onError(UiError e) {
            Log.d("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel() {
            Log.d("onCancel", "");
        }
    }*/

    private void weiboAuthorize() {
        mController.doOauthVerify(this, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                if (bundle != null && !TextUtils.isEmpty(bundle.getString("uid"))) {
                    Toast.makeText(RegisterAndLoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                    getWeiboInfo();
                } else {
                    Toast.makeText(RegisterAndLoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }

    private void getWeiboInfo() {
        mController.getPlatformInfo(this, SHARE_MEDIA.SINA, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> stringObjectMap) {
                if (status == 200 && stringObjectMap != null) {
                    /*Set<String> keys = stringObjectMap.keySet();
                    for(String key : keys){
                        Log.d(TAG, "key :" + key);
                        Log.d(TAG, "value :" + stringObjectMap.get(key));
                    }*/
                    String uid = String.valueOf(stringObjectMap.get("uid"));
                    String accessToken = String.valueOf(stringObjectMap.get("access_token"));

                    if (!StringUtil.isEmpty(uid) && !StringUtil.isEmpty(accessToken)) {
                        showProgress(getString(R.string.logining));
                        mEventLogic.weiboLogin(uid, accessToken);
                    }
                }
            }
        });
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.RESULT_LOGIN) {
                Tencent.handleResultData(data, listener);
                Log.d(TAG, "-->onActivityResult handle logindata");
            }
        }*/

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        /*if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }*/

        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    /*class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {

                String uid = mAccessToken.getUid();
                String accessToken = mAccessToken.getToken();

                Log.d(TAG, "uid : " + uid);
                Log.d(TAG, "accessToken : " + accessToken);

                showProgress(getString(R.string.logining));
                mEventLogic.weiboLogin(uid, accessToken);
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(RegisterAndLoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(RegisterAndLoginActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(RegisterAndLoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }*/

    /**
     * 发送微信请求
     */
    private void sendAuth() {
        /*final SendAuth.Req req = new SendAuth.Req();
        req.scope="snsapi_userinfo";
        req.state="youngmam_weixin_login";
        api.sendReq(req);*/
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.phone_register:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    // 此时,先将电话号码保存在SharedPreference中
                    SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                    /**
                     * 此时,也要保存此用户的推送通知状态,默认是true,表示系统可以推送通知给用户
                     */
                    sp.edit().putString(MOBILE, mRegisterPhone.getText().toString()).commit();
                    Log.d(TAG, "mobile :" + sp.getString(MOBILE, ""));

                    if (status == 0) {
                        // 此时说明注册成功,这时候会下发Cookie
                        checkCookie(httpResult.getHeaders());
                        // 此时,Cookie存入数据库,进入下一个页面
                        Intent intent = new Intent(RegisterAndLoginActivity.this, MamRegister1Activity.class);
                        startActivity(intent);
                    } else if (status == 10) {
                        // 此手机号已注册
                        Toast.makeText(this, message + ", 请直接登录", Toast.LENGTH_SHORT).show();

                    } else if (status == 2) {
                        // 服务器异常
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.login:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                    // 此时,先将电话号码保存在SharedPreference中
                    SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                    /**
                     * 此时,也要保存此用户的推送通知状态,默认是true,表示系统可以推送通知给用户
                     */
                    sp.edit().putString(MOBILE, mLoginPhone.getText().toString()).commit();

                    Log.d(TAG, "mobile :" + sp.getString(MOBILE, ""));

                    if (status == 0) {
                        // 此时说明登录成功,这时候会下发Cookie
                        checkCookie(httpResult.getHeaders());
                        // 此时,Cookie存入数据库,进入下一个页面
                        AppActivityManager.getInstance().popAllActivity();
                        Intent intent = new Intent(RegisterAndLoginActivity.this, HuaYoungActivity.class);
                        intent.putExtra(GlobalConstants.JSON_PAGE, 3);
                        startActivity(intent);
                        finish();
                    } else if (status == 12) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

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

            case R.id.weibo_login:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 此时说明登录成功,这时候会下发Cookie
                        checkCookie(httpResult.getHeaders());
                        // 此时,Cookie存入数据库,进入下一个页面
                        AppActivityManager.getInstance().popAllActivity();
                        Intent intent = new Intent(RegisterAndLoginActivity.this, HuaYoungActivity.class);
                        // 3--mine页
                        intent.putExtra(GlobalConstants.JSON_PAGE, 3);
                        startActivity(intent);
                    }
                }
                break;

            case R.id.qq_login:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 此时说明登录成功,这时候会下发Cookie
                        checkCookie(httpResult.getHeaders());
                        // 此时,Cookie存入数据库,进入下一个页面
                        AppActivityManager.getInstance().popAllActivity();
                        Intent intent = new Intent(RegisterAndLoginActivity.this, HuaYoungActivity.class);
                        // 3--mine页
                        intent.putExtra(GlobalConstants.JSON_PAGE, 3);
                        startActivity(intent);
                    }
                }
                break;

            case R.id.weixin_login2:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 此时说明登录成功,这时候会下发Cookie
                        checkCookie(httpResult.getHeaders());
                        // 此时,Cookie存入数据库,进入下一个页面
                        AppActivityManager.getInstance().popAllActivity();
                        Intent intent = new Intent(RegisterAndLoginActivity.this, HuaYoungActivity.class);
                        // 3--mine页
                        intent.putExtra(GlobalConstants.JSON_PAGE, 3);
                        startActivity(intent);
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * 检测Cookie
      * @param headers
     */
    private void checkCookie(Map<String, String> headers) {

        if (headers.containsKey(GlobalConstants.SET_COOKIE_KEY)) {

            String cookie = headers.get(GlobalConstants.SET_COOKIE_KEY);

            Map<String, String> customHeaders = new HashMap<String, String>();

            String[] split = cookie.split("\n");

            // 四行--四个Cookie行--解析Cookie
            for (String sc : split) {
                Log.d(TAG, " sc : " + sc);

                if (cookie.length() > 0) {
                    String[] splitCookie = sc.split(";");
                    for (int i = 0; i < splitCookie.length; i++) {
                        String[] entry = splitCookie[i].split("=");
                        if (entry.length == 1) {
                            // 当只有key时，value为null
                            customHeaders.put(entry[0], null);
                        } else {
                            customHeaders.put(entry[0], entry[1]);
                        }
                    }
                }
            }

            HuaYoungApplication.mCookie_T = customHeaders.get(GlobalConstants.COOKIE_T);

            /**
             * 保存uid
             */
            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            sp.edit().putLong(SplashActivity.UID, Long.valueOf(customHeaders
                    .get(GlobalConstants.COOKIE_UID))).commit();

            /**
             * 此时,也要保存此用户的推送通知状态,默认是true,表示系统可以推送通知给用户
             */
            sp.edit().putBoolean(SettingActivity.PUSH_STATE, true).commit();

            /**
             * 创建或者打开数据库
             */
            MultiDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(), customHeaders
                    .get(GlobalConstants.COOKIE_UID));

            // 将Cookie插入到数据库中
            insertToDatabase(Long.valueOf(customHeaders
                    .get(GlobalConstants.COOKIE_UID)), customHeaders.get(GlobalConstants.COOKIE_P),
                    customHeaders.get(GlobalConstants.COOKIE_LOGIN_TYPE));
        }
    }

    public synchronized void insertToDatabase(Long uid, String p,
                                              String login_type) {

        ContentValues values = new ContentValues();
        values.put(CookieTable.COLUMN_UID, uid);
        values.put(CookieTable.COLUMN_P, p);
        values.put(CookieTable.COLUMN_LOGIN_TYPE, login_type);

        // 此时先清空一下数据库，防止数据库已经存在Cookie
        Cursor cursor = getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            getContentResolver().delete(YoungContentProvider.COOKIE_CONTENT_URI, null, null);
            cursor.close();
        }

        // 插入数据库
        getContentResolver().insert(YoungContentProvider.COOKIE_CONTENT_URI,values);

    }


    @Override
    public void onBackPressed() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            AppActivityManager.getInstance().popAllActivity();
            Intent intent = new Intent(RegisterAndLoginActivity.this, HuaYoungActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 3--mine页
            intent.putExtra(GlobalConstants.JSON_PAGE, 3);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

}
