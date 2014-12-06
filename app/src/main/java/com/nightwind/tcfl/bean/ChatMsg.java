
package com.nightwind.tcfl.bean;

public class ChatMsg {
    private static final String TAG = ChatMsg.class.getSimpleName();
    //名字
    private String name;
    //日期
    private String date;
    //聊天内容
    private String text;
    //是否为对方发来的信息
    private boolean isComMeg = true;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getMsgType() {
        return isComMeg;
    }

    public void setMsgType(boolean isComMsg) {
    	isComMeg = isComMsg;
    }

    public ChatMsg() {
    }

    public boolean isComMeg() {
        return isComMeg;
    }

    public void setComMeg(boolean isComMeg) {
        this.isComMeg = isComMeg;
    }

    public ChatMsg(String name, String date, String text, boolean isComMeg) {
        this.name = name;
        this.date = date;
        this.text = text;
        this.isComMeg = isComMeg;
    }
}
