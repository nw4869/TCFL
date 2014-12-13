package com.nightwind.tcfl.bean;

import java.util.ArrayList;

/**
 * Created by wind on 2014/11/29.
 */
public class User {
    private int uid, level, age, sex, edu;
    private String username, password, salt, email, work, info, school, tel;
    private String hobby;
    private String avatarUrl;
    private ArrayList<Integer> friendsUidList = new ArrayList<>();
    private boolean online;

    static public int eduNum = 6;
    static public String[] eduStrings = {"本科", "硕士", "博士", "专科", "中学", "小学"};

    public String getEduString() {
        if (0 <= edu && edu <= eduNum) {
            return eduStrings[edu];
        } else {
            return eduStrings[0];
        }
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getEdu() {
        return edu;
    }

    public void setEdu(int edu) {
        this.edu = edu;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }


    public String getAvatarUrl() {
        if (avatarUrl == null) {
            return "";
        }
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public ArrayList<Integer> getFriendsUidList() {
        return friendsUidList;
    }

    public void setFriendsUidList(ArrayList<Integer> friendsUidList) {
        this.friendsUidList = friendsUidList;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public User() {
    }

    public User(int uid, int level, int age, int sex, int edu, String username, String password, String salt, String email, String work, String info, String school, String tel, String hobby, String avatarUrl, ArrayList<Integer> friendsUidList, boolean online) {
        this.uid = uid;
        this.level = level;
        this.age = age;
        this.sex = sex;
        this.edu = edu;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.work = work;
        this.info = info;
        this.school = school;
        this.tel = tel;
        this.hobby = hobby;
        this.avatarUrl = avatarUrl;
        this.friendsUidList = friendsUidList;
        this.online = online;
    }

    /**
     * 添加好友
     * @param uid
     * @return
     */
    public boolean addFriend(int uid) {
        if (friendsUidList.contains(uid)) {
            return false;
        } else {
            //todo 检查用户是否存在
            friendsUidList.add(uid);
            return true;
        }
    }

    /**
     * 删除好友
     * @param uid
     * @return
     */
    public boolean delFriend(int uid) {
        if (!friendsUidList.contains(uid)) {
            return false;
        } else {
            friendsUidList.remove((Integer)uid);
            return true;
        }
    }
}
