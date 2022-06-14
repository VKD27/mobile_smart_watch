package ai.docty.mobile_smart_watch.util;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GlobalMethods {

    public static String getTempIntoFahrenheit(float tempInCelsius) {
        //  (°C × 9/5) + 32
        double infoValue =  (tempInCelsius *1.8000)+32.00;
       // return new DecimalFormat("0.00").format(infoValue);
        return new DecimalFormat("0.0").format(infoValue);
    }

    public static String getTempTimeByIntegerMin(int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hour, min);
    }

    public static String getTimeByIntegerMin(int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hour, min);
    }

    public static String convertIntToHHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format(Locale.getDefault(),"%02d:%02d:%02d", h,m,s);
    }

    public static String getIntegerToHHmm(int minutes) {
        int hour = (minutes / 60);
        int min = (minutes - hour * 60);
        return String.format(Locale.getDefault(), "%02d:%02d", (hour - 1), min);
    }

    public static String convertTimeToHHMm(String dateTime) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            // Get date from string
            Date date = dateFormatter.parse(dateTime);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
            // Get time from date
            assert date != null;
            String displayValue = timeFormatter.format(date);
            Log.e("convertDisplayValue>>", displayValue);
            return displayValue;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
//        long s = seconds % 60;
//        long m = (seconds / 60) % 60;
//        long h = (seconds / (60 * 60)) % 24;
//        return String.format(Locale.getDefault(),"%02d:%02d:%02d", h,m,s);
    }


    public static String convertDoubleToStringWithDecimal(double infoValue) {
        //Log.e("resultValue", "decimal_ddf: " + resultValue);
        return new DecimalFormat("0.00").format(infoValue);
    }

    public static String convertDoubleToCelsiusWithDecimal(double infoValue) {
        //Log.e("resultValue", "decimal_ddf: " + resultValue);
        return new DecimalFormat("0.0").format(infoValue);
    }

    public static String formatTime(long millis) {
        long secs = millis / 1000;
        return String.format(Locale.getDefault(),"%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
    }

    public String fromMinutesToHHmm(int minutes) {
        long hours = TimeUnit.MINUTES.toHours((long) minutes);
        long remainMinutes = minutes - TimeUnit.HOURS.toMinutes(hours);
        return String.format(Locale.getDefault(), "%02d:%02d", hours, remainMinutes);
    }
    
    /*public String fromMinToHHmm(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        //String.format(Locale.getDefault(), "%02d"+TIME_SEPARATOR+"%02d", hours, minutes);
        // another result
//        int hours = (int) minutes/ 3600;
//        int temp = (int) minutes - hours * 3600;
//        int mins = temp / 60;
//        temp = temp - mins * 60;
//        int secs = temp;
//        Log.e("hours-mins-secs::", String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, mins, secs));
        int deep_hour = minutes / 60;
        int deep_minute = (minutes - deep_hour * 60);
        Log.e("hours_mins::", String.format(Locale.getDefault(), "%02d:%02d",deep_hour,deep_minute));

        return  String.format(Locale.getDefault(),"%02d:%02d",h,m); // output : "02:00"
    }*/
    
    /*private String intToStringTimeFormat(int time)
    {
        String strTemp;
        int minutes = time / 60;
        int seconds = time % 60;

        if(minutes < 10)
            strTemp = "0" + Integer.toString(minutes) + ":";
        else
            strTemp = Integer.toString(minutes) + ":";

        if(seconds < 10)
            strTemp = strTemp + "0" + Integer.toString(seconds);
        else
            strTemp = strTemp + Integer.toString(seconds);

        return strTemp;
    }*/

}
