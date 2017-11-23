package me.davikawasaki.routineapp.userroutineapp.utils;

/**
 * Created by kawasaki on 15/11/17.
 */

/**
 * Utility Class with String Manipulations.
 */
public class UtilsString {

    /**
     * Check if string is empty
     * @param text
     * @return emptyStatus
     */
    public static boolean stringEmpty(String text) {
        return (text.equals(null) || text.matches("") || text.trim().length() == 0);
    }

}
