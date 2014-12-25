package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;

/**
 * Created by wind on 2014/12/24.
 */
public class UsersLoader extends DataLoader<User[]> {
    private final String[] mUsernames;

    public UsersLoader(Context context, String... usernames) {
        super(context);
        mUsernames = usernames;
    }

    @Override
    public User[] loadInBackground() {
        User[] users = new User[mUsernames.length];
        int i = 0;
        UserController uc = new UserController(getContext());
        for (String username: mUsernames) {
            users[i++] = uc.getUser(username);
        }
        return users;
    }
}
