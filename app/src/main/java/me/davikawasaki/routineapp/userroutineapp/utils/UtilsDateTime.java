package me.davikawasaki.routineapp.userroutineapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kawasaki on 19/11/17.
 */

public class UtilsDateTime {

    public static String convertDateToString(Date date) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public static String convertDateTimeToString(Date date) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static Date setDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    public static Date setDateTime(int year, int month, int day, int hours, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hours, minutes);
        return cal.getTime();
    }

}
