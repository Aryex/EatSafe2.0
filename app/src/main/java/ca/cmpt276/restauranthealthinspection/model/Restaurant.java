package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.bean.CsvBindByName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents each restaurant.
 */
public class Restaurant implements Iterable<Inspection> {
    @CsvBindByName(column = "TRACKINGNUMBER", required = true)
    private String resTrackingNumber;

    @CsvBindByName(column = "NAME", required = true)
    private String name;

    @CsvBindByName(column = "PHYSICALADDRESS", required = true)
    private String address;

    @CsvBindByName(column = "PHYSICALCITY", required = true)
    private String city;

    @CsvBindByName(column = "FACTYPE", required = true)
    private String resType;

    @CsvBindByName(column = "LATITUDE", required = true)
    private double latitude;

    @CsvBindByName(column = "LONGITUDE", required = true)
    private double longitude;

    private List<Inspection> inspections = new ArrayList<>();

    // ****************************************
    // Methods for List<Inspection> inspections
    // ****************************************

    public void add(Inspection inspection) {
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

    public int size() {
        return inspections.size();
    }

    public void remove(int index) {
        inspections.remove(index);
    }

    @Override
    public Iterator<Inspection> iterator() {
        return inspections.iterator();
    }

    // **************
    // Other methods
    // **************

    public String getAddress() {
        return address;
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
        return inspections;
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