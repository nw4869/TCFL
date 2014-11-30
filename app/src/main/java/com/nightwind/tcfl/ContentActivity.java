package com.nightwind.tcfl;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nightwind.tcfl.bean.MyListItem;
import com.nightwind.tcfl.tool.Constants;

import java.util.Random;


public class ContentActivity extends ActionBarActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private MyListItem mMylistItem;

    //Comment Items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Toolbar mToolbar;

    private GestureDetector mGestureDetector;

    private final int verticalMinDistance = 50;
    private final int minVelocity         = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("TCFL");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int id = getIntent().getIntExtra("id", 0);
        mMylistItem = Constants.getMyListItem().get(id);

        Random random = new Random();
        for (int i = 0; i < mMylistItem.getCommentNum(); i++) {
            mMylistItem.getCommentItems()[i].setImg(Constants.getMyListItem().get(random.nextInt(mMylistItem.getCommentNum())).getImg());
        }

        TextView tvUsername = (TextView) findViewById(R.id.username);
        TextView tvDateTime = (TextView) findViewById(R.id.datetime);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
        TextView tvContent = new TextView(this);


        tvUsername.setText(mMylistItem.getUsername());
        tvTitle.setText(mMylistItem.getTitle());
        tvDateTime.setText(mMylistItem.getDateTime());

        tvContent.setText(mMylistItem.getContent());
        contentLayout.addView(tvContent);


        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentAdapter(this, mMylistItem.getCommentItems());
        mRecyclerView.setAdapter(mAdapter);

        mGestureDetector = new GestureDetector(this, (GestureDetector.OnGestureListener) this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
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
        System.out.println("x` = " + (e1.getX() - e2.getX()) + " y` = " + Math.abs(e1.getY() - e2.getY()));

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
