package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.*;

public class MainActivity extends AppCompatActivity {

    private RestaurantManager restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupModel();

        setupRecyclerView();
    }

    private void setupModel() {
        Log.i("Start parsing", "Starting to parse data....");

        restaurants = RestaurantManager.getInstance(this);

        for (Restaurant r : restaurants) {
            Log.d("Main Activity", "onCreate: " + r);
        }

        Restaurant restaurant = restaurants.get(2);
        Log.d("MainActivity", restaurant.getResTrackingNumber());
        List<Inspection> inspections = restaurant.getInspections();

        for (Inspection ins : inspections) {
            Log.d("MainActivity", "inspection: " + ins.toString());
            for (Violation vio : ins) {
                Log.d("MainActivity", "violation: " + vio.toString());
            }
        }

    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, restaurants);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public enum HazardLevel {
        LOW, MEDIUM, HIGH
    }
}
