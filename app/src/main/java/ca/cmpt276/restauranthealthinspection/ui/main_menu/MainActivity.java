package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.*;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> dummyNames = new ArrayList<>();
    private ArrayList<String> dummyDates = new ArrayList<>();
    private ArrayList<HazardLevel> dummyHazardLevel = new ArrayList<>();


    private RestaurantManager restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i("Start parsing", "Starting to parse data....");

        restaurants = RestaurantManager.getInstance(this);

        for(Restaurant r : restaurants){
            Log.d("Main Activity", "onCreate: " + r);
        }

        setupDummyData();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, dummyNames, dummyDates, dummyHazardLevel);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupDummyData() {
        dummyNames.add("Mc Donalds");
        dummyDates.add("November 20th, 2020");
        dummyHazardLevel.add(HazardLevel.LOW);

        dummyNames.add("7-Eleven");
        dummyDates.add("19 Days ago");
        dummyHazardLevel.add(HazardLevel.MEDIUM);

        dummyNames.add("Boston Pizza");
        dummyDates.add("May 2017");
        dummyHazardLevel.add(HazardLevel.HIGH);

        dummyNames.add("Chachi's");
        dummyDates.add("5 Days ago");
        dummyHazardLevel.add(HazardLevel.MEDIUM);

        dummyNames.add("Some Asian Restaurant");
        dummyDates.add("21 Days ago");
        dummyHazardLevel.add(HazardLevel.HIGH);

    }

    public enum HazardLevel{
        LOW,MEDIUM,HIGH
    }

}
