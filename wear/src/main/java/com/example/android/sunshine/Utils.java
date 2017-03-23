package com.example.android.sunshine;

import android.content.Context;
import android.graphics.Rect;

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


    //This helper method returns the X position to center a text on the X axis
    public static float centerX (Rect bounds, float withOf){
        float xStart;

        xStart=bounds.centerX()- (withOf/2f);

        return xStart;
    }


    public static String formatTemperature(Context context, double temperature) {

        int temperatureFormatResourceId = R.string.format_temperature;

        /* For presentation, assume the user doesn't care about tenths of a degree. */
        return String.format(context.getString(temperatureFormatResourceId), temperature);
    }


    public static String getStringWeekDay (Context context,int day){

        String weekDay;

        switch (day){
            case 1:
                weekDay=context.getString(R.string.Sunday);
                break;
            case 2:
                weekDay=context.getString(R.string.Monday);
                break;
            case 3:
                weekDay=context.getString(R.string.Tuesday);
                break;
            case 4:
                weekDay=context.getString(R.string.Wednesday);
                break;
            case 5:
                weekDay=context.getString(R.string.Thursday);
                break;
            case 6:
                weekDay=context.getString(R.string.Friday);
                break;
            case 7:
                weekDay=context.getString(R.string.Saturday);
                break;
            default:
                weekDay="ERR";
                break;

        }

        return weekDay;

    }


    public static String getStringMonth (Context context,int month){

        String monthDesc;

        switch (month){
            case 0:
                monthDesc=context.getString(R.string.January);
                break;
            case 1:
                monthDesc=context.getString(R.string.February);
                break;
            case 2:
                monthDesc=context.getString(R.string.March);
                break;
            case 3:
                monthDesc=context.getString(R.string.April);
                break;
            case 4:
                monthDesc=context.getString(R.string.May);
                break;
            case 5:
                monthDesc=context.getString(R.string.June);
                break;
            case 6:
                monthDesc=context.getString(R.string.July);
                break;
            case 7:
                monthDesc=context.getString(R.string.August);
                break;
            case 8:
                monthDesc=context.getString(R.string.September);
                break;
            case 9:
                monthDesc=context.getString(R.string.October);
                break;
            case 10:
                monthDesc=context.getString(R.string.November);
                break;
            case 11:
                monthDesc=context.getString(R.string.December);
                break;

            default:
                monthDesc="ERR";
                break;

        }

        return monthDesc;

    }


    public static String getFormattedDate(Context context, Calendar calendar){
        String date;
        String weekDay=getStringWeekDay(context,calendar.get(Calendar.DAY_OF_WEEK));
        String month=getStringMonth(context,calendar.get(Calendar.MONTH));
        String day=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String year=String.valueOf(calendar.get(Calendar.YEAR));

        int DateFormatResourceId = R.string.format_date;

        date= String.format(context.getString(DateFormatResourceId),weekDay,month,day,year);
        return date;

    }














    /**
     *
     * To find weather icon. Copied from app code.
     * @param weatherId
     * @return resource id
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }





}
