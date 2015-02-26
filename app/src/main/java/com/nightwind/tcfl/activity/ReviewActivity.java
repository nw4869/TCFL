package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.tool.BaseTools;

public class ReviewActivity extends BaseActivity {

    private TextView mETTitle;
    private TextView mETContent;

    @Override
    int getLayoutResID() {
        return R.layout.activity_review;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mETTitle = (TextView) findViewById(R.id.et_title);
        mETContent = (TextView) findViewById(R.id.et_content);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_review, menu);
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

    public void onSave(View v) {
        final Article article = new Article();

        String title = String.valueOf(mETTitle.getText());
        String content = String.valueOf(mETContent.getText());

        if (content.trim().length() == 0 || title.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.input_can_not_none, Toast.LENGTH_SHORT).show();
            return ;
        }

        String username = new Auth(this).getUsername();
        if (username == null) {
            Toast.makeText(this, R.string.login_request, Toast.LENGTH_SHORT).show();
            return ;
        }

        article.setTitle(title);
        article.setClassify(-1);
        article.setContent(content);

        new AddArticleTask().execute(article);
    }

    public void onShow(View v) {
        Intent intent = new Intent(this, ReviewListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }


    public class AddArticleTask extends AsyncTask<Article, Void, Integer> {

        private ProgressDialog mDialog;

        @Override
        protected Integer doInBackground(Article... params) {
            ArticleController ac = new ArticleController(ReviewActivity.this);
            Article article = params[0];
            int id = -1;
            article = ac.addArticleToServer( article.getClassify(), article.getTitle(), article.getContent());
            if (article != null) {
                id = article.getId();
            }
            return id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(ReviewActivity.this);
            mDialog.setMessage("正在保存，请稍后...");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Integer id) {
            super.onPostExecute(id);
            mDialog.cancel();
            if (id != -1) {
                Toast.makeText(ReviewActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ReviewActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
