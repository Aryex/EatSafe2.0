package ca.cmpt276.restauranthealthinspection.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the manager for restaurants.
 *
 * Contains a singleton to store and retain restaurants.
 */
public class RestaurantManager implements Iterable<Restaurant> {
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

    public RestaurantManager() {
        // Nothing is here - forces singleton use.
    }

    private List<Restaurant> restaurants = new ArrayList<>();

    public void add(Restaurant restaurant) {
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

    public int size() {
        return restaurants.size();
    }

    public void remove(int index) {
        restaurants.remove(index);
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }
}