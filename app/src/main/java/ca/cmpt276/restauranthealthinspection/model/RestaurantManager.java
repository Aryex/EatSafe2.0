package ca.cmpt276.restauranthealthinspection.model;

import java.util.ArrayList;
import java.util.List;

public class RestaurantManager {
    private List<Restaurant> restaurants = new ArrayList<>();

    private static RestaurantManager instance;

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
    }
}
