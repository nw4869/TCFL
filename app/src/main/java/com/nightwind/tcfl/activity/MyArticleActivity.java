package com.nightwind.tcfl.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.fragment.ArticleRecyclerFragment;

public class MyArticleActivity extends BaseActivity{

    @Override
    int getLayoutResID() {
        return R.layout.activity_my_acticle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, ArticleRecyclerFragment.newInstance(ArticleRecyclerFragment.TYPE_MY_ARTICLE))
                    .commit();
        }

        getSupportActionBar().setTitle("我的帖子");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_acticle, menu);
        return true;
    }


}
