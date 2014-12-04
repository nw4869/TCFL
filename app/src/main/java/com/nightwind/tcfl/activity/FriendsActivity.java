package com.nightwind.tcfl.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.fragment.ChatFragment;
import com.nightwind.tcfl.fragment.FriendsFragment;
import com.nightwind.tcfl.tool.Dummy;


public class FriendsActivity extends ActionBarActivity implements FriendsFragment.OnFragmentInteractionListener {


    private Toolbar mToolbar;
    private boolean mIsChatting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out)
                    .add(R.id.container, new FriendsFragment())
                    .commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("Friends");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
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
            if (!mIsChatting) {
//            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                finish();
                mIsChatting = !mIsChatting;
            } else {
                getSupportActionBar().setTitle("Friends");
                getSupportFragmentManager().popBackStack();
                mIsChatting = !mIsChatting;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String username) {
//        Toast.makeText(this, "selected" + username.toString(), Toast.LENGTH_SHORT).show();
        User user = Dummy.getUser(username);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out)
                .add(R.id.container, ChatFragment.newInstance(Dummy.getSelfUser().getUid(), user.getUid()))
                .addToBackStack("friendsList")
                .commit();
        mIsChatting = true;
        getSupportActionBar().setTitle(username);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getSupportActionBar().setTitle("Friends");
            mIsChatting = !mIsChatting;
        }
        return super.onKeyDown(keyCode, event);
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
