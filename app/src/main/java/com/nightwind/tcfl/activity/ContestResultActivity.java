package com.nightwind.tcfl.activity;

import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndetermininate;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.adapter.NeighborAdapter;
import com.nightwind.tcfl.adapter.RankUserAdapter;
import com.nightwind.tcfl.bean.RankUser;
import com.nightwind.tcfl.server.RankUserLoader;

import java.util.ArrayList;
import java.util.List;

public class ContestResultActivity extends BaseActivity {

    private static final int LOAD_RANK_USER = 0;
    private List<RankUser> mUsers = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RankUserAdapter mAdapter;
    private ProgressBarCircularIndetermininate mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    int getLayoutResID() {
        return R.layout.activity_contest_result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RankUserAdapter(this, mUsers);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBarCircularIndetermininate) findViewById(R.id.progressBar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        Bundle data = new Bundle();
        data.putInt(RankUserLoader.ARG_BEGIN, 0);
        data.putInt(RankUserLoader.ARG_END, 10);
        getSupportLoaderManager().restartLoader(LOAD_RANK_USER, data, new LoadRankUserCallbacks());
    }

    private class LoadRankUserCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<RankUser>> {

        @Override
        public Loader<List<RankUser>> onCreateLoader(int id, Bundle args) {
            return new RankUserLoader(getApplication(), args.getInt(RankUserLoader.ARG_BEGIN), args.getInt(RankUserLoader.ARG_END));
        }

        @Override
        public void onLoadFinished(Loader<List<RankUser>> loader, List<RankUser> data) {
            if (data != null) {
                mUsers.clear();
                mUsers.addAll(data);
                if (data.size() == 0) {
                    Toast.makeText(getApplicationContext(), "暂无排名", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.load_failed, Toast.LENGTH_SHORT).show();
            }

            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);

        }

        @Override
        public void onLoaderReset(Loader<List<RankUser>> loader) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_contest_result, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
