package com.nightwind.tcfl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.Toast;

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
        Auth auth = new Auth(context);
        if (auth.getUsername() == null) {
            Toast.makeText(context, R.string.login_request, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.ARG_USERNAME, username);
        context.startActivity(intent);
    }
}