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

    static public boolean isInCurrentHour(String strDate) {
        Date date = strToDate(strDate);
        Calendar currentDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)
                && calendar.get(Calendar.HOUR_OF_DAY) == currentDate.get(Calendar.HOUR_OF_DAY);
    }

    static public String getDisplayTime1(String strDate) {
        Date date = strToDate(strDate);
        Calendar currentDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String rst = "";
        if (calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
            long diffDay = currentDate.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
            rst = diffDay + "天前";
        } else if (calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
            long diffDay = currentDate.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
            rst = diffDay + "天前";
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == currentDate.get(Calendar.HOUR_OF_DAY)) {
            long diff = currentDate.get(Calendar.HOUR_OF_DAY) - calendar.get(Calendar.HOUR_OF_DAY);
            rst = diff + "小时前";
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == currentDate.get(Calendar.HOUR_OF_DAY)) {
            long diff = currentDate.get(Calendar.MINUTE) - calendar.get(Calendar.MINUTE);
            rst = diff + "分钟前";
        } else {
            long diff = currentDate.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            rst = diff + "年前";
        }
        return rst;
    }

    static public String getDisplayTime(String strDate) {
        Date date = strToDate(strDate);
        Calendar currentDate = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        long crtTime = currentDate.getTime().getTime();
        long time = calendar.getTime().getTime();
        double diff = (crtTime - time)/1000.0/60/60/24/365;

        String rst = "";
        if (diff > 1) {
            rst = (int)diff + "年前";
        } else if ((diff *= 365) > 1) {
            rst = (int)diff + "天前";
        } else if ((diff *= 24) > 1) {
            rst = (int)diff + "小时前";
        } else {
            diff *= 60;
            rst = (int)diff + "分钟前";
        }
        return  rst;
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
