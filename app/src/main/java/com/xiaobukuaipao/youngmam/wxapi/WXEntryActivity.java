package com.xiaobukuaipao.youngmam.wxapi;

import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * Created by xiaobu1 on 15-5-28.
 */
/*public class WXEntryActivity extends BaseHttpFragmentActivity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();

    private IWXAPI weChatApi;

    public void initViews() {
        weChatApi = WXAPIFactory.createWXAPI(this, GlobalConstants.WECHAT_APPID, false);
        weChatApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        weChatApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Bundle bundle = new Bundle();
        Log.d(TAG, "on resp");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                resp.toBundle(bundle);

                Resp sp = new Resp(bundle);

                String code = sp.code;

                Log.d(TAG, "code :" + code);
                // 此时, 获得微信返回的code, 执行登录请求
                if (mEventLogic == null) {
                    mEventLogic = new YoungEventLogic(this);

                    mEventLogic.weixinLogin(code);
                } else {
                    mEventLogic.weixinLogin(code);
                }
                showProgress(getString(R.string.logining));
                break;
            default:
                break;
        }
    }

    */
/**
 * 网络数据返回处理
 * @param msg
 */
/*
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.weixin_login:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;

                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 此时说明注册成功,这时候会下发Cookie
                        checkCookie(httpResult.getHeaders());
                        // 此时,Cookie存入数据库
                        // 进入主页面的个人主页
                        AppActivityManager.getInstance().popAllActivity();
                        Intent intent = new Intent(WXEntryActivity.this, HuaYoungActivity.class);
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

    */
/**
 * 检测Cookie
 * @param headers
 */
/*
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

            */
/**
 * 保存uid
 */
/*
            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            sp.edit().putLong(SplashActivity.UID, Long.valueOf(customHeaders
                    .get(GlobalConstants.COOKIE_UID))).commit();

            */

/**
             * 创建或者打开数据库
             *//*
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
}*/

public class WXEntryActivity extends WXCallbackActivity {

}
