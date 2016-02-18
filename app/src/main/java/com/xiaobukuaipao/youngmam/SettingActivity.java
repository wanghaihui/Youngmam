package com.xiaobukuaipao.youngmam;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.database.MamaTable;
import com.xiaobukuaipao.youngmam.database.MultiDatabaseHelper;
import com.xiaobukuaipao.youngmam.domain.AvatarModifyEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.manager.YoungDatabaseManager;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.SDCardUtil;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.VersionUtil;
import com.xiaobukuaipao.youngmam.widget.ToggleButton;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-4-30.
 */
public class SettingActivity extends BaseHttpFragmentActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    public static final String PUSH_STATE = "push_state";

    public static final int REQUEST_BASIC_SETTING = 100;
    public static final int REQUEST_MARKET = 101;

    public static final int DEVICE_TYPE = 3;

    private RelativeLayout mMineLayout;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mState;

    private TextView mSystemNotify;
    private TextView mClearCache;
    private TextView mClearCacheSize;

    private TextView mResetPassword;

    private TextView mVersion;
    private ImageView mVersionMore;
    private TextView mFeedback;

    private TextView mContact;
    private TextView mPhone;

    private TextView mAboutUs;

    private Button logout;

    // 推送通知Toggle
    private ToggleButton pushToggle;

    private boolean isModifyAvatar = false;

    // 重置密码Layout
    private LinearLayout resetPasswordLayout;

    // 我的积分
    private LinearLayout mineCreditLayout;
    private TextView mineCredit;

    // 礼品商城
    private LinearLayout giftCenterLayout;
    private TextView giftCenter;

    // 我的礼品
    private LinearLayout mineGiftLayout;
    private TextView mineGift;

    // 收件地址
    private LinearLayout giftAddressLayout;
    private TextView giftAddress;

    // 邀请好友
    private LinearLayout inviteFriendsLayout;
    private TextView inviteFriends;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);
    // 分享的内容
    private String shareContent;
    // 分享的内容的链接
    private String targetUrl;
    // 分享的图片--活动或文章或主题
    private String imageUrl;

    public void initViews() {
        setContentView(R.layout.activity_setting);
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);

        setYoungActionBar();

        mMineLayout = (RelativeLayout) findViewById(R.id.mine_basic_layout);

        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mState = (TextView) findViewById(R.id.state);

        mineCreditLayout = (LinearLayout) findViewById(R.id.mine_credit_layout);
        mineCredit = (TextView) (findViewById(R.id.mine_credit).findViewById(R.id.title));

        inviteFriendsLayout = (LinearLayout) findViewById(R.id.invite_friends_layout);
        inviteFriends = (TextView) (findViewById(R.id.invite_friends).findViewById(R.id.title));

        giftCenterLayout = (LinearLayout) findViewById(R.id.gift_center_layout);
        giftCenter = (TextView) (findViewById(R.id.gift_center).findViewById(R.id.title));

        mineGiftLayout = (LinearLayout) findViewById(R.id.mine_gift_layout);
        mineGift = (TextView) (findViewById(R.id.mine_gift).findViewById(R.id.title));

        giftAddressLayout = (LinearLayout) findViewById(R.id.gift_address_layout);
        giftAddress = (TextView) (findViewById(R.id.gift_address).findViewById(R.id.title));

        mSystemNotify = (TextView) (findViewById(R.id.system_notify).findViewById(R.id.title));

        //  缓存问题
        mClearCache = (TextView) (findViewById(R.id.clear_cache).findViewById(R.id.clear_cache_title));
        mClearCacheSize = (TextView) (findViewById(R.id.clear_cache).findViewById(R.id.cache_size));

        resetPasswordLayout = (LinearLayout) findViewById(R.id.reset_password_layout);
        mResetPassword = (TextView) (findViewById(R.id.reset_password).findViewById(R.id.title));

        mVersion = (TextView) (findViewById(R.id.version).findViewById(R.id.title));
        mVersionMore = (ImageView) (findViewById(R.id.version).findViewById(R.id.more));
        mFeedback = (TextView) (findViewById(R.id.feedback).findViewById(R.id.title));

        mContact = (TextView) (findViewById(R.id.contact).findViewById(R.id.title));
        mPhone = (TextView) (findViewById(R.id.contact).findViewById(R.id.content));

        mAboutUs = (TextView) (findViewById(R.id.about_us).findViewById(R.id.title));

        logout = (Button) findViewById(R.id.logout);

        pushToggle = (ToggleButton) findViewById(R.id.push_notify);

        // 设置数据
        initDatas();

        // 配置需要分享的相关平台
        configPlatforms(mController);

        setUIListeners();
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_setting));

        setBackClickListener(this);
    }

    private void checkIn() {
        mEventLogic.checkIn();
    }

    private void initDatas() {

        addUserInfo();

        mineCredit.setText(getResources().getString(R.string.str_mine_credit));
        inviteFriends.setText(getResources().getString(R.string.str_invite_friends));

        giftCenter.setText(getResources().getString(R.string.str_gift_center));
        mineGift.setText(getResources().getString(R.string.str_mine_gift));
        giftAddress.setText(getResources().getString(R.string.str_gift_address));

        mSystemNotify.setText(getResources().getString(R.string.str_system_notify));

        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);

        if (sp.getBoolean(PUSH_STATE, true)) {
            pushToggle.setToggleOn();
        } else {
            pushToggle.setToggleOff();
        }


        mClearCache.setText(getString(R.string.str_clear_cache));
        try {
            mClearCacheSize.setText(SDCardUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String loginType = YoungDatabaseManager.getInstance().getLoginType();
        if (!StringUtil.isEmpty(loginType)) {
            if (loginType.equals("mobile")) {
                resetPasswordLayout.setVisibility(View.VISIBLE);
                mResetPassword.setText(getString(R.string.str_reset_password));
            } else {
                resetPasswordLayout.setVisibility(View.GONE);
            }
        } else {
            resetPasswordLayout.setVisibility(View.GONE);
        }

        Log.d(TAG, "version code : " + VersionUtil.getAppVersion(this));
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.str_version));
        sb.append(VersionUtil.getVersionName(this));
        mVersion.setText(sb.toString());

        mVersionMore.setVisibility(View.GONE);
        mFeedback.setText(getString(R.string.str_feedback));

        mContact.setText(getString(R.string.str_contact));
        mPhone.setText(getResources().getString(R.string.str_contact_phone));

        mAboutUs.setText(getString(R.string.str_about_us));
    }

    private void addUserInfo() {
        Cursor cursor = getContentResolver().query(YoungContentProvider.MAMA_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String headUrl = cursor.getString(cursor.getColumnIndex(MamaTable.COLUMN_HEAD_URL));
            String name = cursor.getString(cursor.getColumnIndex(MamaTable.COLUMN_NAME));
            int childStatus = cursor.getInt(cursor.getColumnIndex(MamaTable.COLUMN_CHILD_STATUS));

            Picasso.with(this)
                    .load(headUrl)
                    .placeholder(R.drawable.mam_default_avatar)
                    .into(mAvatar);

            mName.setText(name);

            switch (childStatus) {
                case 1:
                    mState.setText(getResources().getString(R.string.str_prepare));
                    break;
                case 2:
                    mState.setText(getResources().getString(R.string.str_gravida));
                    break;
                case 3:
                    mState.setText(getResources().getString(R.string.str_hava_baby));
                    break;
                default:
                    mState.setText(getResources().getString(R.string.str_hava_baby));
                    break;
            }

            cursor.close();
        }
    }

    private void setUIListeners() {
        mMineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, BabySettingActivity.class);
                startActivityForResult(intent, REQUEST_BASIC_SETTING);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        mineCreditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MineCreditActivity.class);
                startActivity(intent);
            }
        });

        inviteFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerShare();
            }
        });

        giftCenterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, GiftCenterActivity.class);
                startActivity(intent);
            }
        });

        mineGiftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MineGiftActivity.class);
                startActivity(intent);
            }
        });

        giftAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, GiftAddressActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 关于我们
         */
        findViewById(R.id.about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 重置密码
         */
        findViewById(R.id.reset_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 反馈意见
         */
        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, FeedBackActivity.class);
                startActivity(intent);
            }
        });

        pushToggle.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                setPushToggle(on);
            }
        });

        /**
         * 清空缓存
         */
        findViewById(R.id.clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
            }
        });
    }

    /**
     * 分享注册
     */
    private void registerShare() {
        long userId = 0;
        Cursor cursor = getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndex(CookieTable.COLUMN_UID));
            cursor.close();
        }

        if (userId > 0) {
            targetUrl = GlobalConstants.SHARE_REGISTER + String.valueOf(userId);
            shareContent = getResources().getString(R.string.str_invite_register_content);

            // 分享活动
            YoungShareBoard shareBoard = new YoungShareBoard(this);
            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            setShareContent(mController, targetUrl, shareContent, null, BaseHttpFragmentActivity.SHARE_TYPE_REGISTER);

            /**
             * 针对微博
             */
            mController.setShareContent(StringUtil.buildWeiboShareActivity(shareContent, targetUrl));
            mController.setShareMedia(new UMImage(this, R.mipmap.share_huayoung));
        }
    }

    /**
     * 清空应用缓存
     */
    private void clearCache() {
        // 首先清除缓存
        SDCardUtil.clearAllCache(this);

        try {
            mClearCacheSize.setText(SDCardUtil.getTotalCacheSize(this));
            Toast.makeText(this, "缓存清除了哦~", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPushToggle(boolean on) {
        Log.d(TAG, "toggle :" + on);
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        /**
         * 此时,也要保存此用户的推送通知状态,默认是true,表示系统可以推送通知给用户
         */
        sp.edit().putBoolean(SettingActivity.PUSH_STATE, on).commit();
    }

    /**
     * 选择应用市场评分
     */
    private void openMarket() {

        // 首先判断手机是否安装市场软件
        if (VersionUtil.hasAnyMarketInstalled(this)) {
            try {
                String str = "market://details?id=" + getPackageName();
                Intent localIntent = new Intent("android.intent.action.VIEW");
                localIntent.setData(Uri.parse(str));
                startActivityForResult(localIntent, REQUEST_MARKET);
            } catch (android.content.ActivityNotFoundException anfe) {
                anfe.printStackTrace();
            }
        } else {
            Toast.makeText(this, "不存在Android应用市场哦~~先去装一个吧", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        mEventLogic.logout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BASIC_SETTING && resultCode == RESULT_OK) {
            if (data != null) {
                boolean success = data.getBooleanExtra("success", false);
                if (success) {
                    SharedPreferences sp = this.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                    if (sp.getLong(SplashActivity.UID, 0) > 0) {
                        /**
                         * 得到用户的基本信息--无论数据库是否写, 每次进来都进行信息的获取
                         */
                        mEventLogic.getUserBase(String.valueOf(sp.getLong(SplashActivity.UID, 0)));
                    }

                    isModifyAvatar = true;
                }
            }
        } else {
            if (requestCode == REQUEST_MARKET) {
                // 在这里执行增加积分请求
                // 3--Android
                mEventLogic.appPraiseBonus(String.valueOf(DEVICE_TYPE), VersionUtil.getVersionName(this));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_user_base:
                if (checkResponse(msg)) {
                    /*HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (isModifyAvatar) {
                        EventBus.getDefault().post(new AvatarModifyEvent(true));
                    }

                    String userId = jsonObject.getString(GlobalConstants.JSON_USERID);
                    String headUrl = jsonObject.getString(GlobalConstants.JSON_HEADURL);
                    String name = jsonObject.getString(GlobalConstants.JSON_NAME);
                    int state = jsonObject.getInteger(GlobalConstants.JSON_CHILDSTATUS);

                    insertToDatabase(Long.valueOf(userId), headUrl, name, state);



                    Glide.with(this)
                            .load(headUrl)
                            .placeholder(R.drawable.mam_default_avatar)
                            .centerCrop()
                            .into(mAvatar);

                    mName.setText(name);

                    switch (state) {
                        case 1:
                            mState.setText(getResources().getString(R.string.str_prepare));
                            break;
                        case 2:
                            mState.setText(getResources().getString(R.string.str_gravida));
                            break;
                        case 3:
                            mState.setText(getResources().getString(R.string.str_hava_baby));
                            break;
                        default:
                            mState.setText(getResources().getString(R.string.str_hava_baby));
                            break;
                    }*/

                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObjectAll = JSONObject.parseObject(httpResult.getData());

                    if (jsonObjectAll != null) {

                        if (isModifyAvatar) {
                            EventBus.getDefault().post(new AvatarModifyEvent(true));
                        }

                        if (jsonObjectAll.containsKey(GlobalConstants.JSON_USERBASE)) {
                            JSONObject jsonObject = jsonObjectAll.getJSONObject(GlobalConstants.JSON_USERBASE);

                            String userId = "";
                            if (jsonObject.containsKey(GlobalConstants.JSON_USERID)) {
                                userId = jsonObject.getString(GlobalConstants.JSON_USERID);
                            }

                            String headUrl = "";
                            if (jsonObject.containsKey(GlobalConstants.JSON_HEADURL)) {
                                headUrl = jsonObject.getString(GlobalConstants.JSON_HEADURL);
                            }

                            String name = "";
                            if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
                                name = jsonObject.getString(GlobalConstants.JSON_NAME);
                            }
                            int state = 0;
                            if (jsonObject.containsKey(GlobalConstants.JSON_CHILDSTATUS)) {
                                state = jsonObject.getInteger(GlobalConstants.JSON_CHILDSTATUS);
                            }

                            int expertType = 0;
                            if (jsonObject.containsKey(GlobalConstants.JSON_EXPERTTYPE)) {
                                expertType = jsonObject.getInteger(GlobalConstants.JSON_EXPERTTYPE);
                            }

                            String expertName = "";
                            if (jsonObject.containsKey(GlobalConstants.JSON_EXPERTNAME)) {
                                expertName = jsonObject.getString(GlobalConstants.JSON_EXPERTNAME);
                            }

                            /**
                             * 插入前判断数据库中是否已经存在数据
                             */
                            insertToDatabase(Long.valueOf(userId), headUrl, name, state, expertType, expertName);

                            Picasso.with(this)
                                    .load(headUrl)
                                    .placeholder(R.drawable.mam_default_avatar)
                                    .fit()
                                    .centerCrop()
                                    .into(mAvatar);

                            mName.setText(name);

                            switch (state) {
                                case 1:
                                    mState.setText(getResources().getString(R.string.str_prepare));
                                    break;
                                case 2:
                                    mState.setText(getResources().getString(R.string.str_gravida));
                                    break;
                                case 3:
                                    mState.setText(getResources().getString(R.string.str_hava_baby));
                                    break;
                                default:
                                    mState.setText(getResources().getString(R.string.str_hava_baby));
                                    break;
                            }

                        }
                    }
                }
                break;

            case R.id.passport_logout:
                // 清除上个用户的我的发布,我的喜欢,我的评论,我的通知缓存
                YoungCache.get(this).remove(YoungCache.CACHE_MINE_PUBLISH);
                YoungCache.get(this).remove(YoungCache.CACHE_MINE_LIKE);
                YoungCache.get(this).remove(YoungCache.CACHE_MINE_COMMENT);
                YoungCache.get(this).remove(YoungCache.CACHE_MINE_NOTIFY);

                String loginType = YoungDatabaseManager.getInstance().getLoginType();
                if (loginType.equals("qq")) {
                    mController.deleteOauth(this, SHARE_MEDIA.QQ,
                        new SocializeListeners.SocializeClientListener() {

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onComplete(int status, SocializeEntity entity) {
                                if (status == 200) {
                                    Log.d(TAG, "QQ注销");
                                } else {

                                }
                            }
                        });
                } else if (loginType.equals("weixin")) {
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
                } else if (loginType.equals("weibo")) {
                    mController.deleteOauth(this, SHARE_MEDIA.SINA,
                            new SocializeListeners.SocializeClientListener() {

                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onComplete(int status, SocializeEntity entity) {
                                    if (status == 200) {
                                        Log.d(TAG, "微博注销");
                                    } else {

                                    }
                                }
                            });
                }

                // 清除Cookie
                YoungDatabaseManager.getInstance().clearCookie();

                // 关闭所有数据库
                MultiDatabaseHelper.getInstance().closeDatabase();

                // 清除uid
                SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                sp.edit().putLong(SplashActivity.UID, Long.valueOf(0)).commit();
                sp.edit().putBoolean(SettingActivity.PUSH_STATE, true).commit();
                sp.edit().putString(RegisterAndLoginActivity.MOBILE, "").commit();

                // 关闭所有Activity
                AppActivityManager.getInstance().popAllActivity();

                // 跳到登录页
                Intent intent = new Intent(SettingActivity.this, HuaYoungActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(GlobalConstants.JSON_PAGE, 3);
                startActivity(intent);

                break;

            case R.id.app_praise_bonus:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                        showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                    }
                }
                break;

            case R.id.check_in:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                        showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                    } else {
                        Toast.makeText(this, jsonObject.getString(GlobalConstants.JSON_MSG), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    public synchronized void insertToDatabase(Long userId, String headUrl, String name, int state, int expertType, String expertName) {

        Cursor cursor = this.getContentResolver().query(YoungContentProvider.MAMA_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            this.getContentResolver().delete(YoungContentProvider.MAMA_CONTENT_URI, null, null);
            // 此时, 存在数据
            cursor.close();
        }

        // 此时, 不存在数据
        ContentValues values = new ContentValues();
        values.put(MamaTable.COLUMN_USER_ID, userId);
        values.put(MamaTable.COLUMN_HEAD_URL, headUrl);
        values.put(MamaTable.COLUMN_NAME, name);
        values.put(MamaTable.COLUMN_CHILD_STATUS, state);
        values.put(MamaTable.COLUMN_EXPERT_TYPE, expertType);
        values.put(MamaTable.COLUMN_EXPERT_NAME, expertName);

        // 插入数据库
        this.getContentResolver().insert(YoungContentProvider.MAMA_CONTENT_URI, values);

    }
}
