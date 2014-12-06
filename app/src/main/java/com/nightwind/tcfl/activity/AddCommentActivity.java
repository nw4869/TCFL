package com.nightwind.tcfl.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.Comment;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.tool.BaseTools;
import com.nightwind.tcfl.tool.Dummy;

public class AddCommentActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private EditText mETContent;

    private User mSelfUser;
    private int mClassify = 0;
    private int mRowId = 1;
    private int mArticleId = 0;
    private int mParentComment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        if (getIntent() != null) {
            mClassify = getIntent().getIntExtra("classify", 0);
            mRowId = getIntent().getIntExtra("rowId", 1);
            mParentComment = getIntent().getIntExtra("parentComment", 0);
            mArticleId = getIntent().getIntExtra("articleId", 0);
        } else {
            Log.e("AddCommentActivity", "getIntent Error!");
        }

        mSelfUser = Dummy.getSelfUser();
        mETContent = (EditText) findViewById(R.id.et_content);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("回复帖子");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_comment, menu);
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
        } else if(id == R.id.action_push_comment) {
            if (pushComment()) {
                Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();
                setResult(0);
                finish();
            } else {
                Toast.makeText(this, "回复失败", Toast.LENGTH_SHORT).show();
            }
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean pushComment() {
        Article article = Dummy.getMyListItem(mClassify).get(mRowId);

        Comment comment = new Comment();

        comment.setParent(mParentComment);
        String content = String.valueOf(mETContent.getText());
        comment.setContent(content);
        comment.setUsername(mSelfUser.getUsername());
        comment.setDateTime(BaseTools.getCurrentDateTime());

        return article.addComment(comment);
    }
}