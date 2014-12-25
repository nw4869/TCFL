package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.ChatMsg;
import com.nightwind.tcfl.controller.ChatMsgController;

import java.util.List;

/**
 * Created by wind on 2014/12/25.
 */
public class ChatMsgLoader extends DataLoader<List<ChatMsg>>{
    private final String mUsername;

    public ChatMsgLoader(Context context, String username) {
        super(context);
        mUsername = username;
    }

    @Override
    public List<ChatMsg> loadInBackground() {
        ChatMsgController cmc = new ChatMsgController(getContext());
        return cmc.getMsgFromServer(mUsername);
    }
}
