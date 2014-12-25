package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;

import java.util.List;

/**
 * Created by wind on 2014/12/24.
 */
public class FriendsLoader extends DataLoader<List<User>> {
    private final int mOnline;

    public FriendsLoader(Context context, int online) {
        super(context);
        mOnline = online;
    }

//    private String mUsername;

//    public FriendsLoader(Context context, String username) {
//        super(context);
//        mUsername = username;
//    }


    @Override
    public List<User> loadInBackground() {
        UserController uc = new UserController(getContext());
        return uc.getFriendList(mOnline);
    }
}
