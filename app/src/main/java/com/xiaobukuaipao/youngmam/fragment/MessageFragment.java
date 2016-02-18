package com.xiaobukuaipao.youngmam.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.LikeAndCommentActivity;
import com.xiaobukuaipao.youngmam.NewFriendsActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.RegisterAndLoginActivity;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.SystemMessageActivity;
import com.xiaobukuaipao.youngmam.domain.NotifyIndicatorEvent;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

/**
 * Created by xiaobu1 on 15-9-23.
 */
public class MessageFragment extends BaseHttpFragment {
    private static final String TAG = MessageFragment.class.getSimpleName();

    /**
     * 网络逻辑
     */
    private YoungEventLogic mEventLogic;

    // ActionBar
    protected YoungActionBar actionBar;

    private RelativeLayout friendsLayout;
    private RelativeLayout commentLayout;
    private RelativeLayout likeLayout;
    private RelativeLayout systemNotifyLayout;

    private int newFansCount;
    private int newCommentCount;
    private int newLikeCount;
    private int newSysNoticeCount;

    // EventBus
    private EventBus eventBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        this.view = view;
        mEventLogic = new YoungEventLogic(this);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        return view;
    }

    @Override
    public void initUIAndData() {
        // 添加ActionBar
        actionBar = (YoungActionBar) view.findViewById(R.id.action_bar);
        setYoungActionBar();

        friendsLayout = (RelativeLayout) view.findViewById(R.id.new_friend);
        commentLayout = (RelativeLayout) view.findViewById(R.id.comment);
        likeLayout = (RelativeLayout) view.findViewById(R.id.like);
        systemNotifyLayout = (RelativeLayout) view.findViewById(R.id.system_notify);

        ((ImageView) friendsLayout.findViewById(R.id.img_message)).setImageResource(R.mipmap.message_new_friend);
        ((TextView) friendsLayout.findViewById(R.id.txt_message)).setText(getResources().getString(R.string.message_new_friend));

        ((ImageView) commentLayout.findViewById(R.id.img_message)).setImageResource(R.mipmap.message_comment);
        ((TextView) commentLayout.findViewById(R.id.txt_message)).setText(getResources().getString(R.string.message_comment));

        ((ImageView) likeLayout.findViewById(R.id.img_message)).setImageResource(R.mipmap.message_like);
        ((TextView) likeLayout.findViewById(R.id.txt_message)).setText(getResources().getString(R.string.message_like));

        ((ImageView) systemNotifyLayout.findViewById(R.id.img_message)).setImageResource(R.mipmap.message_notify);
        ((TextView) systemNotifyLayout.findViewById(R.id.txt_message)).setText(getResources().getString(R.string.message_notify));

        setUIListeners();

        // 获取未读消息数
        mEventLogic.getUnreadCount();
    }

    private void setYoungActionBar() {
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_message));
        actionBar.setRightAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_all_read));

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readAll();
            }
        });
    }

    /**
     * 全部已读
     */
    private void readAll() {
        // 消息全部已读
        mEventLogic.readAllMessage();
    }

    private void setUIListeners() {
        friendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friends();
            }
        });

        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
            }
        });

        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like();
            }
        });

        systemNotifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systemNotify();
            }
        });
    }

    private void friends() {
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            ((TextView) friendsLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
            Intent intent = new Intent(MessageFragment.this.getActivity(), NewFriendsActivity.class);
            startActivity(intent);
        } else {
            // 跳到登录页
            Intent intent = new Intent(MessageFragment.this.getActivity(), RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    private void comment() {
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            ((TextView) commentLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
            Intent intent = new Intent(MessageFragment.this.getActivity(), LikeAndCommentActivity.class);
            intent.putExtra("type", LikeAndCommentActivity.TYPE_COMMENT);
            startActivity(intent);
        } else {
            // 跳到登录页
            Intent intent = new Intent(MessageFragment.this.getActivity(), RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    private void like() {
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            ((TextView) likeLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
            Intent intent = new Intent(MessageFragment.this.getActivity(), LikeAndCommentActivity.class);
            intent.putExtra("type", LikeAndCommentActivity.TYPE_LIKE);
            startActivity(intent);
        } else {
            // 跳到登录页
            Intent intent = new Intent(MessageFragment.this.getActivity(), RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    private void systemNotify() {
        SharedPreferences sp = this.getActivity().getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            ((TextView) systemNotifyLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
            Intent intent = new Intent(MessageFragment.this.getActivity(), SystemMessageActivity.class);
            startActivity(intent);
        } else {
            // 跳到登录页
            Intent intent = new Intent(MessageFragment.this.getActivity(), RegisterAndLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.get_unread_count:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    Log.d(TAG, "message unread datas : " + httpResult.getData());

                    if (jsonObject != null) {
                        newFansCount = jsonObject.getInteger(GlobalConstants.JSON_NEWFANSCOUNT);
                        if (newFansCount > 0) {
                            ((TextView) friendsLayout.findViewById(R.id.count_message)).setVisibility(View.VISIBLE);
                            ((TextView) friendsLayout.findViewById(R.id.count_message)).setText(String.valueOf(newFansCount));
                        } else {
                            ((TextView) friendsLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                        }
                        newCommentCount = jsonObject.getInteger(GlobalConstants.JSON_NEWCOMMENTCOUNT);
                        if (newCommentCount > 0) {
                            ((TextView) commentLayout.findViewById(R.id.count_message)).setVisibility(View.VISIBLE);
                            ((TextView) commentLayout.findViewById(R.id.count_message)).setText(String.valueOf(newCommentCount));
                        } else {
                            ((TextView) commentLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                        }
                        newLikeCount = jsonObject.getInteger(GlobalConstants.JSON_NEWLIKECOUNT);
                        if (newLikeCount > 0) {
                            ((TextView) likeLayout.findViewById(R.id.count_message)).setVisibility(View.VISIBLE);
                            ((TextView) likeLayout.findViewById(R.id.count_message)).setText(String.valueOf(newLikeCount));
                        } else {
                            ((TextView) likeLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                        }
                        newSysNoticeCount = jsonObject.getInteger(GlobalConstants.JSON_NEWSYSNOTICECOUNT);
                        if (newSysNoticeCount > 0) {
                            ((TextView) systemNotifyLayout.findViewById(R.id.count_message)).setVisibility(View.VISIBLE);
                            ((TextView) systemNotifyLayout.findViewById(R.id.count_message)).setText(String.valueOf(newSysNoticeCount));
                        } else {
                            ((TextView) systemNotifyLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case R.id.read_all_message:

                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    Log.d(TAG, "read all message : " + httpResult.getData());

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) == 0) {
                            // 此时, 操作成功, 代表全部已读
                            ((TextView) friendsLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                            ((TextView) commentLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                            ((TextView) likeLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                            ((TextView) systemNotifyLayout.findViewById(R.id.count_message)).setVisibility(View.GONE);
                        }
                    }
                }
                break;

            default:
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
}
