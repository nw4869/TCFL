package com.nightwind.tcfl.bean;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wind on 2015/1/12.
 */
public class Neighbor {
    private int uid;
    private String username;
    private String sign;
    private String avatarUrl;
//    private double latitude;
//    private double longitude;
    private double distance;
    private String time;

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

//    public double getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(double latitude) {
//        this.latitude = latitude;
//    }
//
//    public double getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(double longitude) {
//        this.longitude = longitude;
//    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static List<Neighbor> fromJsonUserList(String json) {
        Gson gson = new Gson();
        Neighbor[] neighbors1 = gson.fromJson(json, Neighbor[].class);
        List<Neighbor> neighbors = new ArrayList<>();
        Collections.addAll(neighbors, neighbors1);
        return neighbors;
    }
}
