package com.nightwind.tcfl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;

import com.nightwind.tcfl.activity.FriendsActivity;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.fragment.ChatFragment;
import com.nightwind.tcfl.tool.ExampleUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

public class MyService extends Service {
    public static final String ARG_ACTION_TYPE = "arg_action_type";
    public static final int ARG_ACTION_TYPE_RECEIVE_MSG = 0;
    public static final int ARG_ACTION_TYPE_CHANGE_ONLINE_STATUS = 1;

    public MyService() {
    }

    private static final String MSG_TITLE = "msgTitle";
    private static final String MSG_CONTENT = "msgContent";
    private static final String MSG_TIME = "msgTime";

    private Intent getChatIntent(String title, String content) {

        Bundle bundle = new Bundle();
        bundle.putString(MSG_TITLE, title);
        bundle.putString("username", title);
        bundle.putBoolean("isFromNotification", true);
        bundle.putString(MSG_CONTENT, content);
        bundle.putLong(MSG_TIME, System.currentTimeMillis());

        Intent openIntent = new Intent(this, FriendsActivity.class);
        openIntent.putExtras(bundle);
        return openIntent;
    }


    private void showNotification(String title, String content, String tickerText) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        int icon = android.R.drawable.stat_notify_chat;
//        int icon = R.drawable.kenan;
        long when = System.currentTimeMillis() /*+ 2000*/;
        Notification n = new Notification(icon, tickerText, when);
        n.defaults = Notification.DEFAULT_SOUND;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        Intent openIntent = getChatIntent(title, content);

        PendingIntent pi = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        n.setLatestEventInfo(this, title, content, pi);
        nm.notify(0, n);

    }

    private void showJPushNotification(String title, String content, String ticker) {
        JPushLocalNotification ln = new JPushLocalNotification();
        ln.setBuilderId(1);
        ln.setContent(content);
        ln.setTitle(title);
        ln.setNotificationId(11111111);
        ln.setBroadcastTime(System.currentTimeMillis()/* + 1000 * 60 * 10*/);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "jpush");
        map.put("test", "111");
        JSONObject json = new JSONObject(map);
        ln.setExtras(json.toString());
        JPushInterface.addLocalNotification(getApplicationContext(), ln);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getIntExtra(ARG_ACTION_TYPE, ARG_ACTION_TYPE_RECEIVE_MSG) == ARG_ACTION_TYPE_RECEIVE_MSG) {
                String title = intent.getStringExtra(JPushInterface.EXTRA_TITLE);
                String content = intent.getStringExtra(JPushInterface.EXTRA_MESSAGE);
                if (ChatFragment.getChattingUsername().equals(title)) {
//                Intent intent1 = getChatIntent(title, content);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                startActivity(intent1);
                    Bundle bundle = intent.getExtras();
                    String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    Intent msgIntent = new Intent(FriendsActivity.MESSAGE_RECEIVED_ACTION);
                    msgIntent.putExtra(FriendsActivity.KEY_MESSAGE, content);
                    msgIntent.putExtra(FriendsActivity.KEY_TITLE, title);
                    if (!ExampleUtil.isEmpty(extras)) {
                        try {
                            JSONObject extraJson = new JSONObject(extras);
                            if (null != extraJson && extraJson.length() > 0) {
                                msgIntent.putExtra(FriendsActivity.KEY_EXTRAS, extras);
                            }
                        } catch (JSONException e) {
                        }
                    }
                    sendBroadcast(msgIntent);
                } else {
                    showNotification(title, content, content);
//            showJPushNotification(title, content, content);
                }
            } else if (intent.getIntExtra(ARG_ACTION_TYPE, ARG_ACTION_TYPE_RECEIVE_MSG) == ARG_ACTION_TYPE_CHANGE_ONLINE_STATUS){
                //向服务器发送登录状态改变
                boolean online = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                new SetOnlineTask().execute(online ? 1 : 0);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class SetOnlineTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            UserController uc = new UserController(getApplicationContext());
            return uc.setOnline(params[0]);
        }
    }
}
