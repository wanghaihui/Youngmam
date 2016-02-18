package com.xiaobukuaipao.youngmam.http;

/**
 * Created by xiaobu1 on 15-5-11.
 */
public interface IEventLogic {
    /**
     * 注册一个订阅者
     */
    void register(Object receiver);

    /**
     * 解绑一个订阅者
     */
    void unregister(Object receiver);

    /**
     * 解绑所有订阅者
     */
    void unregisterAll();

    /**
     * 取消某一个网络请求
     */
    void cancel(Object tag);

    /**
     * 取消所有网络请求
     */
    void cancelAll();

    /**
     * 封装所有内容, post给订阅者
     */
    void onResult(int action, String key, Object response);
}
