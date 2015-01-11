package com.nightwind.tcfl;

import android.test.InstrumentationTestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wind on 2015/1/7.
 */
public class Test extends InstrumentationTestCase {
    public void test() throws Exception{
//        assertEquals(1, 2);

        String content = "aasdfsadf<img src=\"http://img.baidu.com/img/iknow/logo-iknowxjd.gif\"/>ffffsdafsdf<img src=\"http://img.baidu.com/img/iknow/logo-iknowxjd.gif\"/>";
        final String strPattern = "<img src=\".{1,100}?\"/>";
        String[] texts = content.split(strPattern);
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(content);

        for (String text : texts) {
            if (matcher.find()) {
                String imageTag = matcher.group();
                String url = imageTag.substring(10, imageTag.length() - 3);
            }
        }
    }
}
