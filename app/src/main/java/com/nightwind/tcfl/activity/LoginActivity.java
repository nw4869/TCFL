package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.tool.encryptionUtil.MD5Util;
import com.nightwind.tcfl.tool.encryptionUtil.RSAUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private Button loginBtn;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog mDialog;

//    static public final int MSG_SUCCESS = 0;
//    static public final int MSG_PWD_ERROR = 1;
//    static public final int MSG_URL_ERROR = 2;
//    static public final int MSG_TOKEN_ERROR = 3;
//    static public final int MSG_TOKEN_SUCCESS = 4;

    static public final int RESULT_SUCCESS = 0;
    static public final int RESULT_FAILED = 1;

    //Handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Auth.MSG_SUCCESS:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "登录成功！" + msg.getData().getString("info"), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_SUCCESS);
                    finish();
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    break;
                case Auth.MSG_PWD_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_URL_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_TOKEN_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "token验证失败", Toast.LENGTH_SHORT).show();
                    break;
//                case MSG_TOKEN_SUCCESS:
//                    mDialog.cancel();
//                    //todo
//                    Toast.makeText(getApplicationContext(), "token验证成功", Toast.LENGTH_SHORT).show();
//                    break;
            }

        }
    };


    @Override
    int getLayoutResID() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });

        setResult(RESULT_FAILED);
    }

    private void onClickLogin() {
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setTitle("登陆");
        mDialog.setMessage("正在登陆服务器，请稍后...");
        mDialog.show();
//        Thread loginThread = new Thread(new LoginThread());
//        loginThread.start();

        String username = String.valueOf(inputUsername.getText());
        String password = String.valueOf(inputPassword.getText());
        Auth auth = new Auth(this);
        auth.login(username, password, handler);

    }

//    private void onClickCheckToken(View v) {
//        mDialog = new ProgressDialog(LoginActivity.this);
//        mDialog.setTitle("登陆");
//        mDialog.setMessage("正在登陆服务器，请稍后...(CHECK TOKEN)");
//        mDialog.show();
//        Thread checkTokenThread = new Thread(new CheckTokenThread());
//        checkTokenThread.start();
//    }


    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * 菜单选择
     * @param item
     * @return
     */
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
