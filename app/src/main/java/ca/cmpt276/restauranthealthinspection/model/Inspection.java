package ca.cmpt276.restauranthealthinspection.model;

import java.util.ArrayList;
import java.util.List;

public class Inspection {
    private String trackingNumber;
    private int month;
    private int day;
    private int year;
    private String insType;
    private int numCritical;
    private int numNonCritical;
    private enum hazardRating {
        LOW, MEDIUM, HIGH
    }
    private List<Violation> violations = new ArrayList<>();
}
