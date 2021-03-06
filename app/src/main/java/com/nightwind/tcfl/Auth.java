package com.nightwind.tcfl;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.ServerConfig;
import com.nightwind.tcfl.tool.ExampleUtil;
import com.nightwind.tcfl.tool.encryptionUtil.MD5Util;
import com.nightwind.tcfl.tool.encryptionUtil.RSAUtils;

import org.apache.http.HttpEntity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by wind on 2014/12/9.
 */
public class Auth {

    private Context mAppContext;
    private String mServer;
    private Handler mHandler;
    static private HttpClient mClient;
    private String mTel;

    private int mSMSCode;
    private String mUsername;
    private String mPassword;
    private String responseMsg = "";

    private String mSalt;
    protected Auth() {
        mClient = ServerConfig.getHttpClient();
        mServer = ServerConfig.getServer();
    }


    public Auth(Context context) {
        this();
        mAppContext = context.getApplicationContext();
    }

    /**
     *
     * @param context
     * @param server 服务器地址 如"http://192.168.1.123:8081/"
     */
    public Auth(Context context, String server) {
        this(context);
        mServer = server;
    }

    public String getServer() {
        return mServer;
    }

    public void setServer(String Server) {
        this.mServer = Server;
    }

    static public final int MSG_LOGIN_SUCCESS = 0;


    static public final int MSG_LOGIN_PWD_ERROR = 1;
    static public final int MSG_URL_ERROR = 2;
    static public final int MSG_LOGIN_TOKEN_ERROR = 3;
    static public final int MSG_LOGOUT_SUCCESS = 4;

    static public final int MSG_REGISTER_SUCCESS = 5;

    static public final int MSG_REGISTER_FILED = 6;
    static public final int MSG_CHECK_USERNAME_AVAIL = 7;

    static public final int MSG_CHECK_USERNAME_UN_AVAIL = 8;
    static public final int MSG_NETWORK_ERROR = 9;

    public static final int MSG_CHANGE_PWD_SUCCESS = 10;
    public static final int MSG_CHANGE_PWD_FAILED_CODE_ERROR = 11;
    public static final int MSG_CHANGE_PWD_FAILED_PHONE_ERROR = 12;

    public static final int MSG_SERVER_BAN = 13;

    //    private static String SERVER = SERVER0;

    public void checkUserName(final String username, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //todo
                String url = mServer + "MyLogin/CheckUserName";
                HttpPost request = new HttpPost(url);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));

                int msg = MSG_CHECK_USERNAME_UN_AVAIL;
                boolean available = false;
                try {
                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    HttpResponse response = mClient.execute(request);
                    responseMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
                    JSONObject jo = new JSONObject(responseMsg);
                    available = jo.getBoolean("available");
                    msg = available ? MSG_CHECK_USERNAME_AVAIL : MSG_CHECK_USERNAME_UN_AVAIL;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    msg = MSG_NETWORK_ERROR;
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(msg);
            }
        }).start();
    }

    public void changePwdByPhone(final String phone, final String code, final String password, final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = mServer + "MyLogin/ChangePwd";
                HttpPost request = new HttpPost(url);

                Message msg = handler.obtainMessage(MSG_CHANGE_PWD_FAILED_CODE_ERROR);

                //set params
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("phone", phone));
                params.add(new BasicNameValuePair("code", code));
                params.add(new BasicNameValuePair("password", password));
                try {
                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

                    //execute
                    HttpResponse response = mClient.execute(request);

                    //handle response
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        String responseMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
                        JSONObject jo = new JSONObject(responseMsg);
                        int errorCode = jo.getInt("errorCode");
                        if (errorCode == 0) {
                            msg.what = MSG_CHANGE_PWD_SUCCESS;
                        } else if (errorCode == 1) {
                            msg.what = MSG_CHANGE_PWD_FAILED_CODE_ERROR;
                        } else if (errorCode == 2) {
                            msg.what = MSG_CHANGE_PWD_FAILED_PHONE_ERROR;
                        } else if (errorCode == -1) {
                            msg.what = MSG_SERVER_BAN;
                            msg.arg1 = jo.getInt("errorTimes");
                            msg.arg2 = jo.getInt("minute");
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    msg.what = MSG_NETWORK_ERROR;
                    e.printStackTrace();
                }

                //send message
                handler.sendMessage(msg);
            }
        }).start();
    }


//    static public final int MSG_TOKEN_SUCCESS = 4;

    class RegisterThread implements Runnable {

        @Override
        public void run() {

            //信息反馈
            Message msg = mHandler.obtainMessage();

            String tel = mTel;
            int smsCode = mSMSCode;
            String username = mUsername;
            String password = mPassword;
            System.out.println("username=" + username + ":password=" + password);

            //获取PublicKey
            String cipherPwd;
            String strPublicKey = getPublicKey();
            System.out.println("公钥=" + strPublicKey);
            if (strPublicKey != null) {
                try {

                    //公钥加密
                    cipherPwd = RSAUtils.encrypt(password, strPublicKey);
                    System.out.println("密文：" + cipherPwd);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.print("加密失败");
                    msg.what = MSG_URL_ERROR;
                    mHandler.sendMessage(msg);
                    //跳出
                    return;
                }

                //URL合法，但是这一步并不验证返回结果
                boolean registerValidate = registerServer(tel, smsCode, username, cipherPwd);

                System.out.println("----------------------------bool is :" + registerValidate + "----------response:" + responseMsg);
                if (registerValidate) {
                    JSONObject jo = null;
                    boolean success = false;
                    int errorCode = -1;
                    String token = null;
                    User user = null;
                    try {
                        //解析服务器返回的json
                        jo = new JSONObject(responseMsg);
                        //获取状态
                        success = jo.getBoolean("success");
                        //获取sms errorCode
                        errorCode = jo.getInt("errorCode");
                        //获取token
                        if (success) {
                            token = jo.getString("token");
                            user = User.fromJson(responseMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (success) {
                        //注册成功，保存token，用户名
                        saveToken(token);
                        System.out.println("save token:" + token);
                        saveUsername(username);
                        UserController uc = new UserController(mAppContext);
                        uc.saveUser(user);

                        msg.what = MSG_REGISTER_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putString("info", responseMsg);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    } else {
                        if (errorCode == -2) {
                            msg.what = MSG_CHECK_USERNAME_UN_AVAIL;
                        } else if (errorCode >= 500) {
                            msg.what = MSG_REGISTER_FILED;
                        } else if (errorCode == -1) {
                            msg.what = MSG_SERVER_BAN;
                            try {
                                if (jo != null) {
                                    msg.arg1 = jo.getInt("errorTimes");
                                    msg.arg2 = jo.getInt("minute");
                                }
                            } catch (Exception e) {
                                msg.what = MSG_REGISTER_FILED;
                            }
                        }
                        mHandler.sendMessage(msg);
                    }

                } else {
                    msg.what = MSG_URL_ERROR;
                    mHandler.sendMessage(msg);
                }
            } else {
                msg.what = MSG_URL_ERROR;
                mHandler.sendMessage(msg);
            }
        }
    }

    private boolean registerServer(String phone, int mSMSCode, String username, String password) {
        boolean registerValidate = false;
        //使用apache HTTP客户端实现
        String urlStr = mServer + "MyLogin/Register";
        HttpPost request = new HttpPost(urlStr);
        //如果传递参数多的话，可以对传递的参数进行封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //添加用户名和密码
        if (phone != null) {
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("code", String.valueOf(mSMSCode)));
        }
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            //执行请求返回相应
            HttpResponse response = mClient.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                registerValidate = true;
                //获得响应信息
                responseMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(responseMsg);
//                String responseMsg = EntityUtils.toString(response.getEntity(), "GBK");
//                System.out.println(responseMsg);
//                responseMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
//                System.out.println(responseMsg);
//                responseMsg = EntityUtils.toString(response.getEntity(), "iso8859-1");
//                System.out.println(responseMsg);

//                StringBuffer strBuf = new StringBuffer();
//                HttpEntity httpEnt = response.getEntity();
//                InputStream inputStream = httpEnt.getContent();
//                String htmlCharset = "UTF-8";
//                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, htmlCharset));
//                String singleLine = "";
//                while ((singleLine = br.readLine()) != null) {
//                    strBuf.append(singleLine);
//                }
//                responseMsg = strBuf.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registerValidate;

    }




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
            Message msg = mHandler.obtainMessage();

            String username = mUsername;
            String password = mPassword;
            System.out.println("username=" + username + ":password=" + password);

            //获取PublicKey
            String cipherPwd;
            String strPublicKey = getPublicKey();
            System.out.println("公钥=" + strPublicKey);
            if (strPublicKey != null) {
                try {

                    //获取Salt
                    String salt = mSalt =  getSalt(username);
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
                    mHandler.sendMessage(msg);
                    //跳出
                    return;
                }

                //URL合法，但是这一步并不验证密码是否正确
                boolean loginValidate = loginServer(username, cipherPwd);
                System.out.println("----------------------------bool is :" + loginValidate + "----------response:" + responseMsg);
                if (loginValidate) {
                    JSONObject jo = null;
                    boolean success = false;
                    String token = null;
                    User user = null;
                    int uid = -1;
                    try {
                        //解析服务器返回的json
                        jo = new JSONObject(responseMsg);
                        //获取状态
                        success = jo.getBoolean("success");
                        if (success) {
                            //获取token
                            token = jo.getString("token");
                            uid = jo.getInt("uid");
                            user = User.fromJson(responseMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (success) {
                        //登录成功！！
//                        //从服务器获取token
//                        String token = responseMsg.replaceAll(".{0,100}\\btoken=", "").replaceAll("; ?\\b.{0,100}", "");
//                        saveToken(token);
//                        System.out.println("save token:" + token);
//                        //设置当前的用户
//                        saveUsername(username);
//                        UserController uc = new UserController(mAppContext);
//                        uc.setSelfUser(uc.getUser(username));
                        //登录成功！！，保存token，用户名
                        saveToken(token);
                        System.out.println("save token:" + token);
                        saveUsername(username);
                        saveUid(uid);
                        UserController uc = new UserController(mAppContext);
                        uc.saveUser(user);

                        //设置别名
//                        JPushInterface.setAlias(mAppContext, mSalt.replace("-", ""), mAliasCallback);


                        //登录成功将在setAlias后传送
                        msg.what = MSG_LOGIN_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putString("info", responseMsg);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    } else {
                        msg.what = MSG_LOGIN_PWD_ERROR;
                        try {
                            if (jo != null) {
                                int errorCode = jo.getInt("errorCode");
                                msg.arg1 = jo.getInt("errorTimes");
                                msg.arg2 = jo.getInt("minute");
                                msg.what = MSG_SERVER_BAN;
                            }

                        } catch (Exception ignored) {
                        }
                        mHandler.sendMessage(msg);
                    }

                } else {
                    msg.what = MSG_URL_ERROR;
                    mHandler.sendMessage(msg);
                }
            } else {
                msg.what = MSG_URL_ERROR;
                mHandler.sendMessage(msg);
            }
        }

    }


    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        public static final String TAG = "JPUSH SetAlias";

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);

                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_LOGIN_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putString("info", responseMsg);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);

                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(mAppContext)) {
//                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                        //重新加载
                        JPushInterface.setAlias(mAppContext, mSalt.replace("-", ""), mAliasCallback);

                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

//            ExampleUtil.showToast(logs, mAppContext);
        }

    };



    /**
     * 请求登录服务器
     * @param username
     * @param password
     * @return
     */
    private boolean loginServer(String username, String password) {

        String regId = getRegisterId();
        if (regId == null) {
            regId = JPushInterface.getRegistrationID(mAppContext);
            saveRegisterId(regId);
        }
        boolean loginValidate = false;
        //使用apache HTTP客户端实现
        String urlStr = mServer + "MyLogin/Login";
        HttpPost request = new HttpPost(urlStr);
        //如果传递参数多的话，可以对传递的参数进行封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //添加用户名和密码
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        if (regId != null) {
            params.add(new BasicNameValuePair("regId", regId));
        } else {
            Log.w("Login", "regId is null");
        }
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
            Message msg = mHandler.obtainMessage();

//            //取出token
//            String token = getToken();

//            //获取PublicKey
//            String cipherToken;
//            String strPublicKey = getPublicKey();
//            System.out.println("公钥=" + strPublicKey);

            if (getToken() != null) {
                if (checkToken()) {
                    if (responseMsg.contains("success")) {

                        msg.what = MSG_LOGIN_SUCCESS;
                        Bundle bundle = new Bundle();
                        bundle.putString("info", responseMsg);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    } else {
                        msg.what = MSG_LOGIN_TOKEN_ERROR;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    msg.what = MSG_URL_ERROR;
                    mHandler.sendMessage(msg);
                }
            } else {
                msg.what = MSG_LOGIN_TOKEN_ERROR;
                mHandler.sendMessage(msg);
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
        String urlStr = mServer + "MyLogin/Login";
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
    public String getPublicKey() {
        String urlStr = mServer + "MyLogin/GetPublicKey";
        HttpGet request = new HttpGet(urlStr);

        System.out.println("serverURL = " + urlStr);

        try {
            HttpResponse response = mClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (IOException  e) {
//            e.printStackTrace();
//            Looper.prepare();
//
//            Handler handler = new Handler()
//            {
//                public void handleMessage(Message msg)
//                {
//                    if (msg.what == 0) {
//                        Toast.makeText(mAppContext, R.string.net_failed,Toast.LENGTH_SHORT).show();
//                        getLooper().quit();
//                    }
//                }
//            };
//            handler.sendEmptyMessage(0);
//            Looper.loop();
        }
        return null;
    }

    /**
     * 从服务器获取Username的Salt
     * @param username
     * @return
     * @throws IOException
     */
    private String getSalt(String username) throws IOException {
        String url = mServer + "MyLogin/GetSalt";
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


    /**
     * 对外接口
     * 用户注册，提供账号密码，然后开启后台进程注册，信息反馈给handler
     * @param username
     * @param password
     * @param handler
     */
    public void register(String username, String password, Handler handler) {
        mUsername = username;
        mPassword = password;
        mHandler = handler;
        Thread registerThread = new Thread(new RegisterThread());
        registerThread.start();
    }

    public void register(String tel, int smsCode, String username, String password, Handler handler) {
        mTel = tel;
        mSMSCode = smsCode;
        mUsername = username;
        mPassword = password;
        mHandler = handler;
        Thread registerThread = new Thread(new RegisterThread());
        registerThread.start();
    }

    /**
     * 对外接口
     * 提供账号密码，然后开启后台进程登录，信息反馈给handler
     * @param username
     * @param password
     * @param handler
     */
    public void login(String username, String password, Handler handler) {
        mUsername = username;
        mPassword = password;
        mHandler = handler;
        Thread loginThread = new Thread(new LoginThread());
        loginThread.start();
    }

    /**
     * 对外接口
     * 向服务器发送检查上一次保存的token
     * @param handler
     */
    public void loginByToken(Handler handler) {
        mHandler = handler;
        Thread checkTokenThread = new Thread(new CheckTokenThread());
        checkTokenThread.start();
    }

    public boolean isLogin() {
//        SharedPreferences sp = context.getSharedPreferences("token", Activity.MODE_PRIVATE);
//        return sp.getBoolean("isLogin", false);
        return !(getUsername() == null || getUsername() == "");
    }

    public void logout(Handler handler) {
        mHandler = handler;

        UserController uc = new UserController(mAppContext);
        uc.setSelfUser(null);
        saveUsername(null);
        saveToken(null);
        requestLogout();
    }

    private void requestLogout() {
        //todo 向服务器反馈退出登录信息
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_LOGOUT_SUCCESS;
        mHandler.sendMessage(msg);
    }

//    public String getUploadToken(String date/*, String resource*/) throws IOException {
//        String url = "http://192.168.43.243:8081/MyLogin/GetUploadImgToken";
//
//        HttpPost request = new HttpPost(url);
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("username", getUsername()));
//        params.add(new BasicNameValuePair("token", getToken()));
//        params.add(new BasicNameValuePair("date", date));
////        params.add(new BasicNameValuePair("resource", resource));
//        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//
//        HttpResponse httpResponse = new DefaultHttpClient().execute(request);
//        String token = "";
//        if (httpResponse.getStatusLine().getStatusCode() == 200) {
//            token = EntityUtils.toString(httpResponse.getEntity());
//        }
//        System.out.println("token = " + token);
//        return token;
//    }


    /**
     * 向服务器申请上传令牌
     * @param type
     * @return json
     * @throws IOException
     */
    public String getUploadToken(int type) throws IOException {

        String token = getToken();
        //RSA加密token
        String strPublicKey = getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return "";
        }

//        String url = "http://192.168.1.123:8081/MyLogin/GetUploadImgToken1";
        String url = ServerConfig.getServer() +  "MyLogin/GetUploadImgToken1";
        url += "?username=" + URLEncoder.encode(getUsername(), "utf-8");
        url += "&token=" + URLEncoder.encode(cipherToken, "utf-8");
        url += "&type=" + URLEncoder.encode(String.valueOf(type), "utf-8");

        HttpGet request = new HttpGet(url);

        HttpResponse httpResponse = ServerConfig.getHttpClient().execute(request);
        String hrJson = "";
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            hrJson = EntityUtils.toString(httpResponse.getEntity());
            System.out.println("hrJson = " + hrJson);
        }
        return hrJson;
    }



//    private boolean isLogin() {
//        SharedPreferences sp = mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE);
//        return sp.getBoolean("isLogin", false);
//    }
//
//    private void setLogin(boolean ok) {
//        SharedPreferences sp = mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE);
//        sp.edit().putBoolean("isLogin", ok).commit();
//    }

    public String getUsername() {
        SharedPreferences sp = mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE);
        return sp.getString("username", null);
    }

    private void saveUsername(String username) {
        SharedPreferences sp = mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE);
        if (username == null || username.equals("")) {
            sp.edit().remove("username").apply();
        } else {
            sp.edit().putString("username", username).apply();
        }
    }

    public String getToken() {
        SharedPreferences sp = mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    private void saveToken(String token) {
        SharedPreferences sp = mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE);
        if (token == null || token.equals("")) {
            sp.edit().remove("token").apply();
        } else {
            sp.edit().putString("token", token).apply();
        }
    }

    public String getRegisterId() {
        SharedPreferences sp = mAppContext.getSharedPreferences("registerId", Activity.MODE_PRIVATE);
        return sp.getString("registerId", null);
    }

    public void saveRegisterId(String regId) {
        SharedPreferences sp = mAppContext.getSharedPreferences("registerId", Activity.MODE_PRIVATE);
        if (regId == null || regId.equals("")) {
            sp.edit().remove("registerId").apply();
        } else {
            sp.edit().putString("registerId", regId).apply();
        }
    }

    private SharedPreferences getTokenSp() {
        return mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE);
    }

    public void saveUid(int uid) {
        SharedPreferences sp = getTokenSp();
        if (uid == -1) {
            sp.edit().remove("uid").apply();
        } else {
            sp.edit().putInt("uid", uid).apply();
        }
    }

    public int getUid() {
        SharedPreferences sp = getTokenSp();
        return sp.getInt("uid", -1);
    }

    /**
     * 清理SharedPreferences中的token
     * */
    public void cleanToken() {
        mAppContext.getSharedPreferences("token", Activity.MODE_PRIVATE).edit().clear().commit();
    }
}
