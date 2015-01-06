package com.nightwind.tcfl.activity;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.nightwind.tcfl.R;

public abstract class BaseActivity extends ActionBarActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    protected Toolbar mToolbar;
    protected GestureDetector mGestureDetector;
    protected final int verticalMinDistance = 50;
    protected final int minVelocity = 0;
    private float horizonMinDistance = 10;

    /**
     * 必须包含一个toolbar
     * @return
     */
    abstract int getLayoutResID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base);
        setContentView(getLayoutResID());

        //初始化工具栏
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setTitleTextColor(Color.WHITE);
        // toolbar.setLogo(R.drawable.ic_launcher);
//        mToolbar.setTitle("Profile");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //滑动关闭监听器
        mGestureDetector = new GestureDetector(this, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if (id == android.R.id.home) {
            ToolBarClose();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        backKeyClose();
        super.onBackPressed();
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

        } else if (absdx > 1.5*absdy && e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
            slideClose();
            return true;
        } else if (absdy > 1.5*absdx && e2.getY() - e1.getY() > horizonMinDistance && Math.abs(velocityY) > minVelocity) {
            //下拉
            lightPushDown();
        }

        return false;
    }

    protected void lightPushDown() {

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
//        return !mGestureDetector.onTouchEvent(ev);
    }
    protected void ToolBarClose() {
        finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    protected void backKeyClose() {
        finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    protected void slideClose() {
        finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);

    }
}
