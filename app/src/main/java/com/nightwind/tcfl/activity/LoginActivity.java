package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.server.ServerConfig;

public class LoginActivity extends BaseActivity {

    private Button loginBtn;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog mDialog;

    static public final int RESULT_SUCCESS = 0;
    static public final int RESULT_FAILED = 1;

    private static final int REQUEST_REGISTER = 0;

    //Handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Auth.MSG_LOGIN_SUCCESS:
                    mDialog.cancel();
//                    Toast.makeText(getApplicationContext(), "登录成功！" + msg.getData().getString("info"), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_SUCCESS);
                    finish();
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    break;
                case Auth.MSG_LOGIN_PWD_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_URL_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_LOGIN_TOKEN_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "token验证失败", Toast.LENGTH_SHORT).show();
                    break;
//                case MSG_TOKEN_SUCCESS:
//                    mDialog.cancel();
//                    //todo
//                    Toast.makeText(getApplicationContext(), "token验证成功", Toast.LENGTH_SHORT).show();
//                    break;
                case Auth.MSG_SERVER_BAN:
                    mDialog.cancel();
                    int errorTime = msg.arg1;
                    int minute = msg.arg2;
                    Toast.makeText(LoginActivity.this, "错误次数超过" + errorTime + ",请" + minute + "分钟后重试", Toast.LENGTH_SHORT).show();
                    break;
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
                onClickLogin(ServerConfig.getServer());
            }
        });

        //TEST
        loginBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickLogin(ServerConfig.getServer());
                Toast.makeText(LoginActivity.this, "LOGIN_REMOTE", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        setResult(RESULT_FAILED);
    }

    private void onClickLogin(String server) {
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setTitle("登陆");
        mDialog.setMessage("正在登陆服务器，请稍后...");
        mDialog.show();
//        Thread loginThread = new Thread(new LoginThread());
//        loginThread.start();

        String username = String.valueOf(inputUsername.getText());
        String password = String.valueOf(inputPassword.getText());
        Auth auth = new Auth(this, server);
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
     * 菜单栏：注册用户activity返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RegisterActivity.RESULT_REGISTER_SUCCESS) {
                //注册（同时登录）登录，结束当前activity，返回成功
                setResult(RESULT_SUCCESS);
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
        } else if (id == R.id.action_register) {
            toRegister();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onToRegister(View v) {
        toRegister();
    }

    private void toRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    public void onToForgePwd(View v) {
        toForgePwd();
    }

    private void toForgePwd() {
        Intent intent = new Intent(this, ForgetPwdActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
}
