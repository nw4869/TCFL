package com.nightwind.tcfl.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.fragment.ArticleRecyclerFragment;
import com.nightwind.tcfl.tool.Dummy;

public class MyCollectionActivity extends ActionBarActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private Toolbar mToolbar;

//    private User mSelfUser;

    private GestureDetector mGestureDetector;
    private final int verticalMinDistance = 50;
    private final int minVelocity         = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

//        mSelfUser = Dummy.getSelfUser();

        //fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, ArticleRecyclerFragment.newInstance(ArticleRecyclerFragment.TYPE_COLLECTION))
                    .commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("我的收藏");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //滑动关闭监听器
        mGestureDetector = new GestureDetector(this, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_collection, menu);
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
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        }

        return super.onOptionsItemSelected(item);
    }



    //右划关闭

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

    //向右滑动关闭activity
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float absdx = Math.abs(e2.getX() - e1.getX());
        float absdy = Math.abs(e2.getY() - e1.getY());
//        System.out.println("x` = " + (e1.getX() - e2.getX()) + " y` = " + Math.abs(e1.getY() - e2.getY()));

        if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

            // 切换Activity
            // Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
            // startActivity(intent);
//            Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
        } else if (absdx > 1.5*absdy && e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

            // 切换Activity
            // Intent intent = new Intent(ViewSnsActivity.this, UpdateStatusActivity.class);
            // startActivity(intent);
//            Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            return true;
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
}
