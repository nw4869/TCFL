package com.nightwind.tcfl.bean;

import android.graphics.Bitmap;

/**
 * Created by wind on 2014/11/28.
 */
public class CommentEntity {
    private String content;
    private String username;
    private String dateTime;
    private int parent = 0;

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
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
