package com.nightwind.tcfl.server;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

/**
 * Created by wind on 2014/12/13.
 */
public class ServerConfig {

    public static final String SERVER_REMOTE = "http://120.24.223.185/";
    public static final String SERVER_LOCAL_DEBUG = "http://192.168.1.123:8081/";

    private static final int REQUEST_TIMEOUT = 5 * 1000;//设置请求超时10秒钟
    private static final int SO_TIMEOUT = 10 * 1000;  //设置等待数据超时时间10秒钟

    private static String sServer = SERVER_REMOTE;
    static private HttpClient sClient;


    public static String getServer() {
        return sServer;
    }

    public static void setServer(String Server) {
        sServer = Server;
    }

    /**
     * 初始化HttpClient，并设置超时
     * @return
     */
    public static HttpClient getHttpClient() {
        if (sClient == null) {
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
            sClient = new DefaultHttpClient(httpParams);
        }
        return sClient;
    }

}