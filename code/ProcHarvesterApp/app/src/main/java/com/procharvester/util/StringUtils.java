package com.procharvester.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

    public static String formatTimeStamp(long time) {
        Date date = new Date(time);
        return new SimpleDateFormat("HH:mm:ss.SSS").format(date);
    }
}
