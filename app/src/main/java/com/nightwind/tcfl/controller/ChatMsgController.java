package com.nightwind.tcfl.controller;

import android.content.Context;

import com.nightwind.tcfl.bean.ChatMsg;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wind on 2014/12/10.
 */
public class ChatMsgController {

    private static HashMap<Integer,ArrayList<ChatMsg>> sChatMsgMap = new HashMap<>();
    private final Context mContext;
    private final UserController mUserController;

//    static {
//        randGenChat();
//    }


    public ChatMsgController(Context context) {
        mContext = context;
        mUserController = new UserController(mContext);
    }

    public void randGenChat() {
        ArrayList<Integer> friendsUidList = mUserController.getSelfUser().getFriendsUidList();

        ArrayList<ChatMsg> chatMsg = new ArrayList<>();
        String[] msgArray = new String[]{"  孩子们，要好好学习，天天向上！要好好听课，不要翘课！不要挂科，多拿奖学金！三等奖学金的争取拿二等，二等的争取拿一等，一等的争取拿励志！",
                "姚妈妈还有什么吩咐...",
                "还有，明天早上记得跑操啊，不来的就扣德育分！",
                "德育分是什么？扣了会怎么样？",
                "德育分会影响奖学金评比，严重的话，会影响毕业",
                "哇！学院那么不人道？",
                "你要是你不听话，我当场让你不能毕业！",
                "姚妈妈，我知错了(- -我错在哪了...)"};

        String[]dataArray = new String[]{"2012-09-01 18:00", "2012-09-01 18:10",
                "2012-09-01 18:11", "2012-09-01 18:12",
                "2012-09-01 18:14", "2012-09-01 18:35",
                "2012-09-01 18:40", "2012-09-01 18:50"};
        final int COUNT = 8;


        for(int i = 0; i < COUNT; i++) {
            ChatMsg chatEntity = new ChatMsg();
            chatEntity.setDate(dataArray[i]);
            if (i % 2 == 0)
            {
                chatEntity.setName(mUserController.getUser(2).getUsername());
                chatEntity.setMsgType(true);
            }else{
                chatEntity.setName(mUserController.getUser(1).getUsername());
                chatEntity.setMsgType(false);
            }

            chatEntity.setText(msgArray[i]);
            chatMsg.add(chatEntity);
        }
        sChatMsgMap.put(friendsUidList.get(0), chatMsg);

        for (int i = 1; i < friendsUidList.size(); i++) {
            sChatMsgMap.put(friendsUidList.get(i), new ArrayList<>(chatMsg));
        }
    }

    /**
     * 获取自己与uid的聊天记录
     * @param uid 对方uid
     * @return
     */
    public ArrayList<ChatMsg> getMsg(int uid) {
        ArrayList<ChatMsg> chatMsg =  sChatMsgMap.get(uid);
        if (chatMsg == null) {
            chatMsg = new ArrayList<>();
            sChatMsgMap.put(uid, chatMsg);
        }
        return chatMsg;
    }

    public void closeDB() {
        mUserController.closeDB();
    }
}
