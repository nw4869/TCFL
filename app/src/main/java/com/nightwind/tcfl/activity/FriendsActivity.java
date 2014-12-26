package com.nightwind.tcfl.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.fragment.AddFriendFragment;
import com.nightwind.tcfl.fragment.ChatFragment;
import com.nightwind.tcfl.fragment.FriendsFragment;


public class FriendsActivity extends BaseActivity implements FriendsFragment.OnFragmentInteractionListener, AddFriendFragment.OnFragmentInteractionListener{

    FriendsFragment mFriendsListFragment;
    ChatFragment mChatFragment;
    AddFriendFragment mAddFriendFragment;

    private Menu mMenu;
    private Toolbar mToolbar;
    private int currentFragmentStackTop = 0;
    private boolean mOnline = false;

    private boolean mIsFromProfile = false;

    private UserController mUserController;

    @Override
    int getLayoutResID() {
        return R.layout.activity_friends;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friends);

        String profileUsername = null;
        if (getIntent() != null) {
            mOnline = getIntent().getBooleanExtra("online", false);
            mIsFromProfile = getIntent().getBooleanExtra("isFromProfile", false);
            if (mIsFromProfile) {
                profileUsername = getIntent().getStringExtra("username");
            }
        }
        mUserController = new UserController(this);

        if (savedInstanceState == null) {
            mFriendsListFragment = FriendsFragment.newInstance(mOnline);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, mFriendsListFragment)
                    .commit();
        }


        if (mIsFromProfile) {
            onFragmentInteraction(profileUsername);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserController.closeDB();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMenu != null) {
            mMenu.getItem(0).setVisible(currentFragmentStackTop == 0);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mMenu != null) {
            mMenu.getItem(0).setVisible(currentFragmentStackTop == 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        mMenu = menu;
        mMenu.getItem(0).setVisible(currentFragmentStackTop == 0);
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
//        else if (id == android.R.id.home) {
//            hideSoftInput();
//            if (currentFragmentStackTop == 0) {
//                //结束该Activiy
//                finish();
//                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
////                mIsShowingList = !mIsShowingList;
//            } else {
//                //返回上一个fragment
//                getSupportActionBar().setTitle("Friends");
//                mMenu.getItem(0).setVisible(true);  //显示添加好友
//                getSupportFragmentManager().popBackStack();
//                currentFragmentStackTop--;
//            }
//        }
        else if (id == R.id.action_add_friend) {
            //打开添加好友菜单
            onFragmentInteractionAddFriend();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击好友列表调用此函数，然后打开聊天窗口ChatFragment，隐藏添加好友菜单
     * @param username
     */
    @Override
    public void onFragmentInteraction(String username) {
//        Toast.makeText(this, "selected" + username.toString(), Toast.LENGTH_SHORT).show();
//        User user = mUserController.getUser(username);
//        mChatFragment = ChatFragment.newInstance(mUserController.getSelfUser().getUid(), user.getUid());
        String selfUsername = new Auth(this).getUsername();
        mChatFragment = ChatFragment.newInstance(selfUsername, username);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .add(R.id.container, mChatFragment)
                .addToBackStack("friendsList")
                .commit();
        currentFragmentStackTop++;
        getSupportActionBar().setTitle(username);
        if (mMenu != null) {
            mMenu.getItem(0).setVisible(false);
        }
    }

    @Override
    public void onFragmentInteractionAddFriend() {

        mAddFriendFragment = AddFriendFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .add(R.id.container, mAddFriendFragment)
                .addToBackStack("addFriend")
                .commit();
        getSupportActionBar().setTitle("add Friend");
        currentFragmentStackTop++;
        mMenu.getItem(0).setVisible(false);  //隐藏添加好友
    }

    @Override
    protected void ToolBarClose() {
        slideClose();
    }

    @Override
    protected void slideClose() {
        hideSoftInput();
        if (currentFragmentStackTop == 0) { //在列表界面时右划关闭activity
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        } else {
            //返回上一个fragment
            getSupportActionBar().setTitle("Friends");
            mMenu.getItem(0).setVisible(true);  //显示添加好友
            getSupportFragmentManager().popBackStack();
            currentFragmentStackTop--;
        }
    }

    @Override
    protected void backKeyClose() {
//        super.backKeyClose();
        hideSoftInput();
        getSupportActionBar().setTitle("Friends");
        currentFragmentStackTop--;
        if (currentFragmentStackTop == 0) {
            mMenu.getItem(0).setVisible(true);  //显示添加好友
        }
    }

    /**
     * 添加好友时返回，TRUE表示添加成功
     * @param addedFriend
     */
    @Override
    public void onFragmentInteraction(boolean addedFriend) {
        if (addedFriend) {
            if (mFriendsListFragment != null ) {
                mFriendsListFragment.refreshList();
            }
        }
    }


    public void hideSoftInput() {
        if (mChatFragment != null) {
            mChatFragment.hideSoftInput();
        }
        if (mAddFriendFragment != null) {
            mAddFriendFragment.hideSoftInput();
        }
    }

}
