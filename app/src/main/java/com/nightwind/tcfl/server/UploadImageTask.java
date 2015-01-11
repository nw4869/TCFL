package com.nightwind.tcfl.server;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.oss.OSSController;
import com.nightwind.tcfl.oss.SaveCallback;

import java.io.File;
import java.io.IOException;

/**
 * Created by wind on 2015/1/7.
 */
public class UploadImageTask extends AsyncTask<String, String, Boolean> {

    private final int type;
    private final Context context;
    private OSSController mOssController;

    public UploadImageTask(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        for (String uri: params) {
            String filePath = getRealPathFromURI(Uri.parse(uri));

            if (filePath == null || !new File(filePath).exists()) {
                return null;
            }
            try {
                mOssController = new OSSController(context, type);
                String result = mOssController.syncUploadFile(filePath, new SaveCallback() {
                    @Override
                    public void onSuccess(String paramString) {
//                        publishProgress(paramString);
                    }

                    @Override
                    public void onProgress(String paramString, int paramInt1, int paramInt2) {
//                        publishProgress(Math.max(((int) (100.0 * paramInt1 / paramInt2) - 1), 0));
                    }

                    @Override
                    public void onFailure(String paramString) {
                        publishProgress("");
                    }
                });
                publishProgress(result);
                if (result == null) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
