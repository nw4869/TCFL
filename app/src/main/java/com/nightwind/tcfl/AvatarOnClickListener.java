package com.nightwind.tcfl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.nightwind.tcfl.activity.ProfileActivity;

/**
 * Created by wind on 2014/12/5.
 */

public class AvatarOnClickListener implements View.OnClickListener {

    private Context context;
    private String username;

    public AvatarOnClickListener(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("username", username);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
}