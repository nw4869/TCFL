package com.nightwind.tcfl.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;

import cn.jpush.android.api.JPushInterface;

public class TestActivity extends ActionBarActivity {

    private static final String MSG_TITLE = "msgTitle";
    private static final String MSG_CONTENT = "msgContent";
    private static final String MSG_TIME = "msgTime";


    private void showNotification(String title, String content, String tickerText) {
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        int icon = android.R.drawable.stat_notify_chat;
//        int icon = R.drawable.kenan;
        long when = System.currentTimeMillis() /*+ 2000*/;
        Notification n = new Notification(icon, tickerText, when);
        n.defaults = Notification.DEFAULT_SOUND;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        Bundle bundle = new Bundle();
        bundle.putString(MSG_TITLE, title);
        bundle.putString("username", title);
        bundle.putBoolean("isFromNotification", true);
        bundle.putString(MSG_CONTENT, content);
        bundle.putLong(MSG_TIME, when);

        Intent openIntent = new Intent(this, FriendsActivity.class);
        openIntent.putExtras(bundle);
        PendingIntent pi = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        n.setLatestEventInfo(this, title, content, pi);
        nm.notify(0, n);

    }

    public void onButtonClick(View v) {
        final String title = "nw";
        final String content = "HelloWorld";
        final String tickerText = "hello";
        showNotification(title, content, tickerText);
    }

    public void onButtonClick3(View v) {
        Auth auth = new Auth(this);

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                UserController uc = new UserController(getApplicationContext());
                User user = uc.getSelfUser();
                user.setInfo("hellO world");
                return uc.updateUser(user);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                Toast.makeText(getApplicationContext(), "success = " + aBoolean, Toast.LENGTH_SHORT).show();
            }
        }.execute(auth.getUsername());

//        String json = "{\"avatarUrl\":\"http://120.24.223.185/img/conan1.jpg\",\"email\":\"nw4869@gmail.com\",,\"info\":\"hellO world\",\"school\":\"GUET\",\"username\":\"nw\",\"work\":\"Google\",\"age\":20,\"edu\":0,\"level\":1,\"online\":1,\"sex\":0,\"uid\":15}";
        String json1 = "{\"avatarUrl\":\"http://120.24.223.185/img/conan1.jpg\",\"email\":\"nw4869@gmail.com\",\"info\":\"hellO world\",\"school\":\"GUET\",\"username\":\"nw\",\"work\":\"Google\",\"age\":20,\"edu\":0,\"level\":1,\"online\":1,\"sex\":0,\"uid\":15}";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView tv = new TextView(this);
        tv.setText("用户自定义打开的Activity");
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String title = bundle.getString(MSG_TITLE);
                String content = bundle.getString(MSG_CONTENT);
                tv.setText("Title : " + title + "  " + "Content : " + content);
                addContentView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            }
        }


        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setTextIsSelectable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
