package ca.cmpt276.restauranthealthinspection.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents each restaurant.
 */
public class Restaurant implements Iterable<Inspection> {

    private String resTrackingNumber;

    private String name;

    private String address;

    private String city;

    private String resType;

    private double latitude;

    private double longitude;

    private List<Inspection> inspections = new ArrayList<>();

    // ****************************************
    // Methods for List<Inspection> inspections
    // ****************************************

    //Package Private
    Restaurant(String resTrackingNumber, String name, String address, String city, String resType, double latitude, double longitude) {
        this.resTrackingNumber = resTrackingNumber;
        this.name = name;
        this.address = address;
        this.city = city;
        this.resType = resType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    void add(Inspection inspection) {
        inspections.add(inspection);

        // Maintain *reverse* sorted date order
        // Uses lambda expression instead of anon class for readability
        inspections.sort((Inspection i1, Inspection i2)
                -> i2.getCalendar().compareTo(i1.getCalendar())
        );
    }

    public Inspection get(int index) {
        return inspections.get(index);
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspections.iterator();
    }

    // **************
    // Other methods
    // **************

    public String getAddress() {
        return address + ", " + city + ", " + "BC";
    }

    public String getCity() {
        return city;
    }

    public String getResType() {
        return resType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getResTrackingNumber() {
        return resTrackingNumber;
    }

    public String getName() {
        return name;
    }

    public List<Inspection> getInspections() {
        return Collections.unmodifiableList(inspections);
    }

    public boolean hasBeenInspected() {
        return !inspections.isEmpty();
    }

    public String getLatestInspectionDate() {
        if (inspections.isEmpty()) {
            return "";
        }
        return inspections.get(0).getFromCurrentDate();
    }

    public String getLatestInspectionTotalIssues() {
        if (inspections.isEmpty()) {
            return "";
        }
        int totalIssues = inspections.get(0).getTotalIssues();
        return String.valueOf(totalIssues);
    }

    public String getHazardLevel() {
        if (inspections.isEmpty()) {
            return "";
        }
        return inspections.get(0).getHazardRating();
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "resTrackingNumber='" + resTrackingNumber + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", resType='" + resType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", inspections=" + inspections +
                '}';
    }
}