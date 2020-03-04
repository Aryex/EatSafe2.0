package ca.cmpt276.restauranthealthinspection.model;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the manager for restaurants.
 *
 * Contains a singleton to store and retain restaurants.
 */
public class RestaurantManager implements Iterable<Restaurant> {
    private static List<Restaurant> restaurants = new ArrayList<>();
    // **********
    // Singleton
    // **********
    private static RestaurantManager instance;

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }

        return instance;
    }

    private RestaurantManager() {
    }

    // Package private
    void add(Restaurant restaurant) {
        restaurants.add(restaurant);

        // Maintain sorted name order
        // Uses lambda expression instead of anon class for readability
        //noinspection ComparatorCombinators
        restaurants.sort((Restaurant r1, Restaurant r2)
                -> r1.getName().compareTo(r2.getName())
        );
    }

    public Restaurant get(int index) {
        return restaurants.get(index);
    }

    // Returns an unmodifiable list so the list cannot be changed
    public List<Restaurant> getRestaurants() {
        return Collections.unmodifiableList(restaurants);
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }
}