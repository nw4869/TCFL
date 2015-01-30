package com.nightwind.tcfl.activity;

import android.accounts.AuthenticatorException;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ProgressBarDetermininate;
import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.oss.OSSController;
import com.nightwind.tcfl.oss.SaveCallback;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class UploadImageActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_ASK_GALLERY = 100;
    private static final int REQUEST_CROP = 101;
    public static final String EXTRA_IMAGE = "extra_image";
    public static final String ARG_USERNAME = "username";
    private final String filePath = Environment.getExternalStorageDirectory() + "/tcfl/temp" + UUID.randomUUID() + ".png";//temp file
    private final String image_location_path = "file://" + filePath;
    Uri imageUri = Uri.parse(image_location_path);//The Uri to store the big bitmap
    public static final String ARG_IMG_URL = "imgURL";
    private ImageView mImageView;
    private Button mBtnUpload;
    private Button mBtnSelectImage;
    private ProgressBarDetermininate mProgressBar;

    private String mLastImgURL;
    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private AsyncTask<String, Integer, String> mChangeAvatarTask;
    private OSSController mOssController;
    private String mUsername;

    @Override
    int getLayoutResID() {
        return R.layout.activity_upload_image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mImageView = (ImageView) findViewById(R.id.avatar);
        mBtnSelectImage = (Button) findViewById(R.id.selectImage);
        mBtnUpload = (Button) findViewById(R.id.uploadImage);
        mProgressBar = (ProgressBarDetermininate) findViewById(R.id.progressBar);

        mBtnSelectImage.setOnClickListener(this);
        mBtnUpload.setOnClickListener(this);

//        mProgressBar.setProgress(50);

//        ViewCompat.setTransitionName(mImageView, EXTRA_IMAGE);

        if (getIntent() != null) {
            //显示头像，如果有的话
            mLastImgURL = getIntent().getStringExtra(ARG_IMG_URL);
            if (mLastImgURL != null) {
                imageLoader.displayImage(mLastImgURL, mImageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }
                });
            }
            mUsername = getIntent().getStringExtra(ARG_USERNAME);
            String selfName = new Auth(this).getUsername();
            if (selfName == null || !mUsername.equals(selfName)) {
                //查看头像
                mBtnSelectImage.setVisibility(View.GONE);
                mBtnUpload.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.selectImage:
                //选择、裁剪图片
                selectImage();
                break;
            case R.id.uploadImage:
                if (filePath == null || !new File(filePath).exists()) {
                    Toast.makeText(getApplicationContext(), "请选择文件", Toast.LENGTH_SHORT).show();
                    break;
                }
                //上传服务器
                mChangeAvatarTask = new AsyncTask<String, Integer, String>() {

                    private int type;
                    private String filePath;

                    public ProgressDialog mDialog;

                    @Override
                    protected void onCancelled() {
                        if (mOssController != null) {
                            mOssController.cancel();
                        }
                        //删除临时文件
                        File file = new File(filePath);
                        file.delete();
                        super.onCancelled();
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mDialog = new ProgressDialog(UploadImageActivity.this);
                        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                cancel(false);
                            }
                        });
                        mDialog.setMessage("上传中，请稍后...");
                        mDialog.show();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        type = Integer.valueOf(params[0]);
                        filePath = params[1];
                        if (filePath == null || !new File(filePath).exists()) {
                            return null;
                        }
                        try {
                            mOssController = new OSSController(getApplicationContext(), type);
                            String result = mOssController.syncUploadFile(filePath  , new SaveCallback() {
                                @Override
                                public void onSuccess(String paramString) {
                                    publishProgress(100);
                                }

                                @Override
                                public void onProgress(String paramString, int paramInt1, int paramInt2) {
                                    publishProgress(Math.max(((int) (100.0 * paramInt1 / paramInt2) - 1), 0));
                                }

                                @Override
                                public void onFailure(String paramString) {
                                    publishProgress(0);
                                }
                            });
                            UserController uc = new UserController(getApplicationContext());
                            if (uc.updateAvatar(result)) {
                                return result;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AuthenticatorException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        System.out.println("uploading..." + values[0]);
                        mProgressBar.setProgress(values[0]);
                        mDialog.setMessage("上传中，请稍后..." + values[0] + "%");
                    }

                    @Override
                    protected void onPostExecute(String url) {
                        super.onPostExecute(url);
                        if (url == null) {
                            Toast.makeText(getApplicationContext(), R.string.upload_failed, Toast.LENGTH_SHORT).show();
                            mDialog.cancel();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.upload_success, Toast.LENGTH_SHORT).show();
                            mDialog.cancel();
                            MemoryCacheUtils.removeFromCache(url,
                                    imageLoader.getMemoryCache());
                            DiskCacheUtils.removeFromCache(url, imageLoader.getDiskCache());
                        }
                        //删除临时文件
                        File file = new File(filePath);
                        file.delete();
                    }
                }.execute(String.valueOf(OSSController.TYPE_UPLOAD_AVATAR), filePath);
                break;
        }
    }

    private void selectImage() {
        //从相册选择照片
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_ASK_GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ASK_GALLERY) {
            //得到选择的图片结果，请求剪裁图片
            if (data != null) {
                Intent intent = getCropImageIntent(data.getData());
                startActivityForResult(intent, REQUEST_CROP);
            }

        } else if (requestCode == REQUEST_CROP) {
            //取得剪裁图片结果
            if (resultCode == RESULT_OK) {
                if (imageUri != null) {
                    Bitmap bitmap = null;
                    try {
                        //从uri中读取bitmap
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "找不到该文件，请重试", Toast.LENGTH_SHORT).show();
                    }
                    //显示bitmap
                    mImageView.setImageBitmap(bitmap);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 请求剪切图片
     * @param photoUri
     * @return
     */
    private Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setData(photoUri);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 80);
//        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        return intent;
    }

}
