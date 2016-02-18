package com.xiaobukuaipao.youngmam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.adapter.OtherFragmentPagerAdapter;
import com.xiaobukuaipao.youngmam.animate.ViewHelper;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.domain.UserBase;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.parallax.ScrollTabHolder;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;
import com.xiaobukuaipao.youngmam.widget.PagerSlidingTabStrip;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-4-30.
 * 别人的个人主页
 */
public class OtherActivity extends BaseHttpFragmentActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener {
    private static final String TAG = OtherActivity.class.getSimpleName();

    private View mHeader;

    private int mMinHeaderHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;

    private RoundedImageView mAvatar;
    private TextView mName;
    private TextView mExpert;
    private TextView mState;

    private LinearLayout mFollowLayout;
    private TextView mFollow;
    private LinearLayout mHuafenLayout;
    private TextView mHuafen;

    private PagerSlidingTabStrip tabStrip;
    private ViewPager viewPager;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    private OtherFragmentPagerAdapter pagerAdapter;

    /**
     * id
     */
    private String userId;
    private String userName;

    private RelativeLayout otherActionBar;
    private FrameLayout actionLeftLayout;
    private TextView actionName;
    private TextView actionFollow;

    private UserBase userBase;

    // EventBus
    private EventBus eventBus;

    public void initViews() {
        setContentView(R.layout.activity_other);

        getIntentDatas();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        // 设置ActionBar
        otherActionBar = (RelativeLayout) findViewById(R.id.action_bar);
        setYoungActionBar();

        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getResources().getDimensionPixelSize(R.dimen.action_bar_height);

        mHeader = findViewById(R.id.header);

        mHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dm = getResources().getDisplayMetrics();

        mAvatar = (RoundedImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mExpert = (TextView) findViewById(R.id.expert);
        mState = (TextView) findViewById(R.id.state);

        mFollowLayout = (LinearLayout) findViewById(R.id.following_layout);
        mFollow = (TextView) findViewById(R.id.follow);
        mHuafenLayout = (LinearLayout) findViewById(R.id.huafen_layout);
        mHuafen = (TextView) findViewById(R.id.huafen);

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);

        pagerAdapter = new OtherFragmentPagerAdapter(getSupportFragmentManager(), userId);
        pagerAdapter.setTabHolderScrollingContent(this);

        viewPager.setAdapter(pagerAdapter);
        tabStrip.setViewPager(viewPager);
        // 未执行adjustScroll
        tabStrip.setOnPageChangeListener(this);

        setTabsValue();

        setUIListeners();
    }

    private void getIntentDatas() {
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        /*actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, userName);
        setBackClickListener(this);*/

        actionLeftLayout = (FrameLayout) findViewById(R.id.left_layout);
        actionLeftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionName = (TextView) findViewById(R.id.id_name);
        actionName.setText(userName);

        actionFollow = (TextView) findViewById(R.id.id_follow);
        actionFollow.setVisibility(View.GONE);

        actionFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow();
            }
        });
    }

    /**
     * 关注
     */
    private void follow() {
        if (userBase != null) {
            if (userBase.isHasFollowed()) {
                // 此时, 取消关注
                mEventLogic.deleteFollow(userBase.getUserId());
            } else {
                // 此时, 加关注
                mEventLogic.addFollow(userBase.getUserId());
            }
        }
    }

    private void setUIListeners() {
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (member != null) {
                    AvatarDialogFragment fragment = new AvatarDialogFragment(member.getHeadUrl());
                    fragment.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_NoActionBar);
                    fragment.show(OtherActivity.this.getSupportFragmentManager(), "avatar");
                }*/
            }
        });

        mFollowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null) {
                    Intent intent = new Intent(OtherActivity.this, FollowingActivity.class);
                    intent.putExtra("host", userId);
                    intent.putExtra("type", FollowingActivity.TYPE_FOLLOWING);
                    startActivity(intent);
                }
            }
        });

        mHuafenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null) {
                    Intent intent = new Intent(OtherActivity.this, FollowingActivity.class);
                    intent.putExtra("host", userId);
                    intent.putExtra("type", FollowingActivity.TYPE_FOLLOWER);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);

        // 设置Tab是自动填充满屏幕的
        tabStrip.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabStrip.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabStrip.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));

        // 设置Tab Indicator的颜色
        tabStrip.setIndicatorColor(Color.parseColor("#ff4c51"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColor(Color.parseColor("#ff4c51"));

    }

    public void executeHttpRequest() {
        if (userId != null) {
            mEventLogic.getUserBase(userId);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * page页选择
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = pagerAdapter.getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
        currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper.getTranslationY(mHeader)));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void adjustScroll(int scrollHeight) {
        // do nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (viewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);

            ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));

            float ratio = clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
            setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
        }
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    private void setTitleAlpha(float ratio) {
        int alpha = (int) (ratio * 0xff);
        otherActionBar.setBackgroundColor(Color.argb(alpha, 0xff, 0x4c, 0x51));
        actionName.setTextColor(Color.argb(alpha, 0xff, 0xff, 0xff));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int getScrollY(AbsListView listView) {
        View child = listView.getChildAt(0);
        if (child == null) {
            return 0;
        }

        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = child.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * child.getHeight() + headerHeight;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {

            case R.id.get_user_base:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObjectAll = JSONObject.parseObject(httpResult.getData());

                    Log.d(TAG, "datas : " + httpResult.getData());

                    if (jsonObjectAll != null) {

                        if (jsonObjectAll.containsKey(GlobalConstants.JSON_USERBASE)) {
                            JSONObject jsonObject = jsonObjectAll.getJSONObject(GlobalConstants.JSON_USERBASE);

                            if (jsonObject != null) {
                                userBase = new UserBase(jsonObject);

                                if (jsonObject.containsKey(GlobalConstants.JSON_HEADURL)) {
                                    String headUrl = jsonObject.getString(GlobalConstants.JSON_HEADURL);
                                    Glide.with(this)
                                            .load(headUrl)
                                            .centerCrop()
                                            .override(DisplayUtil.dip2px(this, 56), DisplayUtil.dip2px(this, 56))
                                            .dontAnimate()
                                            .into(mAvatar);
                                }

                                if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
                                    String name = jsonObject.getString(GlobalConstants.JSON_NAME);
                                    mName.setText(name);
                                }

                                if (jsonObject.containsKey(GlobalConstants.JSON_CHILDSTATUS)) {
                                    int childStatus = jsonObject.getInteger(GlobalConstants.JSON_CHILDSTATUS);
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
                                            mState.setText(getResources().getString(R.string.str_prepare));
                                            break;
                                    }
                                }

                                if (jsonObject.containsKey(GlobalConstants.JSON_EXPERTTYPE)) {
                                    int expertType = jsonObject.getInteger(GlobalConstants.JSON_EXPERTTYPE);
                                    if (expertType != 0) {
                                        mExpert.setVisibility(View.VISIBLE);
                                        if (jsonObject.containsKey(GlobalConstants.JSON_EXPERTNAME)) {
                                            mExpert.setText(jsonObject.getString(GlobalConstants.JSON_EXPERTNAME));
                                        }
                                    } else {
                                        mExpert.setVisibility(View.GONE);
                                    }
                                }

                                if (jsonObject.containsKey(GlobalConstants.JSON_HASFOLLOWED)) {

                                    SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                                    if (sp.getLong(SplashActivity.UID, 0) > 0) {
                                        if (!userId.equals(String.valueOf(sp.getLong(SplashActivity.UID, 0)))) {
                                            actionFollow.setVisibility(View.VISIBLE);

                                            boolean hasFollowed = jsonObject.getBoolean(GlobalConstants.JSON_HASFOLLOWED);
                                            if (hasFollowed) {
                                                // 此时已经关注
                                                actionFollow.setSelected(true);
                                                actionFollow.setText(getResources().getString(R.string.has_followed));
                                                actionFollow.setTextColor(getResources().getColor(R.color.white));

                                            } else {
                                                // 此时没有关注
                                                actionFollow.setSelected(false);
                                                actionFollow.setText(getResources().getString(R.string.add_follow));
                                                actionFollow.setTextColor(getResources().getColor(R.color.color_ff4c51));

                                            }
                                        } else {
                                            actionFollow.setVisibility(View.GONE);
                                        }
                                    } else {
                                        actionFollow.setVisibility(View.GONE);
                                    }
                                }
                            }

                        }
                    }

                    if (jsonObjectAll.containsKey(GlobalConstants.JSON_FANSCOUNT)) {
                        mHuafen.setText(jsonObjectAll.getString(GlobalConstants.JSON_FANSCOUNT));
                    }

                    if (jsonObjectAll.containsKey(GlobalConstants.JSON_FOLLOWCOUNT)) {
                        mFollow.setText(jsonObjectAll.getString(GlobalConstants.JSON_FOLLOWCOUNT));
                    }

                }
                break;

            case R.id.add_follow:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String userId = msg.getData().getString("key");

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {
                            EventBus.getDefault().post(new FollowEvent(userId, FollowEvent.TYPE_ADD_FOLLOW));
                        }
                    }
                }
                break;
            case R.id.delete_follow:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    String userId = msg.getData().getString("key");

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {
                            EventBus.getDefault().post(new FollowEvent(userId, FollowEvent.TYPE_DELETE_FOLLOW));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    public void onEvent(FollowEvent followEvent) {
        if (followEvent.getType() == FollowEvent.TYPE_ADD_FOLLOW) {
            // 此时, 添加关注
            boolean hasFollowed = userBase.isHasFollowed();
            hasFollowed = !hasFollowed;
            userBase.setHasFollowed(hasFollowed);

            actionFollow.setSelected(true);
            actionFollow.setText(getResources().getString(R.string.has_followed));
            actionFollow.setTextColor(getResources().getColor(R.color.white));
        } else if (followEvent.getType() == FollowEvent.TYPE_DELETE_FOLLOW) {
            // 此时, 取消关注
            boolean hasFollowed = userBase.isHasFollowed();
            hasFollowed = !hasFollowed;
            userBase.setHasFollowed(hasFollowed);

            actionFollow.setSelected(false);
            actionFollow.setText(getResources().getString(R.string.add_follow));
            actionFollow.setTextColor(getResources().getColor(R.color.color_ff4c51));
        }
    }
}
