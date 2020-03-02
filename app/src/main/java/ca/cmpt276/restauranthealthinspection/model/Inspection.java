package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Inspection {
    @CsvBindByName(column = "TrackingNumber", required = true)
    private String insTrackingNumber;

    @CsvBindByName(column = "InspectionDate", required = true)
    @CsvDate("yyyyMMdd")
    private Calendar calendar;

    public Calendar getCalendar() {
        return calendar;
    }

    private int month;

    private int day;

    private int year;

    @CsvBindByName(column = "InspType", required = true)
    private String insType;

    @CsvBindByName(column = "NumCritical", required = true)
    private int numCritical;

    @CsvBindByName(column = "NumNonCritical", required = true)
    private int numNonCritical;

    @CsvBindByName(column = "HazardRating", required = true)
    private String hazardRating;

    @CsvBindByName(column = "ViolLump")
    private String violLump;

    private List<Violation> violations = new ArrayList<>();

    public String getInsTrackingNumber() {
        return insTrackingNumber;
    }

    @Override
    public String toString() {
        return "\n\tInspection{" +
                "insTrackingNumber='" + insTrackingNumber + '\'' +
                ", year=" + calendar.get(Calendar.YEAR) +
                ", month=" + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CANADA) +
                ", day=" + calendar.get(Calendar.DAY_OF_MONTH) +
                ", insType='" + insType + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating='" + hazardRating + '\'' +
                ", violLump='" + violLump + '\'' +
                '}';
    }

//    [5] I want to be told when something happened in an intelligent format so that it's easier to understand than dates:
//
//    If it was within 30 days, tell me the number of days ago it was (such as "24 days")
//    Otherwise, if it was less than a year ago, tell me the month and day (such as "May 12")
//    Otherwise, tell me just the month and year (such as "May 2018")
}
