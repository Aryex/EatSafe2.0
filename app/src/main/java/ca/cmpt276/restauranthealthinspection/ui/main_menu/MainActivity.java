package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
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

        InputStreamReader inspectionDataReader = new InputStreamReader(getResources().openRawResource(R.raw.inspectionreports_itr1));
        InputStreamReader restaurantDataReader = new InputStreamReader(getResources().openRawResource(R.raw.restaurants_itr1));

        restaurants = RestaurantManager.getInstance();

        //parse
        try {
            Parser.parseData(restaurants, inspectionDataReader, restaurantDataReader);
            Log.i("Parse success", "Successfully parsed csv file.");
        } catch (IOException e) {
            Log.e("Parse error", "Error while parsing csv file.");
            e.printStackTrace();
            throw new RuntimeException("Parse error");
        }

        for(Restaurant res : restaurants) {
            Log.d("Res Info", res.toString());
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
