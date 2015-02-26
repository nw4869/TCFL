package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class Register2Activity extends BaseActivity {

    private String username;
    private String password;
    private ProgressDialog mDialog;

    private EditText et_tel;
    private EditText et_code;
    private String tel;
    private int code;
    private Button btn_getCode;
    private static int sCountDown;
    private static boolean sCountDowning = false;

    @Override
    int getLayoutResID() {
        return R.layout.activity_register2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
        } else {
            finish();
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getString("username") != null) {
                username = savedInstanceState.getString("username");
            }
            if (savedInstanceState.getString("password") != null) {
                password = savedInstanceState.getString("password");
            }
        }

        et_tel = (EditText)findViewById(R.id.tel);
        et_code = (EditText)findViewById(R.id.code);
        btn_getCode = (Button) findViewById(R.id.getCode);

        initSMS();
        if (sCountDown > 0) {
            countDown(sCountDown);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("username", username);
        outState.putString("password", password);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initSMS() {

        SMSSDK.initSDK(this, "5d1eac0fa81a", "9bf4795785b0b3017dcd40a2fc449118");
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                smsHandler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
    }

    public void onGetCode(View view) {
        tel = String.valueOf(et_tel.getText());
        if (tel.length() == 11) {
            SMSSDK.getVerificationCode("86", tel);
            btn_getCode.setEnabled(false);
            btn_getCode.setText("正在获取验证码");
        } else {
            Toast.makeText(this, "手机号码的长度不为11", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRegister(View view) {
        tel = String.valueOf(et_tel.getText());
        String strCode = String.valueOf(et_code.getText());
        code = Integer.valueOf(strCode);
        if (tel.length() == 0 || strCode.length() == 0) {
            Toast.makeText(this, "手机号码和验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Auth auth = new Auth(this);
        auth.register(tel, code, username, password, handler);

        mDialog = new ProgressDialog(Register2Activity.this);
        mDialog.setTitle("注册");
        mDialog.setMessage("正在注册，请稍后...");
        mDialog.show();
    }

    //Handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Auth.MSG_REGISTER_SUCCESS:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "注册成功！" + msg.getData().getString("info"), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    break;
                case Auth.MSG_URL_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_CHECK_USERNAME_UN_AVAIL:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "用户名不可用", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_SERVER_BAN:
                    mDialog.cancel();
                    int errorTime = msg.arg1;
                    int minute = msg.arg2;
                    Toast.makeText(Register2Activity.this, "错误次数超过" + errorTime + ",请" + minute + "分钟后重试", Toast.LENGTH_SHORT).show();
                    break;
                case Auth.MSG_REGISTER_FILED:
                default:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "注册失败，请检查验证码", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };


    Handler smsHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                    btn_getCode.setText("已发送");
                    countDown(-1);
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();

                }
            } else {
                ((Throwable) data).printStackTrace();
            }

        }

    };

    private void countDown(final int t) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sCountDowning = true;
                sCountDown = t > 0 ? t : 60;
                while (sCountDown > 0 && sCountDowning) {
                    btn_getCode.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ForgetPwd", "btn_getCode = " + btn_getCode);
                            btn_getCode.setEnabled(false);
                            btn_getCode.setText("重新获取" + sCountDown);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sCountDown--;
                    Log.d("ForgetPwd", "countDown=" + sCountDown);
                }
                btn_getCode.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_getCode.setEnabled(true);
                        btn_getCode.setText("重新获取验证码");
                    }
                });
                sCountDowning = false;
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
}
