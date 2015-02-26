package com.nightwind.tcfl.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.fragment.ArticleRecyclerFragment;

public class ReviewListActivity extends BaseActivity {

    @Override
    int getLayoutResID() {
        return R.layout.activity_review_list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .add(R.id.container, ArticleRecyclerFragment.newInstance(ArticleRecyclerFragment.TYPE_REVIEW))
                    .commit();
        }

        getSupportActionBar().setTitle("复习");
    }

}
