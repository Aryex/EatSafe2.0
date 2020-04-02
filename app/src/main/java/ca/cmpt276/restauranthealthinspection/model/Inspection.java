package ca.cmpt276.restauranthealthinspection.model;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ca.cmpt276.restauranthealthinspection.R;

/**
 * Represents the inspections that were performed in the restaurant.
 */
public class Inspection implements Iterable<Violation>, Serializable {

    private String insTrackingNumber;

    private Calendar calendar;

    private String insType;

    private int numCritical;

    private int numNonCritical;

    private String hazardRating;

    private String violLump;

    private List<Violation> violations = new ArrayList<>();

    public static final String HAZARD_RATING_LOW = "Low";

    public static final String HAZARD_RATING_MODERATE = "Moderate";

    public static final String HAZARD_RATING_HIGH = "High";

    // ****************************************
    // Methods for List<Violation> violations
    // ****************************************

    // Package private

    Inspection(String insTrackingNumber, Calendar calendar, String insType,
               int numCritical, int numNonCritical, String hazardRating, String violLump) {
        this.insTrackingNumber = insTrackingNumber;
        this.calendar = calendar;
        this.insType = insType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violLump = violLump;
    }

    void add(Violation violation) {
        violations.add(violation);
    }

    public Violation get(int index) {
        return violations.get(index);
    }

    @Override
    public Iterator<Violation> iterator() {
        return violations.iterator();
    }

    // **************
    // Other methods
    // **************

    public String getInsType() {
        return insType;
    }

    // Calendar contains the data of the inspection
    // Year, Month, and Day is stored
    Calendar getCalendar() {
        return calendar;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    // Package private
    String getInsTrackingNumber() {
        return insTrackingNumber;
    }

    // Package private
    String getViolLump() {
        return violLump;
    }

    // Source : https://stackoverflow.com/questions/7103064/java-calculate-the-number-of-days-between-two-dates/14278129
    private int getDaysInBetween() {
        long days = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();

        return (int) Math.round(days / (60.0 * 60 * 24 * 1000)); // 60 seconds * 60 minutes * 24 hours * 1000 ms per second
    }

    // Needed for displaying date of inspection from current date in MainActivity
    public String getFromCurrentDate(Context context) {
        Log.d("Inspection Object", "getFromCurrentDate: " + calendar.getTime());
        if (getDaysInBetween() <= 30) {
            return getDaysInBetween() + context.getString(R.string.restaurant_days);
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int inspectionYear = calendar.get(Calendar.YEAR);
        // Not accounting for leap years
        if (getDaysInBetween() <= 365) {
            if (inspectionYear < currentYear) {
                return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CANADA)
                        + " " + calendar.get(Calendar.DAY_OF_MONTH)
                        + ", " + calendar.get(Calendar.YEAR);
            }
            return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CANADA)
                    + " " + calendar.get(Calendar.DAY_OF_MONTH);
        }

        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CANADA)
                + " " + calendar.get(Calendar.YEAR);
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
                ", vioLump='" + violLump + '\'' +
                '}';
    }

    int getTotalIssues() {
        return numCritical + numNonCritical;
    }
}
