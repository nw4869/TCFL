package com.nightwind.tcfl.server;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

/**
 * Created by wind on 2014/12/13.
 */
public class ServerConfig {

    public static final String SERVER_REMOTE = "http://120.24.223.185/";
    public static final String SERVER_LOCAL_DEBUG = "http://192.168.1.103:8080/";

    public static final String BAIDU_AK = "a3NpssSoPbiIG6STyPhGn5ch";

    private static final int REQUEST_TIMEOUT = 5 * 1000;//设置请求超时10秒钟
    private static final int SO_TIMEOUT = 10 * 1000;  //设置等待数据超时时间10秒钟

//    private static String sServer = SERVER_REMOTE;
    private static String sServer = SERVER_LOCAL_DEBUG;
    private static String sBaiduAk = BAIDU_AK;
    static private HttpClient sClient;


    public static String getServer() {
        return sServer;
    }

    public static void setServer(String Server) {
        sServer = Server;
    }

    public static String getBaiduAk() {
        return sBaiduAk;
    }

    public static void setBaiduAk(String BaiduAk) {
        ServerConfig.sBaiduAk = BaiduAk;
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

            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            //线程安全client
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager( httpParams, schReg);

            sClient = new DefaultHttpClient(conMgr, httpParams);
        }
        return sClient;
    }

}
