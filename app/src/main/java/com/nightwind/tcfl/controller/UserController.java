package com.nightwind.tcfl.controller;

import com.nightwind.tcfl.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by wind on 2014/12/10.
 */
public class UserController {

    static private final int ORI_USER_NUM = 8;

    static private User sSelfUser;

    static private ArrayList<User> sUsersList = new ArrayList<>();
    static private HashMap<Integer, User> sUidMap = new HashMap<>();
    static private HashMap<String, User> sUsernameMap = new HashMap<>();

    static {
        randGenUsers(ORI_USER_NUM);
        setSelfUser(getUser(1));
    }

    static public void randGenUsers(int n) {
        Random random = new Random();
        for (int i = 1; i <= n; i++) {
            int uid = i;
            User user = new User();
            user.setUid(uid);
            user.setUsername("user" + user.getUid());
            user.setLevel(random.nextInt(100) + 1);
            user.setAge(random.nextInt(60) + 1);
            user.setInfo("Hello World");
            user.setSex(random.nextInt(2));
            user.setEdu(random.nextInt(User.eduNum));
            user.setWork("IT");
            user.setHobby("Programming");

            //头像
            user.setAvatarUrl("http://192.168.1.123/img/conan" + uid + ".jpg");

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

            sUsersList.add(user);
            sUidMap.put(uid, user);
            sUsernameMap.put(user.getUsername(), user);
        }
    }


    static public ArrayList<User> getUsersList() {
        if (sUsersList != null) {
            return sUsersList;
        } else {
            sUsersList = new ArrayList<>();
        }
        return sUsersList;
    }

    static public User getUser(int uid) {
        return sUidMap.get(uid);
    }
    static public User getUser(String username) {
        return sUsernameMap.get(username);
    }
    static public User getSelfUser() {
        return sSelfUser;
    }
    static public void setSelfUser(User user) {
         sSelfUser = user;
    }

    static public int getUserCount() { return sUsersList.size();}
}
