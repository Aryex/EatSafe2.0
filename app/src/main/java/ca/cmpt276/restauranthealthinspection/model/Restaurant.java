package ca.cmpt276.restauranthealthinspection.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String resTrackingNumber;
    private String name;
    private String address;
    private String resType;
    private String city;
    private boolean latitude;
    private boolean longitude;
    private List<Inspection> inspections = new ArrayList<>();

    public Restaurant(String resTrackingNumber, String name, String address, String resType, String city,
                      boolean latitude, boolean longitude, List<Inspection> inspections) {
        this.resTrackingNumber = resTrackingNumber;
        this.name = name;
        this.address = address;
        this.resType = resType;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inspections = inspections;
    }
}
