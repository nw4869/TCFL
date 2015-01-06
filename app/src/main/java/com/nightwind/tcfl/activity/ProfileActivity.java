package com.nightwind.tcfl.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.UserLoader;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;


public class ProfileActivity extends BaseActivity {
    public static final String ARG_UID = "uid";
    public static final String ARG_USERNAME = "username";
    private static final int LOAD_USER = 0;
    private static final int REQUEST_ASK_GALLERY = 10;
    private static final int REQUEST_CROP = 11;
    public static final String EXTRA_IMAGE = "extra_image";

//    private Toolbar mToolbar;
//    private GestureDetector mGestureDetector;
//
//    private final int verticalMinDistance = 50;
//    private final int minVelocity = 0;

    private User mUser;

    private ImageView mIVAvatar;
    private TextView mTVUsername;
    private TextView mETSign;
    private TextView mTVLevel;
    private TextView mETAge;
//    private TextView mTVSex;
    private TextView mETWork;
//    private TextView mTVEdu;
    private TextView mETHobby;

    private ViewGroup mVGStartChat;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private View mProcessBar;
    private HashMap<String, Integer> mEduStrMap = new HashMap<>();
    private Menu mMenu;
    private String mUsername;
    private Spinner mSexSpinner;
    private Spinner mEduSpinner;

    private static final String IMAGE_FILE_LOCATION = "file://" + Environment.getExternalStorageDirectory() + "/tcfl/temp.png";//temp file
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap

    @Override
    int getLayoutResID() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsername = new Auth(this).getUsername();
        if (getIntent() != null) {
            String tmpUsername = getIntent().getStringExtra("username");
            if (tmpUsername != null) {
                mUsername = tmpUsername;
                Log.d("ProfileActivity getIntent", mUsername);
            }
        }

        mProcessBar = findViewById(R.id.progressBarCircularIndetermininate);

        mIVAvatar = (ImageView) findViewById(R.id.avatar);
//        ViewCompat.setTransitionName(mIVAvatar, EXTRA_IMAGE);

        mTVUsername = (TextView) findViewById(R.id.username);
        mETSign = (EditText) findViewById(R.id.sign);
        mTVLevel = (TextView) findViewById(R.id.tv_level);
        mETAge = (EditText) findViewById(R.id.et_age);
//        mTVSex = (TextView) findViewById(R.id.tv_sex);
        mETWork = (EditText) findViewById(R.id.et_work);
//        mTVEdu = (TextView) findViewById(R.id.tv_edu);
        mETHobby = (EditText) findViewById(R.id.et_hobby);


        //性别
        mSexSpinner = (Spinner) findViewById(R.id.sex_spinner);
        String[] mItems = new String[] {"", "男", "女"};
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        mSexSpinner.setAdapter(_Adapter);

        //学历
        mEduSpinner = (Spinner) findViewById(R.id.edu_spinner);
        String[] mItems1 = new String[] {"", "幼儿园小朋友", "小学生"};
        ArrayAdapter<String> _Adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems1);
        mEduSpinner.setAdapter(_Adapter1);

        mVGStartChat = (ViewGroup) findViewById(R.id.rl_start_chat);

        final String selfUsername = new Auth(this).getUsername();
        if (selfUsername == null || !selfUsername.equals(mUsername)) {
            mETSign.setHint("sign");
            mTVLevel.setHint("");
            mETAge.setHint("");
//            mTVSex.setHint("");
            mETWork.setHint("");
            mETHobby.setHint("");
//            mTVEdu.setHint("");
        }

        init();

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void init() {
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, mUsername);
//        LoaderManager lm = getLoaderManager();
        LoaderManager lm = getSupportLoaderManager();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        mMenu = menu;
        boolean visible = false;
        if (mUsername.equals(new Auth(this).getUsername())) {
            visible = true;
        }
        menu.getItem(0).setVisible(visible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_profile) {
            new EditProfileTask().execute();
            mProcessBar.setVisibility(View.VISIBLE);
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
            } else {
                Toast.makeText(getApplicationContext(), R.string.load_failed, Toast.LENGTH_SHORT).show();
            }

            mProcessBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader<User> loader) {
            //Do nothing
        }
    }

    private void updateUI() {


        //修改信息

        mIVAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                changeAvatar();
                Intent intent = new Intent(ProfileActivity.this, UploadImageActivity.class);
                intent.putExtra(UploadImageActivity.ARG_IMG_URL, mUser.getAvatarUrl());
                intent.putExtra(UploadImageActivity.ARG_USERNAME, mUser.getUsername());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
//                (ProfileActivity.this).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, v, UploadImageActivity.EXTRA_IMAGE);
//                ActivityCompat.startActivity( ProfileActivity.this, intent,
//                        options.toBundle());
            }
        });


//        mIVAvatar.setOnClickListener(new AvatarOnClickListener(this, mUser.getUsername()));
        imageLoader.displayImage(mUser.getAvatarUrl(), mIVAvatar, options);

        mTVUsername.setText(mUser.getUsername());
        mETSign.setText(mUser.getInfo());
        String level = null;
        if (mUser.getLevel() != -1) {
            level = String.valueOf(mUser.getLevel());
        }
        mTVLevel.setText(level);
        String age = null;
        if (mUser.getAge() != -1) {
            age = String.valueOf(mUser.getAge());
        }
        mETAge.setText(age);

        int sexSel = 0;
        if (mUser.getSex() != -1) {
            sexSel = mUser.getSex();
        }
        mSexSpinner.setSelection(sexSel + 1, true);

//        mTVSex.setText(sex);
        mETWork.setText(mUser.getWork());

        int eduSel = 0;
        if (mUser.getEdu() != -1) {
            eduSel = mUser.getEdu();
        }
        mEduSpinner.setSelection(eduSel + 1, true);


        mETHobby.setText(mUser.getHobby());

        Auth auth = new Auth(this);
        String selfUsername = auth.getUsername();

        if (selfUsername.equals(mUser.getUsername())) {
            mVGStartChat.setVisibility(View.GONE);
        } else {
            mVGStartChat.setVisibility(View.VISIBLE);
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
            if (mMenu != null) {
                mMenu.getItem(0).setVisible(mUsername.equals(selfUsername));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mMenu.getItem(0).setVisible(mUser.getUsername().equals(new Auth(this).getUsername()));
    }



    private class EditProfileTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            UserController uc = new UserController(getApplicationContext());
            User user = uc.getSelfUser();

            String sign = String.valueOf(mETSign.getText());

            int age = -1;
            try {
                age = Integer.valueOf(mETAge.getText().toString());
            } catch (Exception ignored) {
            }

            int edu = (int) mEduSpinner.getSelectedItemId();
            if (edu -1 >= 0) {
                user.setEdu(edu-1 );
            }

            int sex = (int) mSexSpinner.getSelectedItemId();
            if (sex-1 >= 0) {
                user.setSex(sex-1 );
            }

            String work = String.valueOf(mETWork.getText());
            String hobby = String.valueOf(mETHobby.getText());
            if (age > 0) {
                user.setAge(age);
            }
            user.setInfo(sign);
            user.setWork(work);
            user.setHobby(hobby);

            return uc.updateUser(user);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "修改失败，请重试", Toast.LENGTH_SHORT).show();
            }
            mProcessBar.setVisibility(View.GONE);
            super.onPostExecute(aBoolean);
        }
    }
}
