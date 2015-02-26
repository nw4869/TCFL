package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.RankUser;
import com.nightwind.tcfl.controller.UserController;

import java.io.IOException;
import java.util.List;

/**
 * Created by root on 2/26/15.
 */
public class RankUserLoader extends DataLoader<List<RankUser>>{

    public static final String ARG_BEGIN = "arg_begin";
    public static final String ARG_END = "arg_end";
    private final int begin;
    private final int end;

    public RankUserLoader(Context context, int begin, int end) {
        super(context);
        this.begin = begin;
        this.end = end;
    }

    @Override
    public List<RankUser> loadInBackground() {
        List<RankUser> contestResult = null;
        try {
            contestResult = new UserController(getContext()).getContestResult(begin, end);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contestResult;
    }
}
