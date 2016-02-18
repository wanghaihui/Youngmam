package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.domain.MineGiftAddrEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-7-1.
 */
public class GiftAddressActivity extends BaseHttpFragmentActivity {
    private static final String TAG = GiftAddressActivity.class.getSimpleName();

    private EditText nameText;
    private String name;

    private EditText phoneText;
    private String phone;

    private EditText addressText;
    private String address;

    // 地址Id
    private String txId;

    private boolean exchange = false;

    private boolean mineGiftAddr = false;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);
    // 分享的标题
    private String shareTitle;
    // 分享的内容
    private String shareContent;
    // 分享的内容的链接
    private String targetUrl;
    // 分享的图片--活动或文章或主题
    private String imageUrl;

    public void initViews() {
        setContentView(R.layout.activity_gift_address);

        getIntentDatas();
        // 设置ActionBar
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        nameText = (EditText) findViewById(R.id.recipient_name);
        phoneText = (EditText) findViewById(R.id.phone);
        addressText = (EditText) findViewById(R.id.input_address);

        // 配置需要分享的相关平台
        configPlatforms(mController);

    }

    private void getIntentDatas() {
        txId = getIntent().getStringExtra("tx_id");
        shareTitle = getIntent().getStringExtra("name");
        shareContent = getIntent().getStringExtra("content");
        imageUrl = getIntent().getStringExtra("image_url");
        exchange = getIntent().getBooleanExtra("exchange", false);
        mineGiftAddr = getIntent().getBooleanExtra("mine_gift_addr", false);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_gift_address));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_save));

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGiftAddress();
            }
        });
    }

    private void saveGiftAddress() {
        if (StringUtil.isEmpty(nameText.getText().toString())) {
            Toast.makeText(this, "请填写您的真实姓名", Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(phoneText.getText().toString())) {
            Toast.makeText(this, "请填写您的手机号码", Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(addressText.getText().toString())) {
            Toast.makeText(this, "请填写您的详细地址", Toast.LENGTH_SHORT).show();
        } else {
            if (phoneText.getText().toString().length() != 11) {
                Toast.makeText(this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
            } else {
                mEventLogic.setGiftAddress(txId, nameText.getText().toString(),
                        phoneText.getText().toString(),
                        addressText.getText().toString());
            }
        }
    }

    public void executeHttpRequest() {
        // 获取用户收件地址
        if (exchange) {
            mEventLogic.getGiftAddress(null);
        } else {
            mEventLogic.getGiftAddress(txId);
        }
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_gift_address:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        if (jsonObject.containsKey(GlobalConstants.JSON_NAME)) {
                            name = jsonObject.getString(GlobalConstants.JSON_NAME);
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_MOBILE)) {
                            phone = jsonObject.getString(GlobalConstants.JSON_MOBILE);
                        }

                        if (jsonObject.containsKey(GlobalConstants.JSON_ADDR)) {
                            address = jsonObject.getString(GlobalConstants.JSON_ADDR);
                        }

                        nameText.setText(name);
                        phoneText.setText(phone);
                        addressText.setText(address);
                    }

                }
                break;

            case R.id.set_gift_address:

                if (checkResponse(msg)) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

                    if (mineGiftAddr) {
                        EventBus.getDefault().post(new MineGiftAddrEvent(true));
                    }

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
