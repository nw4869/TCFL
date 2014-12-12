package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
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

public class RegisterActivity extends BaseActivity {

    private Button registerBtn;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog mDialog;

    static public final int RESULT_REGISTER_SUCCESS = 0;
    static public final int RESULT_REGISTER_FAILED = 1;


    //Handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Auth.MSG_REGISTER_SUCCESS:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "注册成功！" + msg.getData().getString("info"), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_REGISTER_SUCCESS);
                    finish();
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    break;
                case Auth.MSG_URL_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_REGISTER_FILED:
                default:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
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
                onClickRegister(Auth.SERVER_REMOTE);
            }
        });

        setResult(RESULT_REGISTER_FAILED);

    }

    private void onClickRegister(String server) {
        mDialog = new ProgressDialog(RegisterActivity.this);
        mDialog.setTitle("登陆");
        mDialog.setMessage("正在登陆服务器，请稍后...");
        mDialog.show();
//        Thread loginThread = new Thread(new LoginThread());
//        loginThread.start();

        String username = String.valueOf(inputUsername.getText());
        String password = String.valueOf(inputPassword.getText());
        Auth auth = new Auth(this, server);
        auth.register(username, password, handler);

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
