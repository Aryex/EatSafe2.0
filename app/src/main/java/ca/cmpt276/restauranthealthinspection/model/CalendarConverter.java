package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.bean.AbstractBeanField;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Represents the custom calendar converter for use in the
 * Inspection class.
 *
 * CsvCustomBindMyName annotation uses this class for conversion.
 */
public class CalendarConverter {

     static Calendar convert(String value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);

        try {
            Date date = format.parse(value);
            Calendar calendar = Calendar.getInstance();

            assert date != null;

            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to parse date");
        }
    }
}
