package com.nightwind.tcfl.tool;

import android.app.Activity;
import android.util.DisplayMetrics;

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


    static private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static public SimpleDateFormat getDateFormat() {return sDateFormat;}

    /**
     * 日期转换 yyyy-MM-dd HH:mm
     *
     * @param str
     * @return
     */
    static public Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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


}
