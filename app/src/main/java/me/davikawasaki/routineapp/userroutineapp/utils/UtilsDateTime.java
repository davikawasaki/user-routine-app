package me.davikawasaki.routineapp.userroutineapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kawasaki on 19/11/17.
 */

/**
 * Utility Class with DateTime Manipulations.
 */
public class UtilsDateTime {

    /**
     * Convert receiving date to string pattern.
     * BR Pattern: dd/MM/yyyy
     * @param date
     * @return dateString
     */
    public static String convertDateToString(Date date) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    /**
     * Convert receiving dateTime to string pattern.
     * BR Pattern: dd/MM/yyyy HH:mm:ss
     * @param date
     * @return dateTimeString
     */
    public static String convertDateTimeToString(Date date) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * Receive year, month and day to construct a Date.
     * @param year
     * @param month
     * @param day
     * @return date
     */
    public static Date setDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    /**
     * Receive year, month, day, hours and minutes to construct a DateTime.
     * @param year
     * @param month
     * @param day
     * @param hours
     * @param minutes
     * @return dateTime
     */
    public static Date setDateTime(int year, int month, int day, int hours, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hours, minutes);
        return cal.getTime();
    }

}
