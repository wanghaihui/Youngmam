package com.xiaobukuaipao.youngmam.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.alibaba.fastjson.JSONObject;
import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.HuaYoungActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SettingActivity;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.domain.NotifyIndicatorEvent;
import com.xiaobukuaipao.youngmam.message.YoungMessage;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

/**
 * Created by xiaobu1 on 15-5-22.
 */
public class YoungNotificationManager {

    private Context context;
    private NotificationManager notificationManager;

    private static final YoungNotificationManager youngNotificationManager = new YoungNotificationManager();

    private YoungNotificationManager() {

    }

    public static YoungNotificationManager getInstance() {
        return youngNotificationManager;
    }

    public void setContext(Context context) {
        if (context == null) {
            throw new RuntimeException("context is null");
        }
        this.context = context;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 显示推送的通知
     */
    public void showNotify(String message) {
        // 首先判断是否允许发送推送通知
        SharedPreferences sp = context.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);

        // 如果用户允许推送系统通知
        if (sp.getBoolean(SettingActivity.PUSH_STATE, true)) {

            JSONObject jsonObject = (JSONObject) JSONObject.parse(message);

            if (notificationManager == null) {
                return;
            }

            if (jsonObject == null) {
                return;
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.app_cn_name))
                    .setContentText(jsonObject.getString(GlobalConstants.JSON_CONTENT))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND);

            long[] vibrate = {0, 200, 250, 200};
            builder.setVibrate(vibrate);

            Intent resultIntent = new Intent(context, HuaYoungActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 此时,显示Notification
            EventBus.getDefault().post(new NotifyIndicatorEvent(true));

            switch (jsonObject.getInteger(GlobalConstants.JSON_TYPE)) {
                case YoungMessage.MSG_TYPE_NEW_FANS_APPLY:
                case YoungMessage.NOTIFY_TYPE_COMMON_MSG:
                    resultIntent.putExtra(GlobalConstants.JSON_PAGE, 2);
                    break;
                case YoungMessage.COMMON_MSG:
                    resultIntent.putExtra(GlobalConstants.JSON_PAGE, 0);
                    break;

                case YoungMessage.MSG_TYPE_ARTICLE_COMMENT:
                case YoungMessage.MSG_TYPE_ARTICLE_COMMENT_REPLY:
                case YoungMessage.MSG_TYPE_QUESTION_COMMENT:
                case YoungMessage.MSG_TYPE_QUESTION_COMMENT_REPLY:
                case YoungMessage.MSG_TYPE_SPECIAL_COMMENT_REPLY:
                case YoungMessage.MSG_TYPE_H5_WEBPAGE_COMMENT_REPLY:

                case YoungMessage.MSG_TYPE_ARTICLE_LIKE:
                case YoungMessage.MSG_TYPE_ARTICLE_COMMENT_LIKE:
                case YoungMessage.MSG_TYPE_QUESTION_COMMENT_LIKE:
                case YoungMessage.MSG_TYPE_SPECIAL_COMMENT_LIKE:
                case YoungMessage.MSG_TYPE_H5_WEBPAGE_COMMENT_LIKE:

                case YoungMessage.NOTIFY_TYPE_ACTIVITY_ACTIVE:
                case YoungMessage.NOTIFY_TYPE_SPECIAL_PUBLISH:
                case YoungMessage.NOTIFY_TYPE_WEB_ACTIVITY_ACTIVE:
                case YoungMessage.NOTIFY_TYPE_WEBPAGE_PUBLISH:
                case YoungMessage.NOTIFY_TYPE_URL_PUBLISH:
                case YoungMessage.NOTIFY_TYPE_THEME_PUBLISH:
                case YoungMessage.NOTIFY_TYPE_ARTICLE_PUBLISH:
                    resultIntent.putExtra(GlobalConstants.JSON_PAGE, 0);
                    resultIntent.putExtra("message", message);
                    break;
            }

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(HuaYoungActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);

            // mId allows you to update the notification later on.
            notificationManager.notify(Integer.parseInt(jsonObject.getString(GlobalConstants.JSON_TYPE)), builder.build());

        }
    }
}
