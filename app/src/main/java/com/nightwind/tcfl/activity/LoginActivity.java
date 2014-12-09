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
//    private ToggleButton tb;
    private String responseMsg = "";
    private static final int REQUEST_TIMEOUT = 5 * 1000;//设置请求超时10秒钟
    private static final int SO_TIMEOUT = 10 * 1000;  //设置等待数据超时时间10秒钟
    private static final int LOGIN_OK = 1;
    private static final String SERVER1 = "http://nw48692.s155.eatj.com/";
    private static final String SERVER0 = "http://192.168.1.123:8081/";
    private static String SERVER = SERVER0;
    private HttpClient mClient;

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

        mClient = getHttpClient();
    }




    /**
     * 初始化HttpClient，并设置超时
     * @return
     */
    public HttpClient getHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpParams);
        return client;
    }

    static public final int MSG_SUCCESS = 0;
    static public final int MSG_PWD_ERROR = 1;
    static public final int MSG_URL_ERROR = 2;
    static public final int MSG_TOKEN_ERROR = 3;
//    static public final int MSG_TOKEN_SUCCESS = 4;

    //Handler
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "登录成功！" + msg.getData().getString("info"), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    intent.setClass(LoginDemoActivity.this, MainActivity.class);
//                    startActivity(intent);
                    setResult(MSG_SUCCESS);
                    finish();
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    break;
                case MSG_PWD_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_URL_ERROR:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "URL验证失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_TOKEN_ERROR:
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

    //LoginThread线程类

    /**
     * 后台进程：获取输入的账号密码，获取公钥和Salt，然后MD5加密输入的密码
     * 然后调用loginServer登录服务器取得相应数据
     * 通过handler回馈给主线程
     */
    class LoginThread implements Runnable {

        @Override
        public void run() {

            //信息反馈
            Message msg = handler.obtainMessage();

            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();
            System.out.println("username=" + username + ":password=" + password);

            //获取PublicKey
            String cipherPwd;
            String strPublicKey = getPublicKey();
            System.out.println("公钥=" + strPublicKey);
            if (strPublicKey != null) {
                try {

                    //获取Salt
                    String salt = getSalt(username);
                    System.out.println("Salt=" + salt);

                    //MD5加密
                    String MD5Pwd = MD5Util.encode(username + password + salt);
                    System.out.println("MD5Pwd=" + MD5Pwd);

                    //公钥加密
                    cipherPwd = RSAUtils.encrypt(MD5Pwd, strPublicKey);
                    System.out.println("密文：" + cipherPwd);

//                    //debug:my解密
//                    String strPrvKey = getPrivateKey();
//                    String target = RSAUtils.decrypt(cipherPwd, strPrvKey);
//                    System.out.println("本地加解密结果：" + target);

//                    //debug:RSATest
//                    System.out.println("RSATest=" + RSATest());

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.print("加密失败");
                    msg.what = MSG_URL_ERROR;
                    handler.sendMessage(msg);
                    //跳出
                    return;
                }

                //URL合法，但是这一步并不验证密码是否正确
                boolean loginValidate = loginServer(username, cipherPwd);
                System.out.println("----------------------------bool is :" + loginValidate + "----------response:" + responseMsg);
                if (loginValidate) {
                    if (responseMsg.contains("success")) {

                        //从服务器获取token
                        String token = responseMsg.replaceAll(".{0,100}\\btoken=", "").replaceAll("; ?\\b.{0,100}", "");
                        saveToken(token);
                        System.out.println("save token:" + token);
                        saveUsername(username);

                        msg.what = MSG_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putString("info", responseMsg);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        msg.what = MSG_PWD_ERROR;
                        handler.sendMessage(msg);
                    }

                } else {
                    msg.what = MSG_URL_ERROR;
                    handler.sendMessage(msg);
                }
            } else {
                msg.what = MSG_URL_ERROR;
                handler.sendMessage(msg);
            }
        }

    }


    /**
     * 请求登录服务器
     * @param username
     * @param password
     * @return
     */
    private boolean loginServer(String username, String password) {
        boolean loginValidate = false;
        //使用apache HTTP客户端实现
        String urlStr = SERVER + "MyLogin/Login";
        HttpPost request = new HttpPost(urlStr);
        //如果传递参数多的话，可以对传递的参数进行封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //添加用户名和密码
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//            HttpClient client = getHttpClient();
            //执行请求返回相应
            HttpResponse response = mClient.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                loginValidate = true;
                //获得响应信息
                responseMsg = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginValidate;
    }


    /**
     * checkToken调用的后台线程
     */
    class CheckTokenThread implements Runnable {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();

//            //取出token
//            String token = getToken();

//            //获取PublicKey
//            String cipherToken;
//            String strPublicKey = getPublicKey();
//            System.out.println("公钥=" + strPublicKey);

            if (getToken() != null) {
                if (checkToken()) {
                    if (responseMsg.contains("success")) {

                        msg.what = MSG_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putString("info", responseMsg);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        msg.what = MSG_TOKEN_ERROR;
                        handler.sendMessage(msg);
                    }
                } else {
                    msg.what = MSG_URL_ERROR;
                    handler.sendMessage(msg);
                }
            } else {
                msg.what = MSG_TOKEN_ERROR;
                handler.sendMessage(msg);
            }
        }
    }

    /**
     * 将token发送给服务器检查token是否有效，token发送前经过RSA加密
     * @return
     */
    private boolean checkToken() {
        boolean checkValidate = false;
        //使用apache HTTP客户端实现
        String urlStr = SERVER + "MyLogin/Login";
        HttpPost request = new HttpPost(urlStr);
        //如果传递参数多的话，可以对传递的参数进行封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //添加token
        String token = getToken();
        if (token == null) {
            System.out.println("本地token不存在");
            return false;
        }
        //RSA加密token
        String strPublicKey = getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return false;
        }

        String username = getUsername();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//            HttpClient client = getHttpClient();
            //执行请求返回相应
            HttpResponse response = mClient.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                responseMsg = EntityUtils.toString(response.getEntity());
                checkValidate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkValidate;
    }


    /**
     * 从服务器获取公钥
     * @return
     */
    private String getPublicKey() {
        String urlStr = SERVER + "MyLogin/GetPublicKey";
        HttpGet request = new HttpGet(urlStr);

        System.out.println("serverURL = " + urlStr);

        try {
            HttpResponse response = mClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从服务器获取Username的Salt
     * @param username
     * @return
     * @throws IOException
     */
    public String getSalt(String username) throws IOException {
        String url = SERVER + "MyLogin/GetSalt";
        HttpPost request = new HttpPost(url);

        //如果传递参数多的话，可以对传递的参数进行封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //添加用户名和密码
        params.add(new BasicNameValuePair("username", username));
        //设置请求参数项
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        HttpResponse response = mClient.execute(request);

        String salt = EntityUtils.toString(response.getEntity());
        return salt;
    }

    private void onClickLogin() {
//        switch (v.getId()) {
//            case R.id.login:
//                SERVER = SERVER0;
//                break;
//            case R.id.login_remote:
//                SERVER = SERVER1;
//                break;
//        }
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setTitle("登陆");
        mDialog.setMessage("正在登陆服务器，请稍后...");
        mDialog.show();
        Thread loginThread = new Thread(new LoginThread());
        loginThread.start();
    }

//    private void onClickCheckToken(View v) {
//        mDialog = new ProgressDialog(LoginActivity.this);
//        mDialog.setTitle("登陆");
//        mDialog.setMessage("正在登陆服务器，请稍后...(CHECK TOKEN)");
//        mDialog.show();
//        Thread checkTokenThread = new Thread(new CheckTokenThread());
//        checkTokenThread.start();
//    }

    private String getUsername() {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        return sp.getString("username", null);
    }

    private void saveUsername(String token) {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        sp.edit().putString("username", token).commit();
    }

    private String getToken() {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        return sp.getString("token", null);
    }

    private void saveToken(String token) {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        sp.edit().putString("token", token).commit();
    }

    /**
     * 清理SharedPreferences中的token
     * @param v
     */
    public void cleanToken(View v) {
        getSharedPreferences("token", MODE_PRIVATE).edit().clear().commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
