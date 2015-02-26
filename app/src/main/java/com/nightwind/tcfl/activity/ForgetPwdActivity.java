package com.nightwind.tcfl.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPwdActivity extends BaseActivity {

    private EditText et_phone;
    private EditText et_password;
    private EditText et_code;
    private Button btn_getCode;
    private static int sCountDown;
    private static boolean sCountDowning = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Auth.MSG_CHANGE_PWD_SUCCESS:
                    Toast.makeText(ForgetPwdActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    break;
                case Auth.MSG_CHANGE_PWD_FAILED_CODE_ERROR:
                    Toast.makeText(ForgetPwdActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    et_code.setError("验证码错误");
                    break;
                case Auth.MSG_CHANGE_PWD_FAILED_PHONE_ERROR:
                    Toast.makeText(ForgetPwdActivity.this, "手机号码不存在", Toast.LENGTH_SHORT).show();
                    et_phone.setError("手机号码不存在");
                    break;
                case Auth.MSG_SERVER_BAN:
                    int errorTime = msg.arg1;
                    int minute = msg.arg2;
                    Toast.makeText(ForgetPwdActivity.this, "错误次数超过" + errorTime + ",请" + minute + "分钟后重试", Toast.LENGTH_SHORT).show();
                    btn_getCode.setEnabled(false);
                    break;
                case Auth.MSG_NETWORK_ERROR:
                    Toast.makeText(ForgetPwdActivity.this, "网络链接失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    int getLayoutResID() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.et_phone = (EditText) findViewById(R.id.tel);
        this.et_password = ((EditText) findViewById(R.id.password));
        this.et_code = ((EditText) findViewById(R.id.code));

        this.btn_getCode = (Button) findViewById(R.id.getCode);

        initSMS();

        if (sCountDown > 0) {
            countDown(sCountDown);
        }
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

    public void onGetCode(View v) {

        String phone = this.et_phone.getText().toString();
        if (phone.length() != 11) {
            et_phone.setError("手机号码不等于11位");
        } else {
            SMSSDK.getVerificationCode("86", phone);
            btn_getCode.setEnabled(false);
            btn_getCode.setText("正在获取验证码");
        }
    }

    public void onChangePwd(View v) {
        String phone = this.et_phone.getText().toString();
        String password = this.et_password.getText().toString();
        String code = this.et_code.getText().toString();

        //check input correct
        boolean ok = true;
        if (phone.length() != 11) {
            et_phone.setError("手机号码不等于11位");
            ok = false;
        }
        if (password.length() < 6) {
            et_password.setError("密码必须不少于6为");
            ok = false;
        }
        if (code.length() == 0) {
            et_code.setError("验证码不能为空");
            ok = false;
        }

        //changePwd
        if (ok) {
            new Auth(this).changePwdByPhone(phone, code, password, handler);
        }
    }



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
