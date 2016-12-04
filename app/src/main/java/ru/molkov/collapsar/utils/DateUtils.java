package ru.molkov.collapsar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static Date toDate(String date) {
        Date result = null;
        try {
            SimpleDateFormat parser = new SimpleDateFormat(Constants.APOD_DATE_FORMAT, Locale.US);
            result = parser.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String toString(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.APOD_DATE_FORMAT, Locale.US);
        return formatter.format(date);
    }

    public static String friendlyFormat(Date date, boolean withRelativeTime) {
        int diff = daysBetween(date, new Date());
        if (withRelativeTime && diff <= 5) {
            return android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime()).toString();
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(Constants.FRIENDLY_DATE_FORMAT, Locale.US);
            return formatter.format(date);
        }
    }

    public static List<Date> getDatesToLoad(Date dateFrom, int count) {
        List<Date> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int diff = 0 - i;
            result.add(addDays(dateFrom, diff));
        }
        return result;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static int daysBetween(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static Date toGMT(Date localDate) {
        TimeZone tz = TimeZone.getDefault();
        Date result = new Date(localDate.getTime() - tz.getRawOffset());

        // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
        if (tz.inDaylightTime(result)) {
            Date dstDate = new Date(result.getTime() - tz.getDSTSavings());

            // check to make sure we have not crossed back into standard time
            // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
            if (tz.inDaylightTime(dstDate)) {
                result = dstDate;
            }
        }
        return result;
    }
}
