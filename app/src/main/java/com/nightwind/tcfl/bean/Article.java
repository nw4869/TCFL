package com.nightwind.tcfl.bean;

import java.util.ArrayList;

/**
 * Created by wind on 2014/11/28.
 */
public class Article {
    private int classify;
    private int id;
    private String title;
    private String newsAbstract;
    private String content;
//    private int uid;
    private String username;
    private String dateTime;
    private boolean collected;
//    private int commentNum;
//    private Bitmap img;
    private ArrayList<Comment> commentEntities = new ArrayList<>();

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

//    public int getUid() {
//        return uid;
//    }
//
//    public void setUid(int uid) {
//        this.uid = uid;
//    }

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

    public int getCommentNum() {
        return commentEntities.size() - 1;
    }

//    public void setCommentNum(int commentNum) {
//        this.commentNum = commentNum;
//    }

//    public Bitmap getImg() {
//        return img;
//    }
//
//    public void setImg(Bitmap img) {
//        this.img = img;
//    }

    public String getNewsAbstract() {
        if (newsAbstract == null) {
            if (content != null) {
                newsAbstract = content.substring(0, Math.min(20, content.length()));
            } else {
                newsAbstract = "";
            }
        }
        return newsAbstract;
    }

    public void setNewsAbstract(String NewsAbstract) {
        this.newsAbstract = NewsAbstract;
    }


    public ArrayList<Comment> getCommentEntities() {
        return commentEntities;
    }

    public void setCommentEntities(ArrayList<Comment> commentEntities) {
        this.commentEntities = commentEntities;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }
    //////////////////////////////////////////////////////////////////////

    public boolean addComment(Comment comment) {
        commentEntities.add(comment);
        return true;
    }

}
