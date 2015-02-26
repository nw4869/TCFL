package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.exception.AuthenticationException;

import java.io.IOException;

public class ContestActivity extends BaseActivity {

    @Override
    int getLayoutResID() {
        return R.layout.activity_contest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_contest, menu);
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

    public void onUpload(View v) {
        new UploadReviewTask().execute();
    }

    public void onShow(View v) {
        Intent intent = new Intent(this, ContestResultActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }


    public class UploadReviewTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mDialog;

        @Override
        protected Boolean doInBackground(Void... params) {
            ArticleController ac = new ArticleController(ContestActivity.this);
            boolean ok = false;
            try {
                ok =  ac.uploadReview();
            } catch (AuthenticationException | IOException e) {
                e.printStackTrace();
            }
            return ok;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(ContestActivity.this);
            mDialog.setMessage("正在上传，请稍后...");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean ok) {
            super.onPostExecute(ok);
            mDialog.cancel();
            if (ok) {
                Toast.makeText(ContestActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ContestActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
