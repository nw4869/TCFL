package com.nightwind.tcfl.bean;

import java.util.ArrayList;

/**
 * Created by wind on 2014/11/29.
 */
public class User {
    private int uid, level, age, sex, edu;
    private String username, password, salt, email, work, info, school, tel;
    private String hobby;
    private String avaterUrl;
    private ArrayList<Integer> friendsList = new ArrayList<>();

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


    public String getAvaterUrl() {
        return avaterUrl;
    }

    public void setAvaterUrl(String avaterUrl) {
        this.avaterUrl = avaterUrl;
    }

    public ArrayList<Integer> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<Integer> friendsList) {
        this.friendsList = friendsList;
    }


    /**
     * 添加好友
     * @param uid
     * @return
     */
    public boolean addFriend(int uid) {
        if (friendsList.contains(uid)) {
            return false;
        } else {
            //todo 检查用户是否存在
            friendsList.add(uid);
            return true;
        }
    }

    /**
     * 删除好友
     * @param uid
     * @return
     */
    public boolean delFriend(int uid) {
        if (!friendsList.contains(uid)) {
            return false;
        } else {
            friendsList.remove((Integer)uid);
            return true;
        }
    }
}
