package com.nightwind.tcfl.controller;

import android.content.Context;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.tool.localDB.UserDBManager;

import java.util.ArrayList;
import java.util.HashMap;
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
            user.setAvatarUrl(Auth.SERVER_REMOTE +"img/conan" + uid + ".jpg");

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
//                user = getUserFromServer(username);
//                //写入本地数据库
//                insertToDB(user);
                return null;
            }

            int uid = user.getUid();
            //写入内存
            sUidMap.put(uid, user);
            sUsernameMap.put(user.getUsername(), user);
//            sUsersList.add(user);
        }
        return user;
    }

    private User getUserFromServer(String username) {
        return null;
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

    public void closeDB() { mUDBMgr.closeDB();}
}
