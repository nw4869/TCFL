package com.nightwind.tcfl.controller;

import android.content.Context;

import com.google.gson.Gson;
import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.server.ServerConfig;
import com.nightwind.tcfl.tool.encryptionUtil.RSAUtils;
import com.nightwind.tcfl.tool.localDB.UserDBManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by wind on 2014/12/10.
 */
public class UserController {

    static private final int ORI_USER_NUM = 8;

//    public static final String SERVER_REMOTE = "http://120.24.223.185/";
//    public static final String SERVER_LOCAL_DEBUG = "http://192.168.1.123:8081/";

    static private User sSelfUser;

//    static private ArrayList<User> sUsersList = new ArrayList<>();
    static private HashMap<Integer, User> sUidMap = new HashMap<>();
    static private HashMap<String, User> sUsernameMap = new HashMap<>();

//    static {
//        randGenUsers(ORI_USER_NUM);
//        setSelfUser(sUidMap.get(1));
//    }

    private final Context mContext;
    private final UserDBManager mUDBMgr;

    public UserController(Context context) {
        mContext = context;
        mUDBMgr = new UserDBManager(mContext);
        if (sUidMap == null) {
            randGenUsers(50);
        }
    }

    public void randGenUsers(int n) {
        Random random = new Random();
        for (int i = 1; i <= n; i++) {
            int uid = i;
            User user = new User();
            user.setUid(uid);
            if (uid == 1) {
                user.setUsername("nw");
            } else {
                user.setUsername("user" + user.getUid());
            }
            user.setLevel(random.nextInt(100) + 1);
            user.setAge(random.nextInt(60) + 1);
            user.setInfo("Hello World");
            user.setSex(random.nextInt(2));
            user.setEdu(random.nextInt(User.eduNum));
            user.setWork("IT");
            user.setHobby("Programming");

            //头像
            user.setAvatarUrl(ServerConfig.getServer() +"img/conan" + uid + ".jpg");

            //添加好友
            int m = random.nextInt(n / 2) + 2;
            for (int j = 0; j < m; j++) {
                int friendUid = random.nextInt(n) + 1;
                if (friendUid == uid || !user.addFriend(friendUid)) {
                    j--;
                    continue;
                }
            }
            //在线状态
            user.setOnline(uid % 2 == 0);

//            sUsersList.add(user);
            sUidMap.put(uid, user);
            sUsernameMap.put(user.getUsername(), user);
        }
    }

    //debug
    public ArrayList<User> getAllDBUser() {
        return mUDBMgr.getAllUsers();
    }


//    static public ArrayList<User> getUsersList() {
//        if (sUsersList != null) {
//            return sUsersList;
//        } else {
//            sUsersList = new ArrayList<>();
//        }
//        return sUsersList;
//    }

    public User getUser(int uid) {
        User user = sUidMap.get(uid);
        //内存为空，从本地数据库读取
        if (user == null) {
            user = getUserFromDB(uid);
            //本地数据库也为空，从服务器获取
            if (user == null) {
//                user = getUserFromServer(uid);
//                //写入本地数据库
//                insertToDB(user);
                return null;
            }
            //写入内存
            sUidMap.put(uid, user);
            sUsernameMap.put(user.getUsername(), user);
//            sUsersList.add(user);
        }
        return user;
    }

    /**
     *
     * @param user
     * @return 0表示成功
     */
    public int saveUser(User user) {
        int ok = 0;
        //已在getUser的时候写入
//        if (sUidMap.put(user.getUid(), user) == null) {
//            ok += 1;
//        }
//        if (sUsernameMap.put(user.getUsername(), user) == null) {
//            ok += 1 << 2;
//        }
        if (!insertToDB(user)) {
            ok += 1 << 3;
        }
        return ok;
    }

    //todo
    private User getUserFromServer(int uid) {
        return null;
    }

    public User getUser(String username) {
        if (username == null || username.equals("")) {
            return null;
        }
        User user = sUidMap.get(username);
        //内存为空，从本地数据库读取
        if (user == null) {
            user = getUserFromDB(username);
            //本地数据库也为空，从服务器获取
            if (user == null) {
                user = getUserFromServer(username);
                if(user != null) {
                    //写入本地数据库
                    insertToDB(user);
                } else {
                    return null;
                }
            }

            int uid = user.getUid();
            //写入内存
            sUidMap.put(uid, user);
            sUsernameMap.put(user.getUsername(), user);
//            sUsersList.add(user);
        }
        return user;
    }


    public User getSelfUser() {
        if (sSelfUser == null) {
            sSelfUser = getUser(new Auth(mContext).getUsername());
        }
        return sSelfUser;
    }
    public void setSelfUser(User user) {
         sSelfUser = user;
    }

    static public int getUserCount() {
//        return sUsersList.size();
        return sUidMap.size();
    }

    private User getUserFromDB(int uid) {
        return mUDBMgr.getUser(uid);
    }
    private User getUserFromDB(String username) {
        return mUDBMgr.getUser(username);
    }
    public boolean insertToDB(User user) {
        return mUDBMgr.insertUser(user);
    }

    private User getUserFromServer(String requestUsername) {
        User user = null;

        Auth auth = new Auth(mContext);
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/GetUserInfo";
        HttpPost request = new HttpPost(urlStr);
        List<NameValuePair> params = new ArrayList<>();
        if (token == null) {
            System.out.println("本地token不存在");
            return null;
        }

        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return null;
        }

        String selfUsername = auth.getUsername();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", selfUsername));
        params.add(new BasicNameValuePair("requestUsername", requestUsername));

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(responseMsg);
                if(jsonObject.getBoolean("success")) {
                    user = User.fromJson(responseMsg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public void closeDB() { mUDBMgr.closeDB();}
}
