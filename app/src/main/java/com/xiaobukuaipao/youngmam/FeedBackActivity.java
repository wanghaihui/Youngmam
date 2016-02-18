package com.xiaobukuaipao.youngmam;

import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-6-2.
 */
public class FeedBackActivity extends BaseHttpFragmentActivity {

    private EditText mFeedBack;
    private EditText mFeedBackContact;

    public void initViews() {
        setContentView(R.layout.activity_feedback);

        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        mFeedBack = (EditText) findViewById(R.id.feed_back);
        mFeedBackContact = (EditText) findViewById(R.id.feed_back_contact);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_feedback));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_send));

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedBack();
            }
        });
    }

    private void sendFeedBack() {
        if (StringUtil.isNotEmpty(mFeedBack.getText().toString()) && StringUtil.isNotEmpty(mFeedBackContact.getText().toString())) {
            mEventLogic.sendFeedback(mFeedBackContact.getText().toString(), mFeedBack.getText().toString());
        } else {
            if (StringUtil.isEmpty(mFeedBack.getText().toString())) {
                Toast.makeText(this, "请先填写您的意见哦~", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请填写正确的联系方式", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.add_feedback:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                    if (status == 0) {
                        Toast.makeText(this, "谢谢您的宝贵意见", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;

        }
    }
}
