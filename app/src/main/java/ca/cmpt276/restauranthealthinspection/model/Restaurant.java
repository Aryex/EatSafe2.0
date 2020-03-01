package ca.cmpt276.restauranthealthinspection.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String trackingNumber;
    private String name;
    private String address;
    private String resType;
    private String city;
    private boolean latitude;
    private boolean longitude;
    private List<Inspection> inspections = new ArrayList<>();
}
