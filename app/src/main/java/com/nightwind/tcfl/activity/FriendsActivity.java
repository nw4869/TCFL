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

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.fragment.AddFriendFragment;
import com.nightwind.tcfl.fragment.ChatFragment;
import com.nightwind.tcfl.fragment.FriendsFragment;


public class FriendsActivity extends ActionBarActivity implements FriendsFragment.OnFragmentInteractionListener, AddFriendFragment.OnFragmentInteractionListener,
        View.OnTouchListener, GestureDetector.OnGestureListener{

    FriendsFragment mFriendsListFragment;
    ChatFragment mChatFragment;
    AddFriendFragment mAddFriendFragment;

    private Menu mMenu;
    private Toolbar mToolbar;
    private int currentFragmentStackTop = 0;
    private boolean mOnline = false;

    private boolean mIsFromProfile = false;

    //这三个用于右划关闭activity
    private GestureDetector mGestureDetector;
    private final int verticalMinDistance = 50;
    private final int minVelocity         = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        String profileUsername = null;
        if (getIntent() != null) {
            mOnline = getIntent().getBooleanExtra("online", false);
            mIsFromProfile = getIntent().getBooleanExtra("isFromProfile", false);
            if (mIsFromProfile) {
                profileUsername = getIntent().getStringExtra("username");
            }
        }

        if (savedInstanceState == null) {
            mFriendsListFragment = FriendsFragment.newInstance(mOnline);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, mFriendsListFragment)
                    .commit();

            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            // toolbar.setLogo(R.drawable.ic_launcher);
            mToolbar.setTitle("Friends");// 标题的文字需在setSupportActionBar之前，不然会无效
            // toolbar.setSubtitle("副标题");
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mGestureDetector = new GestureDetector(this, this);

        }

        if (mIsFromProfile) {
            onFragmentInteraction(profileUsername);
        }

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
        } else if (id == android.R.id.home) {
            hideSoftInput();
            if (currentFragmentStackTop == 0) {
                //结束该Activiy
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
//                mIsShowingList = !mIsShowingList;
            } else {
                //返回上一个fragment
                getSupportActionBar().setTitle("Friends");
                mMenu.getItem(0).setVisible(true);  //显示添加好友
                getSupportFragmentManager().popBackStack();
                currentFragmentStackTop--;
            }
        }  else if (id == R.id.action_add_friend) {
            //打开添加好友菜单
            mAddFriendFragment = AddFriendFragment.newInstance(UserController.getSelfUser().getUid());
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, mAddFriendFragment)
                    .addToBackStack("addFriend")
                    .commit();
            getSupportActionBar().setTitle("add Friend");
            currentFragmentStackTop++;
            mMenu.getItem(0).setVisible(false);  //隐藏添加好友
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
        User user = UserController.getUser(username);
        mChatFragment = ChatFragment.newInstance(UserController.getSelfUser().getUid(), user.getUid());
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

    /**
     * 返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hideSoftInput();
            getSupportActionBar().setTitle("Friends");
            currentFragmentStackTop--;
            if (currentFragmentStackTop == 0) {
                mMenu.getItem(0).setVisible(true);  //显示添加好友
            }
        }
        return super.onKeyDown(keyCode, event);
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




    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        mToolbar.setFocusableInTouchMode(true);
//        mToolbar.requestFocusFromTouch();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float absdx = Math.abs(e2.getX() - e1.getX());
        float absdy = Math.abs(e2.getY() - e1.getY());
        System.out.println("x` = " + (e1.getX() - e2.getX()) + " y` = " + Math.abs(e1.getY() - e2.getY()));

        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

        } else if (absdx > 1.5*absdy && e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

            hideSoftInput();
            if (currentFragmentStackTop == 0) { //在列表界面时右划关闭activity
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                return true;
            } else {
                //返回上一个fragment
                getSupportActionBar().setTitle("Friends");
                mMenu.getItem(0).setVisible(true);  //显示添加好友
                getSupportFragmentManager().popBackStack();
                currentFragmentStackTop--;
            }

        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        return false;
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void hideSoftInput() {
        if (mChatFragment != null) {
            mChatFragment.hideSoftInput();
        }
        if (mAddFriendFragment != null) {
            mAddFriendFragment.hideSoftInput();
        }
    }

    //    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
//            return rootView;
//        }
//    }
}
