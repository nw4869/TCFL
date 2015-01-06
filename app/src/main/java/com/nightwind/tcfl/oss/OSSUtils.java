//package com.nightwind.tcfl.oss;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.util.Log;
//
//import com.aliyun.mbaas.oss.OSSClient;
//import com.aliyun.mbaas.oss.callback.SaveCallback;
//import com.aliyun.mbaas.oss.model.AccessControlList;
//import com.aliyun.mbaas.oss.model.OSSException;
//import com.aliyun.mbaas.oss.model.TokenGenerator;
//import com.aliyun.mbaas.oss.storage.OSSBucket;
//import com.aliyun.mbaas.oss.storage.OSSData;
//import com.aliyun.mbaas.oss.storage.TaskHandler;
//import com.nightwind.tcfl.Auth;
//
//import java.io.IOException;
//
///**
// * Created by wind on 2014/12/31.
// */
//public class OSSUtils {
//    private final Context mContext;
//
//    public OSSUtils(Context context) {
//        mContext = context;
//        OSSClient.setApplicationContext(context.getApplicationContext());
////        OSSClient.setGlobalDefaultTokenGenerator(TokenGenerator tokenGen);
//
//    }
//
//
//    private TaskHandler uploadImg(String fileKey, String fileType, byte[] data, SaveCallback saveCallback) {
//
////        mapBarAndObject.put(fileKey, progressBar); // 建立进度条与下载文件的对应关系
//        OSSBucket sampleBucket1 = new OSSBucket("nw-test");
//        sampleBucket1.setBucketTokenGen(new TokenGenerator() {
//            @Override
//            public String generateToken(String httpMethod, String md5, String type, String date,
//                                        String ossHeaders, String resource) {
//                try {
//
////                    String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
////                            + resource;
////
////                    System.out.println("LOCAL, content = " + content);
////
////                    String token = OSSToolKit.generateToken(accessKey, screctKey, content);
////
////                    System.out.println("LOCAL, token = " + token);
//
//                    Auth auth = new Auth(mContext);
//
//                    String remoteToken = auth.getUploadToken(date/*, resource*/);
//
//                    return remoteToken;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return "";
//            }
//        });
//        sampleBucket1.setBucketACL(AccessControlList.PRIVATE);
//
//        OSSData ossData = new OSSData(sampleBucket1, fileKey);
//        try {
////            ossData.setData(new byte[]{88, 88, 88}, "raw");
//            ossData.setData(data, fileType);
//        } catch (OSSException e) {
//            e.printStackTrace();
//        }
//        ossData.enableUploadCheckMd5sum();
////        TaskHandler handler = ossData.uploadInBackground(new SaveCallback() {
////            @Override
////            public void onSuccess(String objectKey) {
//////                makeToast("upload ", objectKey, successHandler);
////            }
////
////            @Override
////            public void onProgress(String objectKey, int byteCount, int totalSize) {
////                Log.d("onProgress", String.format("%d %d\n", byteCount, totalSize));
//////                updateBar(objectKey, byteCount, totalSize, MainActivity.this.progressHandler);
////            }
////
////            @Override
////            public void onFailure(String s, OSSException e) {
////                Log.d("onFailure", e.toString());
////            }
////        });
//        TaskHandler handler = ossData.uploadInBackground(saveCallback);
//        return handler;
//    }
//
//
//}
