package vipjokerstudio.com.telegramchart.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
    private static final SimpleDateFormat formatterWithYear = new SimpleDateFormat("dd MMM yyyy");
    private static final SimpleDateFormat formatterWithDayOfWeek = new SimpleDateFormat("EEE, MMM dd");
    public static String formatDate(long date){
        Date d  = new Date(date);
        return formatter.format(d);
    }

    public static String formatDateWithDayOfWeek(long date){

        Date d  = new Date(date);
        return formatterWithDayOfWeek.format(d);
    }
    public static String formatDateWithYear(long date){

        Date d  = new Date(date);
        return formatterWithYear.format(d);
    }
}
