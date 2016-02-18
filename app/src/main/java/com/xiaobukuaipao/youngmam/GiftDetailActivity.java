package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.adapter.SquarePhotoPagerAdapter;
import com.xiaobukuaipao.youngmam.domain.Gift;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.CommonUtil;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.ContentHeightViewPager;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-7-1.
 */
public class GiftDetailActivity extends BaseHttpFragmentActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = GiftDetailActivity.class.getSimpleName();

    // 积分
    private int credit = 0;

    private String itemId;
    private String giftName;

    // ActionBar
    private int leftFrameWidth;
    private int rightFrameWidth;

    private int giftNameLen;

    // 商品Gift
    private Gift gift;

    private ScrollView scrollView;

    private ContentHeightViewPager viewPager;
    private SquarePhotoPagerAdapter mPhotoAdapter;
    private List<String> mPhotoList = new ArrayList<String>();

    private LinearLayout mDotsLayout;

    // 记录当前选中位置
    private int currentIndex;

    private TextView mGiftName;
    private TextView mGiftPrice;
    private TextView mGiftPrivilege;
    private TextView mGiftCount;

    private TextView mGiftDesc;

    private Button exchangeBtn;

    // EventBus
    private EventBus eventBus;

    public void initViews() {
        setContentView(R.layout.activity_gift_detail);

        getIntentDatas();

        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        // ScrollView滚动到顶部
        scrollView.smoothScrollTo(0, 20);
        scrollView.setVisibility(View.GONE);

        viewPager = (ContentHeightViewPager) findViewById(R.id.view_pager);
        mPhotoAdapter = new SquarePhotoPagerAdapter(this, viewPager);

        mDotsLayout = (LinearLayout) findViewById(R.id.guide_dots_layout);

        mGiftName = (TextView) findViewById(R.id.gift_name);
        mGiftPrice = (TextView) findViewById(R.id.gift_price);
        mGiftPrivilege = (TextView) findViewById(R.id.gift_privilege);
        mGiftCount = (TextView) findViewById(R.id.gift_count);

        mGiftDesc = (TextView) findViewById(R.id.gift_desc);

        exchangeBtn = (Button) findViewById(R.id.btn_exchange);

        initDatas();

        setUIListeners();
    }

    private void getIntentDatas() {
        itemId = getIntent().getStringExtra("item_id");
        giftName = getIntent().getStringExtra("gift_name");
    }

    private void initDatas() {
        mPhotoAdapter.setPhotoLists(mPhotoList);
        viewPager.setAdapter(mPhotoAdapter);

        // 绑定回调
        viewPager.setOnPageChangeListener(this);
    }

    private void setUIListeners() {
        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeGift();
            }
        });
    }

    private void exchangeGift() {
        if (CommonUtil.isLogin(this)) {
            if (gift != null) {
                if (credit >= gift.getCost()) {
                    // 进行礼品兑换
                    mEventLogic.exchangeGift(gift.getItemId());
                } else {
                    Toast.makeText(this, "积分不够哦~", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // 未登录,此时要先去登录
            Intent intent = new Intent(this, RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setRightAction(YoungActionBar.Type.TEXT_IMAGE, R.mipmap.mine_credit, credit > 0 ? String.valueOf(credit) : "");

        if (!StringUtil.isEmpty(giftName)) {
            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, giftName);
            leftFrameWidth = DisplayUtil.dip2px(this, 54);

            actionBar.getRightFrame().getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            rightFrameWidth = actionBar.getRightFrame().getWidth();

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(leftFrameWidth, 0, rightFrameWidth, 0);
                            params.addRule(RelativeLayout.CENTER_IN_PARENT);
                            actionBar.getMiddleFrame().setLayoutParams(params);

                            actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, giftName);

                            actionBar.getRightFrame().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });
        }

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GiftDetailActivity.this, MineCreditActivity.class);
                startActivity(intent);
            }
        });
    }

    public void executeHttpRequest() {
        // 获取用户的总积分
        mEventLogic.getAllCredit();

        mEventLogic.getBonusDetail(itemId);
    }

    /**
     * 设置当前的点
     */
    private void setCurrentDot(int position) {
        if (position < 0 || position > mPhotoList.size() - 1 || currentIndex == position) {
            return;
        }

        mDotsLayout.getChildAt(position).setEnabled(false);
        mDotsLayout.getChildAt(currentIndex).setEnabled(true);

        currentIndex = position;
    }


    @Override
    public void	onPageScrollStateChanged(int state) {

    }

    @Override
    public void	onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void	onPageSelected(int position) {
        // 设置底部小点选中状态
        setCurrentDot(position);
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_all_credit:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                        credit = Integer.valueOf(jsonObject.getString(GlobalConstants.JSON_BONUSPOINT));
                    }

                    ((TextView) actionBar.getRightFrame().getChildAt(0)).setText(credit > 0 ? String.valueOf(credit) : "");
                }
                break;

            case R.id.get_bonus_detail:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    scrollView.setVisibility(View.VISIBLE);

                    gift = new Gift(jsonObject);

                    if (gift.getImgs() != null && gift.getImgs().size() > 0) {

                        for (int i=0; i < gift.getImgs().size(); i++) {
                            mPhotoList.add(gift.getImgs().getJSONObject(i).getString(GlobalConstants.JSON_URL));
                        }

                        mPhotoAdapter.notifyDataSetChanged();

                        if (mPhotoList != null && mPhotoList.size() > 1) {
                            for (int i = 0; i < mPhotoList.size(); i++) {
                                ImageView imageView = new ImageView(this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                imageView.setLayoutParams(params);
                                imageView.setPadding(0, 0, 6, 0);
                                imageView.setImageResource(R.drawable.guide_dot2);

                                mDotsLayout.addView(imageView);
                            }

                            currentIndex = 0;
                            // 设置为白色,即选中状态
                            mDotsLayout.getChildAt(currentIndex).setEnabled(false);
                        }
                    }

                    mGiftName.setText(gift.getName());
                    mGiftPrice.setText(String.valueOf(gift.getCost()));

                    if (gift.getPrivilege() == 1) {
                        // 此时,不是达人专享,代表所有用户都可以参与
                        mGiftPrivilege.setText(getResources().getString(R.string.str_gift_privilege_all_user));
                    } else {
                        // 此时,是达人专享
                        mGiftPrivilege.setText(getResources().getString(R.string.str_gift_privilege_expert));
                    }

                    mGiftCount.setText(getResources().getString(R.string.str_gift_count, gift.getCount()));

                    if (!StringUtil.isEmpty(gift.getDesc())) {
                        mGiftDesc.setText(gift.getDesc());
                    } else {
                        mGiftDesc.setText("暂无");
                    }
                }

                break;

            case R.id.exchange_gift:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                        String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                        String txId = null;
                        if (jsonObject.containsKey(GlobalConstants.JSON_TXID)) {
                            txId = jsonObject.getString(GlobalConstants.JSON_TXID);
                        }

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (status == 0) {

                            if (gift != null) {
                                Intent intent = new Intent(this, GiftAddressActivity.class);
                                if (!StringUtil.isEmpty(txId)) {
                                    intent.putExtra("tx_id", txId);
                                }
                                intent.putExtra("exchange", true);
                                intent.putExtra("name", gift.getName());
                                intent.putExtra("content", gift.getDesc());
                                if (gift.getImgs() != null && gift.getImgs().size() > 0) {
                                    intent.putExtra("image_url", gift.getImgs().getJSONObject(0).getString(GlobalConstants.JSON_URL));
                                }
                                startActivity(intent);
                            }

                        }
                    }
                }
                break;
        }
    }
}
