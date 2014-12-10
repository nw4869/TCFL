package com.nightwind.tcfl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.nightwind.tcfl.adapter.CommentAdapter;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.controller.ArticleController;


public class ContentActivity extends ActionBarActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private Article mArticle;

    //Comment Items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Toolbar mToolbar;
    private ShareActionProvider mShareActionProvider;

    private GestureDetector mGestureDetector;
    private final int verticalMinDistance = 50;
    private final int minVelocity         = 0;

//    private int mClassify;
//    private int mRowId;
    private int mArticleId;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
//        mToolbar.setTitle("Content");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mClassify = getIntent().getIntExtra("classify", 0);
//        mRowId = getIntent().getIntExtra("rowId", 0);
        mArticleId = getIntent().getIntExtra("articleId", 0);

//        mArticle = Dummy.getMyListItem(mClassify).get(mRowId);
        mArticle = ArticleController.getArticle(mArticleId);
//        mArticleId = mArticle.getId();

        //设置标题
        getSupportActionBar().setTitle(mArticle.getTitle());

//        //随机指定bitmap
//        Random random = new Random();
//        for (int i = 0; i < mArticle.getCommentNum(); i++) {
//            mArticle.getCommentEntities()[i].setImg(Constants.getMyListItem().get(random.nextInt(mArticle.getCommentNum())).getImg());
//        }
//
//        TextView tvUsername = (TextView) findViewById(R.id.username);
//        TextView tvDateTime = (TextView) findViewById(R.id.datetime);
//        TextView tvTitle = (TextView) findViewById(R.id.title);
//        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.contentLayout);
//        TextView tvContent = new TextView(this);
//
//
//        tvUsername.setText(mArticle.getUsername());
//        tvTitle.setText(mArticle.getTitle());
//        tvDateTime.setText(mArticle.getDateTime());
//
//        tvContent.setText(mArticle.getContent());
//        contentLayout.addView(tvContent);


        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        mAdapter = new CommentAdapter(this, mArticle.getCommentEntities());
//        mAdapter = new CommentAdapter(this, mArticle, mClassify, mRowId, mArticleId);
        mAdapter = new CommentAdapter(this, mArticle);
        mRecyclerView.setAdapter(mAdapter);

        mGestureDetector = new GestureDetector(this,  this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);

        mMenu = menu;

        // ShareActionProvider配置
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu
                .findItem(R.id.action_share));
        String content = mArticle.getContent();
        Intent intent = new Intent(Intent.ACTION_SEND);
        /*设置内容*/
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("text/*");
        mShareActionProvider.setShareIntent(intent);

        //收藏按钮
        if (mArticle.isCollected()) {
            mMenu.getItem(1).setVisible(false);
            mMenu.getItem(2).setVisible(true);
        } else {
            mMenu.getItem(1).setVisible(true);
            mMenu.getItem(2).setVisible(false);
        }
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
//            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        } else if (id == R.id.action_add_comment) {
            //回帖
            Intent intent = new Intent(ContentActivity.this, AddCommentActivity.class);
//            intent.putExtra("classify", mClassify);
//            intent.putExtra("rowId", mRowId);
            intent.putExtra("articleId", mArticleId);
            intent.putExtra("parentComment", 0);
//                    MainActivity.this.startActivity(intent);
            ContentActivity.this.startActivityForResult(intent, 0);
        } else if (id == R.id.action_to_collect) {
//            mArticle.setCollected(true);
            ArticleController.addCollection(mArticle);
            mMenu.getItem(1).setVisible(false);
            mMenu.getItem(2).setVisible(true);
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_to_not_collect) {
//            mArticle.setCollected(false);
            ArticleController.removeCollection(mArticle);
            mMenu.getItem(1).setVisible(true);
            mMenu.getItem(2).setVisible(false);
            Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }

    ///////////////////////////////////////////////////////
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
