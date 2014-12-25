package com.nightwind.tcfl.bean;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wind on 2014/11/28.
 */
public class Comment {
    int id;
    private String content;
    private String username;
    private String dateTime;
    private int parentId = -1;
    private int articleId;
    private String avatarUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parent) {
        this.parentId = parent;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public static Comment fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Comment.class);
    }

    public static List<Comment> fromJsonCommentList(String json) {
        Gson gson = new Gson();
//        return gson.fromJson(json, List.class);
        Comment[] comments = gson.fromJson(json, Comment[].class);
        List<Comment> commentList = new ArrayList<>();
        for(Comment comment: comments) {
            commentList.add(comment);
        }
        return commentList;
    }
}
