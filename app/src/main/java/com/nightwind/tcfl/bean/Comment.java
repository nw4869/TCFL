package com.nightwind.tcfl.bean;

import android.graphics.Bitmap;

/**
 * Created by wind on 2014/11/28.
 */
public class Comment {
    private String content;
    private String username;
    private String dateTime;
    private int parentComment = 0;
    private int articleId;

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

    public int getParentComment() {
        return parentComment;
    }

    public void setParentComment(int parent) {
        this.parentComment = parent;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
}
