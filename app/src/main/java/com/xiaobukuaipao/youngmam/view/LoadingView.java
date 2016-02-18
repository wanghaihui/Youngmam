package com.xiaobukuaipao.youngmam.view;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.annotation.ViewInject;
import com.xiaobukuaipao.youngmam.annotation.ViewUtils;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class LoadingView extends RelativeLayout implements View.OnClickListener {

    @ViewInject(value = R.id.loading_progressBar)
    private ProgressBar progressBar;
    @ViewInject(value = R.id.tip_text)
    private TextView tipText;

    private boolean isLoading = true;

    private EventBus eventBus;

    /**
     * 订阅者
     */
    private Object mSubscriber;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 设置当前View的点击事件
        setOnClickListener(this);
        eventBus = new EventBus();
    }

    public void register(Object subscriber) {
        mSubscriber = subscriber;
        eventBus.register(mSubscriber);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // View注解
        ViewUtils.inject(this, this);
        onLoading();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSubscriber != null) {
            eventBus.unregister(mSubscriber);
        }
    }

    /**
     * 广播事件,包括正在加载
     */
    private void postEvent(int eventId, Object obj) {
        Message msg = new Message();
        msg.what = eventId;
        msg.obj = obj;
        eventBus.post(msg);
    }

    @Override
    public void onClick(View view) {
        if (!isLoading) {
            onLoading();
        }
    }

    /**
     * 正在加载
     */
    public void onLoading()
    {
        onLoading(R.string.loading);
    }

    /**
     * 正在加载
     * @param obj
     */
    public void onLoading(Object obj)
    {
        onLoading(R.string.loading, obj);
    }

    /**
     * 正在加载
     * @param stringId 描述信息
     */
    public void onLoading(int stringId)
    {
        onLoading(stringId, null);
    }

    /**
     * 正在加载
     * @param stringId 描述信息
     * @param obj
     */
    public void onLoading(int stringId, Object obj)
    {
        onLoading(getResources().getString(stringId), obj);
    }

    /**
     * 正在加载
     * @param loadDesc 描述信息
     */
    public void onLoading(String loadDesc)
    {
        onLoading(loadDesc, null);
    }

    /**
     * 正在加载
     * @param loadDesc 描述信息
     * @param obj 传递的参数
     */
    public void onLoading(String loadDesc, Object obj) {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        tipText.setText(loadDesc);
        tipText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        // 发送广播通知注册者
        postEvent(R.id.onLoading, obj);
    }


    /**
     * 失败
     */
    public void onFailure() {
        onFailure(R.string.loading_failure);
    }

    /**
     * 失败
     * @param stringId 描述信息
     */
    public void onFailure(int stringId)
    {
        onFailure(getResources().getString(stringId));
    }

    /**
     * 失败
     * @param errorDesc 描述信息
     */
    public void onFailure(String errorDesc) {
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        tipText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.loading_error_tip), null, null, null);
        tipText.setText(errorDesc);
    }

    /**
     * 成功
     */
    public void onSuccess() {
        isLoading = false;
        setVisibility(View.GONE);
    }

}
