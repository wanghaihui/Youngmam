package com.xiaobukuaipao.youngmam.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.FollowingActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.RegisterAndLoginActivity;
import com.xiaobukuaipao.youngmam.SettingActivity;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.adapter.MineFragmentPagerAdapter;
import com.xiaobukuaipao.youngmam.animate.ViewHelper;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.database.MamaTable;
import com.xiaobukuaipao.youngmam.domain.AvatarModifyEvent;
import com.xiaobukuaipao.youngmam.domain.FollowEvent;
import com.xiaobukuaipao.youngmam.domain.MinePublishDelayRefreshEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.parallax.ScrollTabHolder;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.view.RoundedImageView;
import com.xiaobukuaipao.youngmam.widget.MaterialAlertDialog2;
import com.xiaobukuaipao.youngmam.widget.PagerSlidingTabStrip;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

/**
 * Created by xiaobu1 on 15-4-23.
 */
public class MineFragment extends BaseHttpFragment implements ScrollTabHolder, ViewPager.OnPageChangeListener {

    private static final String TAG = MineFragment.class.getSimpleName();

    // ActionBar
    protected YoungActionBar actionBar;

    private View mHeader;

    // 最小的头部高度
    private int mMinHeaderHeight;
    // 实际的头部高度
    private int mHeaderHeight;
    // 最小的头部转换
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

    private MineFragmentPagerAdapter pagerAdapter;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    private EventBus eventBus;

    private LinearLayout basicLayout;

    private YoungCache youngCache;

    private long credit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_mine, container, false);
        this.view = view;
        mEventLogic = new YoungEventLogic(this);
        return view;
    }

    @Override
    public void initUIAndData() {
        // 添加ActionBar
        actionBar = (YoungActionBar) view.findViewById(R.id.action_bar);
        setYoungActionBar();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        youngCache = YoungCache.get(this.getActivity());

        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getResources().getDimensionPixelSize(R.dimen.action_bar_height);

        mHeader = view.findViewById(R.id.header);

        dm = getResources().getDisplayMetrics();

        mAvatar = (RoundedImageView) view.findViewById(R.id.avatar);
        mName = (TextView) view.findViewById(R.id.name);
        mExpert = (TextView) view.findViewById(R.id.expert);
        mState = (TextView) view.findViewById(R.id.state);

        mFollowLayout = (LinearLayout) view.findViewById(R.id.following_layout);
        mFollow = (TextView) view.findViewById(R.id.follow);
        mHuafenLayout = (LinearLayout) view.findViewById(R.id.huafen_layout);
        mHuafen = (TextView) view.findViewById(R.id.huafen);

        basicLayout = (LinearLayout) view.findViewById(R.id.basic_info_layout);

        tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);

        pagerAdapter = new MineFragmentPagerAdapter(getChildFragmentManager());
        pagerAdapter.setTabHolderScrollingContent(this);

        viewPager.setAdapter(pagerAdapter);
        tabStrip.setViewPager(viewPager);
        // 未执行adjustScroll
        tabStrip.setOnPageChangeListener(this);

        setTabsValue();

        /**
         * 完善用户基本信息
         */
        addUserBaseInfo();

        setUIListeners();
    }

    private void setYoungActionBar() {
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_mine_home));
        actionBar.setMiddleTextColor(Color.TRANSPARENT);

        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            actionBar.setRightAction(YoungActionBar.Type.IMAGE, R.drawable.btn_setting, null);
            /**
             * 设置监听器
             */
            actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MineFragment.this.getActivity(), SettingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
            if (sp.getLong(SplashActivity.UID, 0) <= 0) {
                // 此时, 未登录
                MaterialAlertDialog2 dialog = new MaterialAlertDialog2(this.getActivity(),
                        getResources().getString(R.string.mine_basic_info), getResources().getString(R.string.please_login_first),
                        getResources().getString(R.string.str_login));

                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MineFragment.this.getActivity(), RegisterAndLoginActivity.class);
                        startActivity(intent);
                    }
                });

                dialog.setOnCancelButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                dialog.show();
            } else {
                // 此时, 登录
                // 首先判断四个子页面中哪个的缓存被清空, 清空的则重新加载一遍数据
                if (youngCache.getAsString(YoungCache.CACHE_MINE_PUBLISH) == null) {
                    // 此时, 代表需要刷新PublishFragment页面
                    EventBus.getDefault().post(new MinePublishDelayRefreshEvent(true));
                }

            }
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值
     */
    private void setTabsValue() {
        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);

        // 设置Tab是自动填充满屏幕的
        tabStrip.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        // tabStrip.setDividerColor(Color.TRANSPARENT);
        tabStrip.setDividerColor(getResources().getColor(R.color.color_bbbaba));
        // 设置Tab底部线的高度
        tabStrip.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3, dm));
        // 设置Tab标题文字的大小
        tabStrip.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));

        // 设置Tab Indicator的颜色
        tabStrip.setIndicatorColor(Color.parseColor("#ff4c51"));

        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColor(Color.parseColor("#ff4c51"));

    }

    private void setUIListeners() {
        mHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleHeaderClick();
            }
        });

        mFollowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = MineFragment.this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    Intent intent = new Intent(MineFragment.this.getActivity(), FollowingActivity.class);
                    intent.putExtra("host", String.valueOf(sp.getLong(SplashActivity.UID, 0)));
                    intent.putExtra("type", FollowingActivity.TYPE_FOLLOWING);
                    startActivity(intent);
                }
            }
        });

        mHuafenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = MineFragment.this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                if (sp.getLong(SplashActivity.UID, 0) > 0) {
                    Intent intent = new Intent(MineFragment.this.getActivity(), FollowingActivity.class);
                    intent.putExtra("host", String.valueOf(sp.getLong(SplashActivity.UID, 0)));
                    intent.putExtra("type", FollowingActivity.TYPE_FOLLOWER);
                    startActivity(intent);
                }
            }
        });
    }

    private void handleHeaderClick() {
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            Log.d(TAG, "uid : " + sp.getLong(SplashActivity.UID, 0));
        } else {
            Intent intent = new Intent(MineFragment.this.getActivity(), RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    private void addUserBaseInfo() {
        /**
         * 得到用户的基本信息
         */
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            basicLayout.setVisibility(View.VISIBLE);
            /**
             * 得到用户的基本信息--无论数据库是否写, 每次进来都进行信息的获取
             */
            mEventLogic.getUserBase(String.valueOf(sp.getLong(SplashActivity.UID, 0)));

        } else {
            basicLayout.setVisibility(View.GONE);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
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
        if (currentHolder != null) {
            currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper.getTranslationY(mHeader)));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

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
        actionBar.setBackgroundColor(Color.argb(alpha, 0xff, 0x4c, 0x51));
        actionBar.setMiddleTextColor(Color.argb(alpha, 0xff, 0xff, 0xff));
    }

    ///////////////////////////////////////////////////////////////////////////////////
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

                    if (jsonObjectAll != null) {

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

                            // 从本地数据库取
                            getMineInfoFromDatabase();

                        } else {
                            // 从本地数据库取
                            getMineInfoFromDatabase();
                        }
                    }

                    if (jsonObjectAll.containsKey(GlobalConstants.JSON_FANSCOUNT)) {
                        mHuafen.setText(jsonObjectAll.getString(GlobalConstants.JSON_FANSCOUNT));
                    }

                    if (jsonObjectAll.containsKey(GlobalConstants.JSON_FOLLOWCOUNT)) {
                        mFollow.setText(jsonObjectAll.getString(GlobalConstants.JSON_FOLLOWCOUNT));
                    }

                } else {
                    // 从本地数据库取
                    getMineInfoFromDatabase();
                }
                break;

            default:
                break;
        }
    }

    private void getMineInfoFromDatabase() {
        Cursor cursor = this.getActivity().getContentResolver().query(YoungContentProvider.MAMA_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String baseHeadUrl = cursor.getString(cursor.getColumnIndex(MamaTable.COLUMN_HEAD_URL));
            Glide.with(this.getActivity())
                    .load(baseHeadUrl)
                    .centerCrop()
                    .override(DisplayUtil.dip2px(this.getActivity(), 56), DisplayUtil.dip2px(this.getActivity(), 56))
                    .dontAnimate()
                    .into(mAvatar);

            String baseName = cursor.getString(cursor.getColumnIndex(MamaTable.COLUMN_NAME));
            mName.setText(baseName);

            int childStatus = cursor.getInt(cursor.getColumnIndex(MamaTable.COLUMN_CHILD_STATUS));
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

            int baseExpertType = cursor.getInt(cursor.getColumnIndex(MamaTable.COLUMN_EXPERT_TYPE));
            String baseExpertName = cursor.getString(cursor.getColumnIndex(MamaTable.COLUMN_EXPERT_NAME));
            if (baseExpertType != 0) {
                mExpert.setVisibility(View.VISIBLE);
                mExpert.setText(baseExpertName);
            } else {
                mExpert.setVisibility(View.GONE);
            }

            cursor.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterAll(mEventLogic);
    }


    public synchronized void insertToDatabase(Long userId, String headUrl, String name, int state, int expertType, String expertName) {

        Cursor cursor = this.getActivity().getContentResolver().query(YoungContentProvider.MAMA_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            this.getActivity().getContentResolver().delete(YoungContentProvider.MAMA_CONTENT_URI, null, null);
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
        this.getActivity().getContentResolver().insert(YoungContentProvider.MAMA_CONTENT_URI, values);

    }

    // 积分处理
    /*public void onEvent(MineCreditEvent mineCreditEvent) {
        // 如果此时是否存在用户登录
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            if (mineCreditEvent.getAddCredit()) {
                // 获取用户的总积分

            }
        } else {
            Log.d(TAG, "此时, 没登录, 不能增加积分");
        }
    }*/

    public void onEvent(AvatarModifyEvent avatarModifyEvent) {
        if (avatarModifyEvent.getModify()) {
            addUserBaseInfo();
        }
    }

    public void onEvent(FollowEvent followEvent) {
        if (followEvent.getType() == FollowEvent.TYPE_ADD_FOLLOW) {
            // 此时, 添加关注
            int follow = Integer.valueOf(mFollow.getText().toString());
            follow = follow + 1;
            mFollow.setText(String.valueOf(follow));
        } else if (followEvent.getType() == FollowEvent.TYPE_DELETE_FOLLOW) {
            // 此时, 取消关注
            int follow = Integer.valueOf(mFollow.getText().toString());
            follow = follow - 1;
            mFollow.setText(String.valueOf(follow));
        }
    }
}
