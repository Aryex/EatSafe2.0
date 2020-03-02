package ca.cmpt276.restauranthealthinspection.ui.restaurant_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MainActivity;

public class RestaurantDetails extends AppCompatActivity {

    public static final String INTENT_TAG_RESTAURANT_NAME = "restaurantName";
    private Intent i = getIntent();
    private String restaurantName;

    private ArrayList<String> dummyNames = new ArrayList<>();
    private ArrayList<String> dummyDates = new ArrayList<>();
    private ArrayList<MainActivity.HazardLevel> dummyHazardLevel = new ArrayList<>();

    public static Intent makeLaunchIntent(Context c, String restaurantName){
        Intent i = new Intent(c,RestaurantDetails.class);
        i.putExtra(INTENT_TAG_RESTAURANT_NAME, restaurantName);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        i = getIntent();
        restaurantName = i.getStringExtra(INTENT_TAG_RESTAURANT_NAME);

        //setupDummyData();
        setupTextViews();
        //setupInspectionListView();
    }

    /*private void setupDummyData() {
        dummyNames.add("Mc Donalds");
        dummyDates.add("November 20th, 2020");
        dummyHazardLevel.add(MainActivity.HazardLevel.LOW);

        dummyNames.add("7-Eleven");
        dummyDates.add("19 Days ago");
        dummyHazardLevel.add(MainActivity.HazardLevel.MEDIUM);

        dummyNames.add("Boston Pizza");
        dummyDates.add("May 2017");
        dummyHazardLevel.add(MainActivity.HazardLevel.HIGH);

        dummyNames.add("Chachi's");
        dummyDates.add("5 Days ago");
        dummyHazardLevel.add(MainActivity.HazardLevel.MEDIUM);

        dummyNames.add("Some Asian Restaurant");
        dummyDates.add("21 Days ago");
        dummyHazardLevel.add(MainActivity.HazardLevel.HIGH);
    }*/

    private void setupTextViews() {
        TextView nameTV = findViewById(R.id.resNameTV);
        nameTV.setText(restaurantName);

        TextView addressTV = findViewById(R.id.addressTV);
        addressTV.setText(getString(R.string.address, "Dummy address"));

        TextView latitudeTV = findViewById(R.id.lattitudeTV);
        latitudeTV.setText(getString(R.string.latitude, "49.20610961"));

        TextView longitudeTV = findViewById(R.id.longtitude);
        longitudeTV.setText(getString(R.string.longitude, "-122.8668064"));
    }

    /*private void setupInspectionListView() {
        ListView lv = findViewById(R.id.inspectionListView);
        InspectionLVAdapter adapter = new InspectionLVAdapter(this, dummyNames, dummyDates, dummyHazardLevel);
        lv.setAdapter(adapter);
        lv.setLayoutManager(new LinearLayoutManager(this));
    }*/
}
