package com.kangaruu.sundiary.Shared;

import java.util.Calendar;

public class CalendarUtils {

    public static long getMillisForStartOfDay(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static String convertMillisToTimeString(int millis) {
        int seconds = millis / 1000 % 60;
        int minutes = millis / 1000 / 60 % 60;
        int hours = millis / 1000 / 60 / 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
