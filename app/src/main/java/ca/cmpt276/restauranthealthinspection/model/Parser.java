package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Restaurant> csvRestaurants = new ArrayList<>();
    private List<Inspection> csvInspections = new ArrayList<>();
    private List<Violation> csvViolations = new ArrayList<>();
    private File inspectionData;
    private File restaurantData;

    public static void main(String[] args) {
        File inspectionData = null;
        File restaurantData = null;

        RestaurantManager manager = new RestaurantManager();

        try {
            inspectionData = new File ("data/inspectionreports_itr1.csv");
            restaurantData = new File ("data/restaurants_itr1.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            assert inspectionData != null;

            //
            //noinspection unchecked
            List<Inspection> inspections = new CsvToBeanBuilder(new FileReader(inspectionData.getAbsolutePath()))
                    .withType(Inspection.class)
                    .build()
                    .parse();

            assert restaurantData != null;

            //noinspection unchecked
            List<Restaurant> restaurants = new CsvToBeanBuilder(new FileReader(restaurantData.getAbsolutePath()))
                    .withType(Restaurant.class)
                    .build()
                    .parse();

            for (Restaurant res : restaurants) {
                for (Inspection ins : inspections) {
                    if(ins.getInsTrackingNumber().equals(res.getResTrackingNumber())) {
                        res.add(ins);
                    }
                }
                manager.add(res);
            }

            for (Restaurant res : manager.getRestaurants()) {
                System.out.println(res);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
