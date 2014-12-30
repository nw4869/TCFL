
package com.nightwind.tcfl.bean;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatMsg {
    private static final String TAG = ChatMsg.class.getSimpleName();

    public static final int MSG_IS_COMING = 1;
    public static final int MSG_IS_SELF = 0;

    //名字
    private String name;
    //日期
    private String date;
    //聊天内容
    private String content;
    //是否为对方发来的信息
    private int isComing = 1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String text) {
        this.content = text;
    }

    public boolean getMsgType() {
        return isComing == 1;
    }

    public void setMsgType(boolean isComMsg) {
    	isComing = (isComMsg==true?1:0) ;
    }

    public ChatMsg() {
    }

    public int getComMeg() {
        return isComing;
    }

    public void setComMeg(int isComMeg) {
        this.isComing = isComMeg;
    }


    public static List<ChatMsg> fromJsonUserList(String json) {
        Gson gson = new Gson();
        ChatMsg[] chatMsgs = gson.fromJson(json, ChatMsg[].class);
        ArrayList<ChatMsg> chatMsgs1 = new ArrayList<>();
        for(ChatMsg chatMsg: chatMsgs) {
            chatMsgs1.add(chatMsg);
        }
        return chatMsgs1;
    }
}
