package ca.cmpt276.restauranthealthinspection.model;

import java.util.ArrayList;
import java.util.List;

public class Inspection {
    private String insTrackingNumber;
    private int month;
    private int day;
    private int year;
    private String insType;
    private int numCritical;
    private int numNonCritical;
    private hazardRating hazardRating;
    private List<Violation> violations = new ArrayList<>();

    public enum hazardRating {
        LOW,
        MEDIUM,
        HIGH
    }

    public Inspection(String insTrackingNumber, int month, int day, int year, String insType, int numCritical,
                      int numNonCritical, Inspection.hazardRating hazardRating, List<Violation> violations) {
        this.insTrackingNumber = insTrackingNumber;
        this.month = month;
        this.day = day;
        this.year = year;
        this.insType = insType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardRating = hazardRating;
        this.violations = violations;
    }
}
