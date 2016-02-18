package com.xiaobukuaipao.youngmam;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.greenrobot.event.EventBus;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;
import com.xiaobukuaipao.youngmam.adapter.MainFragmentPagerAdapter;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.database.MultiDatabaseHelper;
import com.xiaobukuaipao.youngmam.domain.BannerActivity;
import com.xiaobukuaipao.youngmam.domain.DbHelperEvent;
import com.xiaobukuaipao.youngmam.domain.FragmentDeliverEvent;
import com.xiaobukuaipao.youngmam.domain.NotifyIndicatorEvent;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.imagechooser.ImageChooserActivity;
import com.xiaobukuaipao.youngmam.message.YoungMessage;
import com.xiaobukuaipao.youngmam.notification.BaiduPushEvent;
import com.xiaobukuaipao.youngmam.utils.DeviceUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.HorizontalDisableViewPager;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;


public class HuaYoungActivity extends BaseHttpFragmentActivity implements ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private static final String TAG = HuaYoungActivity.class.getSimpleName();

    // 主要显示的滑动页面
    private HorizontalDisableViewPager viewPager;

    // 页面适配器
    private MainFragmentPagerAdapter mPagerAdapter;

    // 底部Layout
    private LinearLayout mMainTab;

    // 中间的编辑键
    private ImageButton mEditBtn;

    // 刚进来时,跳到第几页
    private int curPage = 0;
    private String message;

    // EventBus
    private EventBus eventBus;

    private TextView mNotifyIndicator;

    // 缓存
    private YoungCache youngCache;

    /**
     * 记录标记时间
     */
    private long exitTime = 0;

    /**
     * Notificaiton清除
     */
    private NotificationManager notificationManager;

    private RelativeLayout selfActionBar;
    private FrameLayout rightBar;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_youngmam);

        selfActionBar = (RelativeLayout) findViewById(R.id.action_bar);
        rightBar = (FrameLayout) findViewById(R.id.right_layout);

        getIntentDatas();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        youngCache = YoungCache.get(this);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 清空通知
        notificationManager.cancel(YoungMessage.MSG_TYPE_NEW_FANS_APPLY);
        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_COMMON_MSG);
        notificationManager.cancel(YoungMessage.COMMON_MSG);

        notificationManager.cancel(YoungMessage.MSG_TYPE_ARTICLE_COMMENT);
        notificationManager.cancel(YoungMessage.MSG_TYPE_ARTICLE_COMMENT_REPLY);
        notificationManager.cancel(YoungMessage.MSG_TYPE_QUESTION_COMMENT);
        notificationManager.cancel(YoungMessage.MSG_TYPE_QUESTION_COMMENT_REPLY);
        notificationManager.cancel(YoungMessage.MSG_TYPE_SPECIAL_COMMENT_REPLY);
        notificationManager.cancel(YoungMessage.MSG_TYPE_H5_WEBPAGE_COMMENT_REPLY);

        notificationManager.cancel(YoungMessage.MSG_TYPE_ARTICLE_LIKE);
        notificationManager.cancel(YoungMessage.MSG_TYPE_ARTICLE_COMMENT_LIKE);
        notificationManager.cancel(YoungMessage.MSG_TYPE_QUESTION_COMMENT_LIKE);
        notificationManager.cancel(YoungMessage.MSG_TYPE_SPECIAL_COMMENT_LIKE);
        notificationManager.cancel(YoungMessage.MSG_TYPE_H5_WEBPAGE_COMMENT_LIKE);

        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_ACTIVITY_ACTIVE);
        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_SPECIAL_PUBLISH);
        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_WEB_ACTIVITY_ACTIVE);
        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_WEBPAGE_PUBLISH);
        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_URL_PUBLISH);
        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_THEME_PUBLISH);
        notificationManager.cancel(YoungMessage.NOTIFY_TYPE_ARTICLE_PUBLISH);

        viewPager = (HorizontalDisableViewPager) findViewById(R.id.main_pager);
        mMainTab = (LinearLayout) findViewById(R.id.main_bottom_tab);

        mNotifyIndicator = (TextView) findViewById(R.id.notify_indicator);
        mNotifyIndicator.setVisibility(View.GONE);

        // 设置底部监听器
        for (int i=0; i < mMainTab.getChildCount(); i++) {
            if (i != mMainTab.getChildCount() / 2) {
                mMainTab.getChildAt(i).setOnClickListener(this);
            }
        }

        mPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setCanScroll(true);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(mPagerAdapter);

        viewPager.setCurrentItem(curPage);

        if (curPage > 1) {
            mMainTab.getChildAt(curPage + 1).setEnabled(false);
            mMainTab.getChildAt(curPage + 1).setSelected(true);
        } else {
            mMainTab.getChildAt(curPage).setEnabled(false);
            mMainTab.getChildAt(curPage).setSelected(true);
        }

        if (curPage == 0 && !StringUtil.isEmpty(message)) {
            openDetailByMessageType(message);
        }

        mEditBtn = (ImageButton) findViewById(R.id.main_edit_btn);

        // 先获得百度云推送的userId和channelId
        startPushNotification();

        UmengUpdateAgent.update(this);

        setUIListeners();
    }

    private void setUIListeners() {
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishArticle();
            }
        });

        rightBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HuaYoungActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void publishArticle() {
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            Intent intent = new Intent(HuaYoungActivity.this, ImageChooserActivity.class);
            startActivity(intent);
        } else {
            // 跳到登录页
            Intent intent = new Intent(HuaYoungActivity.this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    private void getIntentDatas() {
        curPage = getIntent().getIntExtra(GlobalConstants.JSON_PAGE, 0);
        message = getIntent().getStringExtra("message");
    }

    private void startPushNotification() {
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(HuaYoungActivity.this, "api_key"));
    }

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        Log.d(TAG, "baidy api key : " + apiKey);
        return apiKey;
    }

    public void executeHttpRequest() {
        /**
         * 获取未读消息数
         */
        mEventLogic.getUnreadCount();
    }

    @Override
    public void onClick(View view) {
        for (int i=0; i < mMainTab.getChildCount(); i++) {
            if (i != mMainTab.getChildCount() / 2) {
                mMainTab.getChildAt(i).setEnabled(true);
                mMainTab.getChildAt(i).setSelected(false);
            }
        }

        for (int i=0; i < mMainTab.getChildCount(); i++) {
            if (i != mMainTab.getChildCount() / 2) {
                if (mMainTab.getChildAt(i) == view) {

                    // ViewPager跳转
                    if (i > mMainTab.getChildCount() / 2) {
                        viewPager.setCurrentItem(i - 1);
                    } else {
                        viewPager.setCurrentItem(i);
                    }

                    mMainTab.getChildAt(i).setEnabled(false);
                    mMainTab.getChildAt(i).setSelected(true);

                    if (i == mMainTab.getChildCount() - 2) {
                        if (mNotifyIndicator.getVisibility() == View.VISIBLE) {
                            mNotifyIndicator.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    /**
     * ViewPager页改变
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                selfActionBar.setVisibility(View.VISIBLE);
                // actionBar.setMiddleAction(YoungActionBar.Type.IMAGE, R.mipmap.young_logo, null);
                break;
            case 1:
                selfActionBar.setVisibility(View.GONE);
                // actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getString(R.string.str_find_fresh));
                break;
            case 2:
                selfActionBar.setVisibility(View.GONE);
                break;
            case 3:
                selfActionBar.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        for (int i=0; i < mMainTab.getChildCount(); i++) {
            if (i != mMainTab.getChildCount() / 2) {
                mMainTab.getChildAt(i).setEnabled(true);
                mMainTab.getChildAt(i).setSelected(false);
            }
        }

        if (position < mMainTab.getChildCount() / 2) {
            mMainTab.getChildAt(position).setEnabled(false);
            mMainTab.getChildAt(position).setSelected(true);
        } else {
            mMainTab.getChildAt(position + 1).setEnabled(false);
            mMainTab.getChildAt(position + 1).setSelected(true);
        }

    }
    //////////////////////////////////////////////////////////////////////////////

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.set_push_token:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        Log.d(TAG, "设置用户设备token成功");
                    }
                }
                break;

            case R.id.get_unread_count:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        int unreadCount = jsonObject.getInteger(GlobalConstants.JSON_NEWFANSCOUNT)
                                + jsonObject.getInteger(GlobalConstants.JSON_NEWCOMMENTCOUNT)
                                + jsonObject.getInteger(GlobalConstants.JSON_NEWLIKECOUNT)
                                + jsonObject.getInteger(GlobalConstants.JSON_NEWSYSNOTICECOUNT);

                        if (unreadCount > 0) {
                            mNotifyIndicator.setVisibility(View.VISIBLE);
                        } else {
                            mNotifyIndicator.setVisibility(View.GONE);
                        }

                        if (curPage == 2) {
                            mNotifyIndicator.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 处理预加载
     */
    public void onEvent(BaiduPushEvent baiduPushEvent) {
        String userId = baiduPushEvent.getUserId();
        String chanelId = baiduPushEvent.getChannelId();

        if (!StringUtil.isEmpty(userId) && !StringUtil.isEmpty(chanelId)) {
            DeviceUtil deviceUtil = new DeviceUtil(this);
            Log.d(TAG, "device id : " + deviceUtil.getCombinedId());
            mEventLogic.setPushToken(deviceUtil.getCombinedId(), null, userId, chanelId);
        }
    }

    /**
     * 处理数据库清空的清空
     */
    public void onEvent(DbHelperEvent dbHelperEvent) {
        // 如果此时是否存在用户登录
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            if (dbHelperEvent.getRelogin()) {
                Log.d(TAG, "数据库Helper为空");
                // 重新打开数据库
                MultiDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(),
                        String.valueOf(sp.getLong(SplashActivity.UID, 0)));

                // 待测试
                /*AppActivityManager.getInstance().popAllActivity();
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
            }
        } else {
            Log.d(TAG, "此时,没登录");
        }
    }

    public void onEvent(FragmentDeliverEvent fragmentDeliverEvent) {
        Log.d(TAG, "red indicator");
        // 如果此时是否存在用户登录
        SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            if (fragmentDeliverEvent.getIndicate() == FragmentDeliverEvent.RED_INDICATOR) {
                if (mNotifyIndicator != null && mNotifyIndicator.getVisibility() == View.VISIBLE) {
                    mNotifyIndicator.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 双击退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            // 判断是否在两秒之内连续点击返回键, 是则退出, 否则不退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                // 将系统的当前的时间赋值给exitTime
                exitTime = System.currentTimeMillis();
            } else {
                exitApp();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void	exitApp() {
        try {
            // 同时清空缓存
            youngCache.remove(YoungCache.CACHE_MINE_PUBLISH);

            AppActivityManager.getInstance().popAllActivity();
        } catch (Exception e) {

        }

    }

    /**
     * 打开通知细节
     */
    private void openDetailByMessageType(String message) {
        Log.d(TAG, "open detail by message type : " + message);

        int type;
        String businessId = "";
        int businessType = 0;
        String targetUrl = "";

        JSONObject jsonObject = JSONObject.parseObject(message);

        type = jsonObject.getInteger(GlobalConstants.JSON_TYPE);

        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSID)) {
            businessId = jsonObject.getString(GlobalConstants.JSON_BUSINESSID);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_BUSINESSTYPE)) {
            businessType = jsonObject.getInteger(GlobalConstants.JSON_BUSINESSTYPE);
        }

        if (jsonObject.containsKey(GlobalConstants.JSON_TARGETURL)) {
            targetUrl = jsonObject.getString(GlobalConstants.JSON_TARGETURL);
        }

        switch (type) {
            case YoungMessage.MSG_TYPE_ARTICLE_COMMENT:
            case YoungMessage.MSG_TYPE_ARTICLE_COMMENT_REPLY:
            case YoungMessage.MSG_TYPE_ARTICLE_LIKE:
            case YoungMessage.MSG_TYPE_ARTICLE_COMMENT_LIKE:
                Intent articleIntent = new Intent(this, FreshDetailsActivity.class);
                articleIntent.putExtra("article_id", businessId);
                startActivity(articleIntent);
                break;
            case YoungMessage.MSG_TYPE_QUESTION_COMMENT:
            case YoungMessage.MSG_TYPE_QUESTION_COMMENT_REPLY:
            case YoungMessage.MSG_TYPE_QUESTION_COMMENT_LIKE:
                Intent questionIntent = new Intent(this, QuestionDetailsActivity.class);
                questionIntent.putExtra("article_id", businessId);
                startActivity(questionIntent);
                break;
            case YoungMessage.MSG_TYPE_SPECIAL_COMMENT_REPLY:
            case YoungMessage.MSG_TYPE_SPECIAL_COMMENT_LIKE:
                Topic topic = new Topic();
                if (!StringUtil.isEmpty(businessId)) {
                    topic.setBusinessId(businessId);
                    topic.setBusinessType(businessType);
                }
                Intent intent = new Intent(this, SpecialTopicActivity.class);
                intent.putExtra("topic", topic);
                startActivity(intent);
                break;
            case YoungMessage.MSG_TYPE_H5_WEBPAGE_COMMENT_REPLY:
            case YoungMessage.MSG_TYPE_H5_WEBPAGE_COMMENT_LIKE:
                if (!StringUtil.isEmpty(targetUrl)) {
                    Intent h5Intent = new Intent(this, NotifyH5Activity.class);
                    h5Intent.putExtra("target_url", targetUrl);
                    startActivity(h5Intent);
                }
                break;

            // 下面是系统消息
            case YoungMessage.NOTIFY_TYPE_ACTIVITY_ACTIVE:
                BannerActivity bannerActivity = new BannerActivity();
                if (!StringUtil.isEmpty(businessId)) {
                    bannerActivity.setBusinessId(businessId);
                }
                Intent latestArticleIntent = new Intent(this, LatestActivity.class);
                latestArticleIntent.putExtra("normal_activity", bannerActivity);
                startActivity(latestArticleIntent);
                break;

            case YoungMessage.NOTIFY_TYPE_SPECIAL_PUBLISH:
                Topic topic2 = new Topic();
                if (!StringUtil.isEmpty(businessId)) {
                    topic2.setBusinessId(businessId);
                    topic2.setBusinessType(businessType);
                }
                Intent intent2 = new Intent(this, SpecialTopicActivity.class);
                intent2.putExtra("topic", topic2);
                startActivity(intent2);
                break;

            case YoungMessage.NOTIFY_TYPE_WEB_ACTIVITY_ACTIVE:
                if (!StringUtil.isEmpty(targetUrl)) {
                    Intent webActivityIntent = new Intent(this, BannerH5Activity.class);
                    webActivityIntent.putExtra("target_url", targetUrl);
                    startActivity(webActivityIntent);
                }
                break;
            case YoungMessage.NOTIFY_TYPE_WEBPAGE_PUBLISH:
                if (!StringUtil.isEmpty(targetUrl)) {
                    Topic topicWeb = new Topic();
                    topicWeb.setTargetUrl(targetUrl);
                    Intent webpageIntent = new Intent(this, TopicWebActivity.class);
                    webpageIntent.putExtra("topic", topicWeb);
                    startActivity(webpageIntent);
                }
                break;
            case YoungMessage.NOTIFY_TYPE_URL_PUBLISH:
                if (!StringUtil.isEmpty(targetUrl)) {
                    Topic topicUrl = new Topic();
                    topicUrl.setTargetUrl(targetUrl);
                    Intent urlIntent = new Intent(this, TopicWebActivity.class);
                    urlIntent.putExtra("topic", topicUrl);
                    startActivity(urlIntent);
                }
                break;

            case YoungMessage.NOTIFY_TYPE_THEME_PUBLISH:
                Intent themePublishIntent = new Intent(this, SearchDetailActivity.class);
                themePublishIntent.putExtra("tag_id", businessId);
                themePublishIntent.putExtra("share_businessType", SearchDetailActivity.TYPE_BUSINESS_ARTICLE);
                startActivity(themePublishIntent);
                break;

            case YoungMessage.NOTIFY_TYPE_ARTICLE_PUBLISH:
                Intent articlePublishIntent = new Intent(this, FreshDetailsActivity.class);
                articlePublishIntent.putExtra("article_id", businessId);
                startActivity(articlePublishIntent);
                break;
            case YoungMessage.NOTIFY_TYPE_COMMON_MSG:
                Intent commonSysIntent = new Intent(this, SystemMessageActivity.class);
                startActivity(commonSysIntent);
                break;
        }
    }

    /**
     * 接收消息Event
     */
    public void onEvent(NotifyIndicatorEvent notifyIndicatorEvent) {
        if (notifyIndicatorEvent.getIndicator()) {
            // 此时, 有新消息
            mEventLogic.getUnreadCount();
        }
    }

    /**
     * 附加到Activity才生效
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
