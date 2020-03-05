package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarConverter extends AbstractBeanField {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
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
