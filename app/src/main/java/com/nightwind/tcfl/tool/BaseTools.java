package com.nightwind.tcfl.tool;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.nightwind.tcfl.server.ServerConfig;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BaseTools {

    /**
     * 获取屏幕的宽度
     */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }


    static private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static public SimpleDateFormat getDateFormat() {return sDateFormat;}

    /**
     * 日期转换 yyyy-MM-dd HH:mm
     *
     * @param str
     * @return
     */
    static public Date strToDate(String str) {
        SimpleDateFormat format = sDateFormat;
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获得当前时间 yyyy-MM-dd HH:mm
     * @return
     */
    static public String getCurrentDateTime() {
        SimpleDateFormat sdf = sDateFormat;
        Date dt = new Date();
        String dts = sdf.format(dt);
        return dts;
    }

    static public String getMonthAndDay(String date) {
        return date.substring(5, 5+5);
    }
    static public String getTime(String date) {
        return date.substring(11, 11+5);
    }

    static public String getDateNextDays(String strDate, int addDay) {
        Date date = strToDate(strDate);
        Calendar cldDate = Calendar.getInstance();
        cldDate.setTime(date);
        cldDate.add(Calendar.DATE, addDay);
        SimpleDateFormat sdf = sDateFormat;
        return sdf.format(cldDate.getTime());
    }

    /**
     *
     * @param strDate
     * @return
     */
    static public boolean isInCurrentYear(String strDate) {
        Date date = strToDate(strDate);
        Calendar currentDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR);
    }
    static public boolean isInCurrentMonth(String strDate) {
        Date date = strToDate(strDate);
        Calendar currentDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH);
    }
    static public boolean isInCurrentDay(String strDate) {
        Date date = strToDate(strDate);
        Calendar currentDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 调用百度API翻译
     * @param from
     * @param to
     * @param query
     * @return 成功则返回翻译的内容,失败返回长度为零的string
     */
    static public String translate(String from, String to, String query)  {
        String url = "http://openapi.baidu.com/public/2.0/bmt/translate";
        url += "?client_id=" + ServerConfig.getBaiduAk();
        url += "&from=" + from;
        url += "&to=" + to;
        HttpResponse httpResponse;
        String responseJson;
        String from1, to1;
        StringBuilder result = new StringBuilder();
        try {
            url += "&q=" + URLEncoder.encode(query, "utf-8");
            HttpGet httpGet = new HttpGet(url);
            httpResponse = ServerConfig.getHttpClient().execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200)
            {
                responseJson = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jo = new JSONObject(responseJson);
                from1 = jo.getString("from");
                to1 = jo.getString("to");
                JSONArray ja = jo.getJSONArray("trans_result");
                final int length = ja.length();
                for (int i = 0; i < length; i++) {
                    JSONObject joRst = ja.getJSONObject(i);
                    result.append(joRst.getString("dst"));
                    if (i != length - 1) {
                        result.append("\n");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
