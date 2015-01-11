package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.adapter.AddImageAdapter;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.UploadImageTask;
import com.nightwind.tcfl.tool.BaseTools;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.gmariotti.recyclerview.itemanimator.SlideScaleInOutRightItemAnimator;

public class AddArticleActivity extends ActionBarActivity {

    private static final int REQUEST_ASK_GALLERY = 100;
    private static final String TAG = "AddArticleActivity";
    private Menu mMenu;
    private Toolbar mToolbar;

    private EditText mETTitle;
    private EditText mETContent;

//    private User mSelfUser;
    private int mClassify = 0;
    private ProgressDialog mDialog;
    private String mUsername;
    private List<String> mImageUriList = new ArrayList<>();
    private Set<Boolean> mUploaded = new HashSet<>();
    private List<String> mRemoteUrl = new ArrayList<>();
    private RecyclerView mImageRecyclerView;
    private AddImageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        //先判断是否登录
        Auth auth = new Auth(this);
        if (!auth.isLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            Toast.makeText(getApplicationContext(), R.string.login_request, Toast.LENGTH_SHORT).show();
            return ;
        }

        if (getIntent() != null) {
            mClassify = getIntent().getIntExtra("classify", 0);
        }

        mImageRecyclerView = (RecyclerView) findViewById(R.id.imageList);
        mImageRecyclerView.setItemAnimator(new SlideScaleInOutRightItemAnimator(mImageRecyclerView));
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mImageRecyclerView.setLayoutManager(mLayoutManager);
        //设置适配器
        mAdapter = new AddImageAdapter(this, mImageUriList);
        mAdapter.setOnItemClickListener(new AddImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
                intent.putExtra(ShowImageActivity.ARG_IMAGE_URI, mImageUriList.get(position));
                startActivity(intent);
            }
        });
        mImageRecyclerView.setAdapter(mAdapter);

        UserController userController = new UserController(this);
//        mSelfUser = userController.getSelfUser();
        mUsername = new Auth(this).getUsername();
        userController.closeDB();

        mETTitle = (EditText) findViewById(R.id.et_title);
        mETContent = (EditText) findViewById(R.id.et_content);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("发布帖子");// 标题的文字需在setSupportActionBar之前，不然会无效
        // toolbar.setSubtitle("副标题");
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_LOGIN) {
            if (resultCode == LoginActivity.RESULT_SUCCESS) {
                //重启
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                finish();
            }
        } else if (requestCode == REQUEST_ASK_GALLERY ) {
            if (data != null) {
                Uri uri = data.getData();
//                String filePath = getRealPathFromURI(uri);
//                Log.d(TAG, "uri = " + uri.toString() + " filePath = " + filePath);
                addImage(uri.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addImage(String ImageUri) {
        mAdapter.add(ImageUri);
        Editable content = mETContent.getText();
        String append = "<图片" + mAdapter.getItemCount() + ">";
        content.append(append);
        mETContent.setText(content);
    }


    class AddArticleTask extends AsyncTask<Article, Void, Integer> {

        @Override
        protected Integer doInBackground(Article... params) {
            ArticleController ac = new ArticleController(AddArticleActivity.this);
            Article article = params[0];
            int id = -1;
            article = ac.addArticleToServer( article.getClassify(), article.getTitle(), imageConverter(article.getContent()));
            if (article != null) {
                id = article.getId();
            }
            return id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(AddArticleActivity.this);
//            mDialog.setTitle("发布帖子");
            mDialog.setMessage("正在发布帖子，请稍后...");
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Integer id) {
            super.onPostExecute(id);
            mDialog.cancel();
            if (id != -1) {
                Toast.makeText(AddArticleActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                setResult(0);
                finish();
            } else {
                Toast.makeText(AddArticleActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            }
        }
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
            final Article article = new Article();

            String title = String.valueOf(mETTitle.getText());
            String content = String.valueOf(mETContent.getText());

            if (content.trim().length() == 0) {
                Toast.makeText(getApplicationContext(), R.string.input_can_not_none, Toast.LENGTH_SHORT).show();
                return false;
            }

            article.setTitle(title);
            article.setClassify(mClassify);
            article.setContent(content);
            article.setUsername(mUsername);

            String date = BaseTools.getCurrentDateTime();
            article.setDate(date);

//            ArticleController articleController = new ArticleController(this);

//            if (articleController.saveArticle(mClassify, article) != -1) {
//                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
//                setResult(0);
//                finish();
//            } else {
//                Toast.makeText(this, "发布失败", Toast.LENGTH_SHORT).show();
//            }
            new UploadImageTask(this, 1){
                public ProgressDialog mDialog;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mDialog = new ProgressDialog(AddArticleActivity.this);
                    mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            cancel(false);
                        }
                    });
                    mDialog.setMessage("上传中，请稍后...第1张图片");
                    mDialog.show();
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if (aBoolean) {
                        new AddArticleTask().execute(article);
                    }
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), R.string.article_add_failed, Toast.LENGTH_SHORT).show();
                }

                @Override
                protected void onProgressUpdate(String... values) {
                    super.onProgressUpdate(values);
                    System.out.println("uploading..." + values[0]);
                    if (values[0] != null || values[0].equals("")) {
                        mRemoteUrl.add(values[0]);
                        mDialog.setMessage("上传中，请稍后...第" + (mRemoteUrl.size() + 1) + "张图片");
                    } else {
                        Toast.makeText(getApplicationContext(), "第" + mRemoteUrl.size() + "张上传失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();
                    mRemoteUrl.clear();
                }
            }.execute(mImageUriList.toArray(new String[mImageUriList.size()]));

        } else if (id == android.R.id.home) {
            setResult(1);
            finish();
        } else if (id == R.id.action_add_image) {
            selectImage();
        }

        return super.onOptionsItemSelected(item);
    }

    private String imageConverter(String content) {
        String rst = content;
        for (int i = 0; i < mRemoteUrl.size(); i++) {
            rst = rst.replaceAll("<图片" + (i+1) + ">", "<img src=\"" + mRemoteUrl.get(i) + "\"/>");
        }
        return rst;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_ASK_GALLERY);
    }

}
