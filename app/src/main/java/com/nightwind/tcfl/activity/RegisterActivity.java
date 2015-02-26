package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.server.ServerConfig;

public class RegisterActivity extends BaseActivity {

    private Button registerBtn;
    private EditText inputUsername;
    private EditText inputPassword;
    private String username;
    private String password;
    private ProgressDialog mDialog;

    static public final int RESULT_REGISTER_SUCCESS = 0;
    static public final int RESULT_REGISTER_FAILED = 1;

    static public final int REQUEST_GET_SMS_CODE = 0;

    //Handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Auth.MSG_CHECK_USERNAME_AVAIL:
                    mDialog.cancel();
//                    Toast.makeText(getApplicationContext(), "用户名可用！" + msg.getData().getString("info"), Toast.LENGTH_SHORT).show();
//                    setResult(RESULT_REGISTER_SUCCESS);
//                    finish();
                    Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    RegisterActivity.this.startActivityForResult(intent, REQUEST_GET_SMS_CODE);
                    RegisterActivity.this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    break;
                case Auth.MSG_NETWORK_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_CHECK_USERNAME_UN_AVAIL:
                default:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "用户名不可用", Toast.LENGTH_SHORT).show();
                    inputUsername.setError("用户名不可用");
                    inputUsername.requestFocus();
                    break;
            }

        }
    };

    @Override
    int getLayoutResID() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        registerBtn = (Button) findViewById(R.id.action_register);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onClickRegister(ServerConfig.getServer());
                onNext();
            }
        });

        setResult(RESULT_REGISTER_FAILED);

    }

//    private void onClickRegister(String server) {
//        mDialog = new ProgressDialog(RegisterActivity.this);
//        mDialog.setTitle("登陆");
//        mDialog.setMessage("正在登陆服务器，请稍后...");
//        mDialog.show();
//
//        String username = String.valueOf(inputUsername.getText());
//        String password = String.valueOf(inputPassword.getText());
//        Auth auth = new Auth(this, server);
//        auth.register(username, password, handler);
//    }

    private void onNext() {
        mDialog = new ProgressDialog(RegisterActivity.this);
        mDialog.setTitle("登陆");
        mDialog.setMessage("正在检查用户名，请稍后...");
        mDialog.show();
        username = String.valueOf(inputUsername.getText());
        password = String.valueOf(inputPassword.getText());
        boolean ok = true;
        if (password.length() < 6) {
            inputPassword.setError("密码长度必须不少与6位");
            inputUsername.requestFocus();
            ok = false;
        }
        if (ok && username.length() == 0) {
            inputUsername.setError("用户名不能为空");
            inputUsername.requestFocus();
            ok = false;
        }
        if (ok) {
            new Auth(this).checkUserName(username, handler);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GET_SMS_CODE) {
            if (resultCode == Register2Activity.RESULT_OK) {
                setResult(RESULT_REGISTER_SUCCESS);
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            } else {
//                finish();
//                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
