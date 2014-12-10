package com.nightwind.tcfl.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.tool.BaseTools;

public class AddArticleActivity extends ActionBarActivity {

    private Menu mMenu;
    private Toolbar mToolbar;

    private EditText mETTitle;
    private EditText mETContent;

    private User mSelfUser;
    private int mClassify = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        if (getIntent() != null) {
            mClassify = getIntent().getIntExtra("classify", 0);
        }
        mSelfUser = UserController.getSelfUser();

        mETTitle = (EditText) findViewById(R.id.et_title);
        mETContent = (EditText) findViewById(R.id.et_content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("发布帖子");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_article, menu);
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
        } else if (id == R.id.action_push_article) {
            Article article = new Article();

            String title = String.valueOf(mETTitle.getText());
            String content = String.valueOf(mETContent.getText());

            article.setTitle(title);
            article.setContent(content);
            article.setUsername(mSelfUser.getUsername());

            String date = BaseTools.getCurrentDateTime();
            article.setDateTime(date);


            if (ArticleController.addArticle(mClassify, article)) {
                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
                setResult(0);
                finish();
            } else {
                Toast.makeText(this, "发布失败", Toast.LENGTH_SHORT).show();
            }

        } else if (id == android.R.id.home) {
            setResult(1);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
