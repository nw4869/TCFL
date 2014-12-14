package com.nightwind.tcfl.activity;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.UserLoader;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ProfileActivity extends BaseActivity {
    public static final String ARG_UID = "uid";
    public static final String ARG_USERNAME = "username";
    private static final int LOAD_USER = 0;

//    private Toolbar mToolbar;
//    private GestureDetector mGestureDetector;
//
//    private final int verticalMinDistance = 50;
//    private final int minVelocity = 0;

    private User mUser;

    private ImageView mIVAvatar;
    private TextView mTVUsername;
    private TextView mTVSign;
    private TextView mTVLevel;
    private TextView mTVAge;
    private TextView mTVSex;
    private TextView mTVWork;
    private TextView mTVEdu;
    private TextView mTVHobby;

    private ViewGroup mVGStartChat;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private UserController mUserController;


    @Override
    int getLayoutResID() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserController = new UserController(this);
        init();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void init() {
        String username = new Auth(this).getUsername();
        if (getIntent() != null) {
            String tmpUsername = getIntent().getStringExtra("username");
            if (tmpUsername != null) {
                Log.d("ProfileActivity getIntent", username);
                username = tmpUsername;
            }
        }
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOAD_USER, args, new UserLoaderCallbacks());


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_LOGIN) {
            if (resultCode == LoginActivity.RESULT_SUCCESS) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserController.closeDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //异步加载回调
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class UserLoaderCallbacks implements LoaderManager.LoaderCallbacks<User> {
        @Override
        public Loader<User> onCreateLoader(int id, Bundle args) {
            return new UserLoader(ProfileActivity.this, args.getString(ARG_USERNAME));
        }

        @Override
        public void onLoadFinished(Loader<User> loader, User user) {
            mUser = user;
            if (mUser != null) {
                updateUI();
            }
        }

        @Override
        public void onLoaderReset(Loader<User> loader) {
            //Do nothing
        }
    }

    private void updateUI() {

        //修改信息
        mIVAvatar = (ImageView) findViewById(R.id.avatar);
        mTVUsername = (TextView) findViewById(R.id.username);
        mTVSign = (TextView) findViewById(R.id.sign);
        mTVLevel = (TextView) findViewById(R.id.tv_level);
        mTVAge = (TextView) findViewById(R.id.tv_age);
        mTVSex = (TextView) findViewById(R.id.tv_sex);
        mTVWork = (TextView) findViewById(R.id.tv_work);
        mTVEdu = (TextView) findViewById(R.id.tv_edu);
        mTVHobby = (TextView) findViewById(R.id.tv_hobby);

        mVGStartChat = (ViewGroup) findViewById(R.id.rl_start_chat);

//        mIVAvatar.setOnClickListener(new AvatarOnClickListener(this, mUser.getUsername()));
        imageLoader.displayImage(mUser.getAvatarUrl(), mIVAvatar, options);

        mTVUsername.setText(mUser.getUsername());
        mTVSign.setText(mUser.getInfo());
        String level = null;
        if (mUser.getLevel() != -1) {
            level = String.valueOf(mUser.getLevel());
        }
        mTVLevel.setText(level);
        String age = null;
        if (mUser.getAge() != -1) {
            age = String.valueOf(mUser.getAge());
        }
        mTVAge.setText(age);
        String sex = null;
        if (mUser.getSex() != -1) {
            sex = String.valueOf(mUser.getSex() == 0 ? "男" : "女");
        }
        mTVSex.setText(sex);
        mTVWork.setText(mUser.getWork());
        mTVEdu.setText(mUser.getEduString());
        mTVHobby.setText(mUser.getHobby());

        if (mUser != null && mUserController.getSelfUser() != null && mUser.getUid() == mUserController.getSelfUser().getUid()) {
            mVGStartChat.setVisibility(View.GONE);
        } else {
            mVGStartChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                    intent.putExtra("isFromProfile", true);
                    intent.putExtra("username", mUser.getUsername());
                    ProfileActivity.this.startActivity(intent);
                    ProfileActivity.this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    finish();
                }
            });
        }
    }


}
