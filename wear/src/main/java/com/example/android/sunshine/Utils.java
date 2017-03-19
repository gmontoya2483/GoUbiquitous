package com.example.android.sunshine;

import java.util.Calendar;

/**
 * Created by Gabriel on 19/03/2017.
 */

public class Utils {


    private static final String AM_STRING="am";
    private static final String PM_STRING="pm";
    public static final int TIME_WITHOUT_SECONDS=0;
    public static final int TIME_WITH_SECONDS=1;


    public static String getFormatedTime(Calendar calendar, Boolean is24Hour, int withSeconds){
        String timeString=null;

        //Formatear la hora
        String hourString;
        if (is24Hour){
            hourString = formatTwoDigitNumber(calendar.get(Calendar.HOUR_OF_DAY));
        }else{
            int hour = calendar.get(Calendar.HOUR);
            if (hour == 0) {
                hour = 12;
            }
            hourString = String.valueOf(hour);
        }

        if (withSeconds==TIME_WITH_SECONDS){
            timeString=String.format("%s:%02d:%02d",hourString,calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND));

        }else{
            timeString=String.format("%s:%02d",hourString,calendar.get(Calendar.MINUTE));
        }
         return timeString;
    }


    private  static String formatTwoDigitNumber(int hour) {
        return String.format("%02d", hour);
    }
}
