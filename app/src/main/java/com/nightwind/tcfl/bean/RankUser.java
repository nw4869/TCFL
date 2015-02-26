package com.nightwind.tcfl.bean;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 2/26/15.
 */
public class RankUser {
    private int uid;
    private String username;
    private String sign;
    private String avatarUrl;
    private int rank;
    private int count;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static List<RankUser> fromJsonUserList(String json) {
        Gson gson = new Gson();
        RankUser[] users = gson.fromJson(json, RankUser[].class);
        List<RankUser> userList = new ArrayList<>();
        Collections.addAll(userList, users);
        return userList;
    }
}
