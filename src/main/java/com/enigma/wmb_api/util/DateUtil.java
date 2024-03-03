package com.enigma.wmb_api.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static Date parseDate(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        Date tempDate = new Date();
        try {
            tempDate = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new Timestamp(tempDate.getTime());
    }

}
