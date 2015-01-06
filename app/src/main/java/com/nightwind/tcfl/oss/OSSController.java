package com.nightwind.tcfl.oss;

import android.accounts.AuthenticatorException;
import android.content.Context;
import android.util.Log;

import com.nightwind.tcfl.Auth;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public class OSSController {

    private static final String TAG = "com.example.android_example.OSSController";
    public static final int TYPE_UPLOAD_AVATAR = 0;
    public static final int TYPE_UPLOAD_IMG = 1;
    private final int mType;
    private final Context mContext;

    protected String host = "oss-cn-shenzhen.aliyuncs.com";
    protected String bucketName;
    protected String objectName;
//    protected String contentLength;
    protected String contentType;
    protected String date;
    protected String authorization;
    protected String url;

    protected HttpUriRequest request;
//    protected HttpClient httpClient;
//
    private byte[] dataToSave;
    private DefaultHttpClient httpClient;
    private AtomicBoolean isCancel = new AtomicBoolean(false);

    public OSSController(Context context, int type) {
        mContext = context;
        mType = type;
        httpClient = new DefaultHttpClient();
        httpClient.setRedirectHandler(new RedirectHandler() {
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                return false;
            }

            public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
                return null;
            }
        });
    }

    private boolean initUploadRequest() throws IOException {
        Auth auth = new Auth(mContext);
        String json = "";
        switch (mType) {
            case TYPE_UPLOAD_AVATAR:
                json = auth.getUploadToken(mType);
                break;
            case TYPE_UPLOAD_IMG:
                json = auth.getUploadToken(mType);
                break;
        }
        try {
            JSONObject jo = new JSONObject(json);
            if (jo.getBoolean("success")) {
                host = jo.getString("host");
                bucketName = jo.getString("bucketName");
                objectName = jo.getString("objectName");
                date = jo.getString("date");
                contentType = jo.getString("contentType");
                authorization = jo.getString("authorization");

                url = ("http://" + host + "/" + objectName);
                request = new HttpPut(url);

                request.setHeader("Date", date);
                request.setHeader("Content-Type", contentType);
                request.setHeader("Host",/* bucketName + "." + */host);
                request.setHeader("Authorization", authorization);
                return true;
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON 解析错误");
        }

        return false;
    }

//    private String getUploadToken(int type) throws IOException {
//        String url = "http://192.168.1.123:8081/MyLogin/GetUploadImgToken1";
//        url += "?username=nw";
//        url += "&token=";
//        url += "&type=" + type;
//
//        HttpGet request = new HttpGet(url);
//
//        HttpResponse httpResponse = httpClient.execute(request);
//        String hrJson = "";
//        if (httpResponse.getStatusLine().getStatusCode() == 200) {
//            hrJson = EntityUtils.toString(httpResponse.getEntity());
//            System.out.println("hrJson = " + hrJson);
//        }
//        return hrJson;
//    }

    public String syncUpload(byte[] data) throws IOException, AuthenticatorException {
        initUploadRequest();
        dataToSave = data;

        HttpPut httpPut = (HttpPut) request;
        InputStream in = new ByteArrayInputStream(this.dataToSave);
        InputStreamEntity ise = new InputStreamEntity(in, this.dataToSave.length);
        httpPut.setEntity(ise);

        HttpResponse hr =  httpClient.execute(request);

        System.out.println("hr=" + EntityUtils.toString(hr.getEntity()));
        if (hr.getStatusLine().getStatusCode() != 200) {
            throw new AuthenticatorException();
        }

        System.out.println("getUrl=" + url);
        return url;
    }

    public String syncUploadFile(String filePath) throws IOException, AuthenticatorException {
        initUploadRequest();
        HttpPut httpPut = (HttpPut) request;

        File file = new File(filePath);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStreamEntity ise = new InputStreamEntity(in, file.length());
        httpPut.setEntity(ise);
        HttpResponse hr =  httpClient.execute(request);

        if (hr.getStatusLine().getStatusCode() != 200) {
            System.out.println("hr=" + EntityUtils.toString(hr.getEntity()));
            throw new AuthenticatorException();
        }

        System.out.println("getUrl=" + url);
        return url;
    }


    public String syncUploadFile(String filePath, SaveCallback saveCallback) throws IOException, AuthenticatorException {
        if (initUploadRequest() == false) {
            throw new AuthenticatorException();
        }
        HttpPut httpPut = (HttpPut) request;

        File file = new File(filePath);
        InputStream in = null;

        in = new FileInputStream(file);

        MeasuableInputStream min = new MeasuableInputStream(objectName, in, saveCallback, (int)file.length());
        min.setSwitch(isCancel);
        InputStreamEntity ise = new InputStreamEntity(min, file.length());
        httpPut.setEntity(ise);
        HttpResponse hr =  httpClient.execute(request);

        if (hr.getStatusLine().getStatusCode() != 200) {
            System.out.println("hr=" + EntityUtils.toString(hr.getEntity()));
            saveCallback.onFailure(objectName);
            throw new AuthenticatorException();
        }

        saveCallback.onSuccess(url);
        System.out.println("getUrl=" + url);
        return url;
    }

    public void cancel() {
        isCancel.set(true);
    }
}
