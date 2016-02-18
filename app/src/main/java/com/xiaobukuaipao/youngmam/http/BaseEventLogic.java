package com.xiaobukuaipao.youngmam.http;

import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.HuaYoungApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by xiaobu1 on 15-5-11.
 */
public class BaseEventLogic implements IEventLogic {

    // 存储所有的订阅者
    private List<Object> subscribers = new ArrayList<Object>();

    // Volley请求队列
    private static RequestQueue requestQueue = Volley.newRequestQueue(HuaYoungApplication.getInstance().getApplicationContext());

    // 缺省的EventBus
    private EventBus mEventBus;

    // 请求的tags
    private Set<Object> requestTags = new HashSet<Object>();

    // 两种构造函数
    public BaseEventLogic(Object subscriber) {
        this(subscriber, new EventBus());
    }

    public BaseEventLogic(Object subscriber, EventBus mEventBus) {
        if (mEventBus == null) {
            this.mEventBus = EventBus.getDefault();
        } else {
            this.mEventBus = mEventBus;
        }

        register(subscriber);
    }

    /**
     * 注册
     */
    public void register(Object receiver) {
        if (!subscribers.contains(receiver)) {
            mEventBus.register(receiver);
            subscribers.add(receiver);
        }
    }

    /**
     * 取消注册
     */
    public void unregister(Object receiver) {
        if (subscribers.contains(receiver)) {
            mEventBus.unregister(receiver);
            subscribers.remove(receiver);
        }
    }

    /**
     * 取消所有注册
     */
    public void unregisterAll() {
        for (Object subscriber : subscribers) {
            mEventBus.unregister(subscriber);
        }

        subscribers.clear();
    }

    @Override
    public void cancel(Object tag) {
        requestQueue.cancelAll(tag);
    }

    /**
     * 取消所有网络请求
     */
    @Override
    public void cancelAll() {
        Iterator<Object> tagIterator = requestTags.iterator();
        while (tagIterator.hasNext()) {
            cancel(tagIterator.next());
        }
    }

    /**
     * 负责封装结果内容, post给订阅者
     */
    @Override
    public void onResult(int action, String key, Object response) {
        Message msg = new Message();
        msg.what = action;
        msg.obj = response;
        msg.getData().putString("key", key);
        mEventBus.post(msg);
    }

    //////////////////////////////////////////////////////////////////////

    public void onEventMainThread(Message msg) {
        mEventBus.post(msg);
    }

    //////////////////////////////////////////////////////////////////////

    protected <T> void sendRequest(Request<T> request, Object tag) {
        sendRequest(request, tag, requestQueue);
    }

    protected <T> void sendRequest(Request<T> request, Object tag, RequestQueue requestQueue) {
        if (tag != null) {
            request.setTag(tag);
            requestTags.add(tag);
        }

        requestQueue.add(request);
    }

    protected <T> void sendRequest(Request<T> request, RequestQueue requestQueue) {
        sendRequest(request, null, requestQueue);
    }

}
