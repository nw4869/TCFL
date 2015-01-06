package com.nightwind.tcfl.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.fragment.AddFriendFragment;
import com.nightwind.tcfl.fragment.ChatFragment;
import com.nightwind.tcfl.fragment.FriendsFragment;
import com.nightwind.tcfl.tool.ExampleUtil;

import cn.jpush.android.api.JPushInterface;


public class FriendsActivity extends BaseActivity implements FriendsFragment.OnFragmentInteractionListener, AddFriendFragment.OnFragmentInteractionListener{

    private static final String ARG_IS_FROM_PROFILE = "isFromProfile";
    private static final String ARG_IS_FROM_NOTIFICATION = "isFromNotification";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_ONLINE = "online";
    FriendsFragment mFriendsListFragment;
    ChatFragment mChatFragment;
    AddFriendFragment mAddFriendFragment;

    private Menu mMenu;
    private Toolbar mToolbar;
    private int currentFragmentStackTop = 0;
    private boolean mOnline = false;

    private String requestUsername;

    @Override
    int getLayoutResID() {
        return R.layout.activity_friends;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friends);

        if (savedInstanceState == null) {
            mFriendsListFragment = FriendsFragment.newInstance(mOnline);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, mFriendsListFragment)
                    .commit();
        }
        registerMessageReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        if (mMenu != null) {
            mMenu.getItem(0).setVisible(currentFragmentStackTop == 0);
        }

        requestUsername = null;
        final Intent intent = getIntent();
        if (intent != null) {
            mOnline = intent.getBooleanExtra(ARG_ONLINE, false);
            boolean isFromProfile = intent.getBooleanExtra(ARG_IS_FROM_PROFILE, false);
            boolean isFromNotification = intent.getBooleanExtra(ARG_IS_FROM_NOTIFICATION, false);
            intent.removeExtra(ARG_IS_FROM_PROFILE);
            intent.removeExtra(ARG_IS_FROM_NOTIFICATION);
            requestUsername = intent.getStringExtra(ARG_USERNAME);
            if (isFromProfile ) {
                onFragmentInteraction(requestUsername);
            } else if ( isFromNotification) {
                onFragmentInteraction(requestUsername);
            }
        }
    }

//    private void updateChatFragment() {
//        if (currentFragmentStackTop > 0) {
//            currentFragmentStackTop--;
//            getSupportFragmentManager().popBackStack();
//        }
//        onFragmentInteraction(requestUsername);
//    }


    //该Activity为singleTask模式，将新的intent覆盖旧的，以便获取正确的ProfileUsername
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mMenu != null) {
            mMenu.getItem(0).setVisible(currentFragmentStackTop == 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
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
        if (currentFragmentStackTop > 0 && mChatFragment != null && mChatFragment.mUsername2 != null && mChatFragment.mUsername2.equals(username)) {
            mChatFragment.refreshList();
        } else {
            mChatFragment = ChatFragment.newInstance(selfUsername, username);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (currentFragmentStackTop > 0) {
                //改变用户名，刷新界面
                transaction.replace(R.id.container, mChatFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .add(R.id.container, mChatFragment)
                        .addToBackStack("friendsList")
                        .commit();
                currentFragmentStackTop++;
            }
        }
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
//            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

    @Override
    protected void lightPushDown() {
        hideSoftInput();
        super.lightPushDown();
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


    //    for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.nightwind.tcfl.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String title = intent.getStringExtra(KEY_TITLE);
                String message = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + message + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
//                setCostomMsg(showMsg.toString());
                if (ChatFragment.getChattingUsername().equals(title)) {
                    mChatFragment.refreshList();
                }
            }
        }
    }

}
