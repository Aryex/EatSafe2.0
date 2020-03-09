package ca.cmpt276.restauranthealthinspection.model;

import android.content.Context;
import android.util.Log;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;

import static java.sql.Types.NULL;

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

    public static RestaurantManager getInstance(Context context) {
        if (instance == null) {
            instance = new RestaurantManager();

            InputStreamReader inspectionDataReader = new InputStreamReader(context.getResources().openRawResource(R.raw.inspectionreports_itr1));
            InputStreamReader restaurantDataReader = new InputStreamReader(context.getResources().openRawResource(R.raw.restaurants_itr1));

            try {
                Parser.parseData(instance, inspectionDataReader, restaurantDataReader);
                Log.i("Parse success", "Successfully parsed csv data.");
            } catch (IOException | CsvValidationException e) {
                Log.e("Parse error", "Error while parsing csv data");
                e.printStackTrace();
                throw new RuntimeException("Parse error");
            }
        }

        return instance;
    }

    private RestaurantManager() {
        // Nothing, ensures singleton usage.
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

    public int restaurantCount() {
        return restaurants.size();
    }

    public int getRestaurantIndexByID(String trackingID) {
        int size = restaurantCount();
        for (int i = 0; i < size; i++) {
            if (this.get(i).getResTrackingNumber().equals(trackingID))
                return i;
        }
        return NULL;
    }
}