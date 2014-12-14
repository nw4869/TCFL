package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;

/**
 * Created by wind on 2014/12/14.
 */
public class UserLoader extends DataLoader<User> {
    private String mUsername;

    public UserLoader(Context context, String username) {
        super(context);
        mUsername = username;
    }

    @Override
    public User loadInBackground() {
        UserController uc = new UserController(getContext());
        return uc.getUser(mUsername);
    }
}
