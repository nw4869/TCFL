package com.nightwind.tcfl.activity;

import android.net.Uri;
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
import com.nightwind.tcfl.fragment.AddFriendFragment;
import com.nightwind.tcfl.fragment.ChatFragment;
import com.nightwind.tcfl.fragment.FriendsFragment;
import com.nightwind.tcfl.tool.Dummy;


public class FriendsActivity extends ActionBarActivity implements FriendsFragment.OnFragmentInteractionListener, AddFriendFragment.OnFragmentInteractionListener,
        View.OnTouchListener, GestureDetector.OnGestureListener{

    FriendsFragment mFriendsListFragment;

    private Menu mMenu;
    private Toolbar mToolbar;
    private int current = 0;

    //这三个用于右划关闭activity
    private GestureDetector mGestureDetector;
    private final int verticalMinDistance = 50;
    private final int minVelocity         = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        if (savedInstanceState == null) {
            mFriendsListFragment = new FriendsFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, mFriendsListFragment)
                    .commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("Friends");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGestureDetector = new GestureDetector(this, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        mMenu = menu;
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
            if (current == 0) {
                //结束该Activiy
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
//                mIsShowingList = !mIsShowingList;
            } else {
                //返回上一个fragment
                getSupportActionBar().setTitle("Friends");
                mMenu.getItem(0).setVisible(true);  //显示添加好友
                getSupportFragmentManager().popBackStack();
                current--;
            }
        }  else if (id == R.id.action_add_friend) {
            //打开添加好友菜单
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, AddFriendFragment.newInstance(Dummy.getSelfUser().getUid()))
                    .addToBackStack("addFriend")
                    .commit();
            getSupportActionBar().setTitle("add Friend");
            current++;
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
        User user = Dummy.getUser(username);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                .add(R.id.container, ChatFragment.newInstance(Dummy.getSelfUser().getUid(), user.getUid()))
                .addToBackStack("friendsList")
                .commit();
        current++;
        getSupportActionBar().setTitle(username);
        mMenu.getItem(0).setVisible(false);
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
            getSupportActionBar().setTitle("Friends");
            current--;
            if (current == 0) {
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

            if (current == 0) { //在列表界面时右划关闭activity
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                return true;
            } else {
                //返回上一个fragment
                getSupportActionBar().setTitle("Friends");
                mMenu.getItem(0).setVisible(true);  //显示添加好友
                getSupportFragmentManager().popBackStack();
                current--;
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
