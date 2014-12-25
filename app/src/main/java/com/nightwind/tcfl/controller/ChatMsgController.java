package com.nightwind.tcfl.controller;

import android.content.Context;
import android.util.Log;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.bean.ChatMsg;
import com.nightwind.tcfl.server.ServerConfig;
import com.nightwind.tcfl.tool.encryptionUtil.RSAUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wind on 2014/12/10.
 */
public class ChatMsgController {

    private static HashMap<Integer,List<ChatMsg>> sChatMsgMap = new HashMap<>();
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

            chatEntity.setContent(msgArray[i]);
            chatMsg.add(chatEntity);
        }
        sChatMsgMap.put(friendsUidList.get(0), chatMsg);

        for (int i = 1; i < friendsUidList.size(); i++) {
            sChatMsgMap.put(friendsUidList.get(i), new ArrayList<>(chatMsg));
        }
    }

    public void setMsg(int uid, List<ChatMsg> chatMsgs) {
        sChatMsgMap.put(uid, chatMsgs);
    }

    /**
     * 获取自己与uid的聊天记录
     * @param uid 对方uid
     * @return
     */
    public List<ChatMsg> getMsg(int uid) {
        List<ChatMsg> chatMsg =  sChatMsgMap.get(uid);
        if (chatMsg == null) {
            chatMsg = new ArrayList<>();
            sChatMsgMap.put(uid, chatMsg);
        }
        return chatMsg;
    }

    public List<ChatMsg> getMsgFromServer(String username2) {
        List<ChatMsg> chatMsgList = new ArrayList<>();
        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/GetChatLog";
        HttpPost request = new HttpPost(urlStr);

        if (token == null) {
            System.out.println("本地token不存在");
            return chatMsgList;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return chatMsgList;
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("username2", username2));


        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
//                JSONObject jsonObject = new JSONObject(responseMsg);
//                if(jsonObject.getBoolean("success"))
                try {
                    chatMsgList = ChatMsg.fromJsonUserList(responseMsg);
                    //写入内存
//                    for (User user: friendList) {
//                        sUidMap.put(user.getUid(), user);
//                        sUsernameMap.put(user.getUsername(), user);
//                        saveUser(user);
//                    }
                } catch (Exception e) {
                    Log.e("GetChatLog", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chatMsgList;
    }

    public int sendMsg(String username2, String content) {

        Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/AddChatLog";
        HttpPost request = new HttpPost(urlStr);

        if (token == null) {
            System.out.println("本地token不存在");
            return -1;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return -1;
        }

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("username2", username2));
        params.add(new BasicNameValuePair("content", content));

        int id = -1;

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
                try {
                    JSONObject jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getBoolean("success")) {
                        //写入内存
                        id =  jsonObject.getInt("id");
                    }
                } catch (Exception e) {
                    Log.e("sendMsg", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

//    public ArrayList<ChatMsg> getMsg(String mUsername2) {
//        return null;
//    }

    public void closeDB() {
        mUserController.closeDB();
    }

}
