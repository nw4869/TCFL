package com.nightwind.tcfl.controller;

import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.Neighbor;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.oss.OSSController;
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
//            randGenUsers(50);
        }
    }

//    public void randGenUsers(int n) {
//        Random random = new Random();
//        for (int i = 1; i <= n; i++) {
//            int uid = i;
//            User user = new User();
//            user.setUid(uid);
//            if (uid == 1) {
//                user.setUsername("nw");
//            } else {
//                user.setUsername("user" + user.getUid());
//            }
//            user.setLevel(random.nextInt(100) + 1);
//            user.setAge(random.nextInt(60) + 1);
//            user.setInfo("Hello World");
//            user.setSex(random.nextInt(2));
//            user.setEdu(random.nextInt(User.eduNum));
//            user.setWork("IT");
//            user.setHobby("Programming");
//
//            //头像
//            user.setAvatarUrl(ServerConfig.getServer() +"img/conan" + uid + ".jpg");
//
//            //添加好友
//            int m = random.nextInt(n / 2) + 2;
//            for (int j = 0; j < m; j++) {
//                int friendUid = random.nextInt(n) + 1;
//                if (friendUid == uid || !user.addFriend(friendUid)) {
//                    j--;
//                    continue;
//                }
//            }
//            //在线状态
////            user.setOnline(uid % 2 == 0);
//
////            sUsersList.add(user);
//            sUidMap.put(uid, user);
//            sUsernameMap.put(user.getUsername(), user);
//        }
//    }

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
//        if (!insertToDB(user)) {
//            ok += 1 << 3;
//        }
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
        //debug停用缓存
//        User user = sUsernameMap.get(username);
//        //内存为空，从本地数据库读取
//        if (user == null) {
//            user = getUserFromDB(username);
//            //本地数据库也为空，从服务器获取
//            if (user == null) {
//                user = getUserFromServer(username);
//                if(user != null) {
//                    //写入本地数据库
//                    insertToDB(user);
//                } else {
//                    return null;
//                }
//            }
//
//            int uid = user.getUid();
//            //写入内存
//            sUidMap.put(uid, user);
//            sUsernameMap.put(user.getUsername(), user);
////            sUsersList.add(user);
//        }
        User user = getUserFromServer(username);
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

    /**
     *
     * @param isOnline
     * @return 失败返回null
     */
    public List<User> getFriendList(int isOnline) {
        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        List<User> friendList = null;

        String urlStr = ServerConfig.getServer() + "MyLogin/GetFriendList";
        HttpPost request = new HttpPost(urlStr);

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

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("isOnline", String.valueOf(isOnline)));


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
//                JSONObject jsonObject = new JSONObject(responseMsg);
//                if(jsonObject.getBoolean("success"))
                try {
                    friendList = User.fromJsonUserList(responseMsg);
                    //写入内存
//                    for (User user: friendList) {
//                        sUidMap.put(user.getUid(), user);
//                        sUsernameMap.put(user.getUsername(), user);
//                        saveUser(user);
//                    }
                } catch (Exception e) {
                    Log.e("GetFriendList", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return friendList;
    }

    public void closeDB() { mUDBMgr.closeDB();}

    public Boolean addFriend(String username2) {

        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/AddFriend";
        HttpPost request = new HttpPost(urlStr);

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

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("username2", username2));


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
                try {
                    JSONObject jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getBoolean("success")) {
                        //写入内存
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("AddFriend", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delFriend(String username2) {

        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/RemoveFriend";
        HttpPost request = new HttpPost(urlStr);

        if (token == null) {
            System.out.println("本地token不存在");
            return false;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return false;
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("username2", username2));

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
                try {
                    JSONObject jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getBoolean("success")) {
                        //写入内存
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("RemoveFriend", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setOnline(int online) {

        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();
        String regId = auth.getRegisterId();

        String urlStr = ServerConfig.getServer() + "MyLogin/UserOnline";
        HttpPost request = new HttpPost(urlStr);

        if (token == null) {
            System.out.println("本地token不存在");
            return false;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return false;
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("online", String.valueOf(online)));
        if (regId != null) {
            params.add(new BasicNameValuePair("regId", regId));
        }

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
                try {
                    JSONObject jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getBoolean("success")) {
                        //写入内存
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("setUserOnline", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(User user) {

        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/UpdateUser";
        HttpPost request = new HttpPost(urlStr);

        if (token == null) {
            System.out.println("本地token不存在");
            return false;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return false;
        }

        Gson gson = new Gson();
        String jsonExtra = gson.toJson(user);
        System.out.println("Update User jsonExtra = " + jsonExtra);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("jsonExtra", jsonExtra));

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
                try {
                    JSONObject jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getBoolean("success")) {
                        //写入内存
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("setUserOnline", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAvatar(String url) {
        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/FinishUpload";
        HttpPost request = new HttpPost(urlStr);

        if (token == null) {
            System.out.println("本地token不存在");
            return false;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return false;
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("url", url));
        params.add(new BasicNameValuePair("type", String.valueOf(OSSController.TYPE_UPLOAD_AVATAR)));

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
                try {
                    JSONObject jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getBoolean("success")) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("update avatar", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateLocation(double latitude, double longitude) {

        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/UpdateLocation";
        HttpPost request = new HttpPost(urlStr);

        if (token == null) {
            System.out.println("本地token不存在");
            return false;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return false;
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
        params.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));

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
                try {
                    JSONObject jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getBoolean("success")) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("update location", "JSON ERROR");
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return false;
    }


    public List<Neighbor> getNeighbor(double latitude, double longitude) {

        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        List<Neighbor> neighborList = null;

        String urlStr = ServerConfig.getServer() + "MyLogin/GetNeighbor";
        HttpPost request = new HttpPost(urlStr);

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

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
        params.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));


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
//                JSONObject jsonObject = new JSONObject(responseMsg);
//                if(jsonObject.getBoolean("success"))
                try {
                    neighborList = Neighbor.fromJsonUserList(responseMsg);
                } catch (Exception e) {
                    Log.e("GetFriendList", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return neighborList;
    }
}
