package ca.cmpt276.restauranthealthinspection.ui.restaurant_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.ui.inspection_details.InspectionDetails;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MainActivity;

public class RestaurantDetails extends AppCompatActivity {

    public static final String INTENT_TAG_RESTAURANT_NAME = "restaurantName";
    private String restaurantName;

    // DUMMY CLASS AND VARIABLES
    private class Inspection {
    }
    private ArrayList<Inspection> inspectionList = new ArrayList<>();
    private ArrayList<Integer> dummyCriticalIssues = new ArrayList<>();
    private ArrayList<Integer> dummyNoncriticalIssues = new ArrayList<>();
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        extractIntent();

        setupDummyData();
        setupTextViews();

        populateListView();
        registerClickCallback();
    }

    private void extractIntent() {
        Intent i = getIntent();
        restaurantName = i.getStringExtra(INTENT_TAG_RESTAURANT_NAME);
    }

    private void setupDummyData() {
        dummyCriticalIssues.add(2);
        dummyNoncriticalIssues.add(1);
        dummyDates.add("November 20th, 2020");
        dummyHazardLevel.add(MainActivity.HazardLevel.MEDIUM);
        inspectionList.add(new Inspection());

        dummyCriticalIssues.add(4);
        dummyNoncriticalIssues.add(0);
        dummyDates.add("19 Days ago");
        dummyHazardLevel.add(MainActivity.HazardLevel.HIGH);
        inspectionList.add(new Inspection());

        dummyCriticalIssues.add(1);
        dummyNoncriticalIssues.add(1);
        dummyDates.add("5 Days ago");
        dummyHazardLevel.add(MainActivity.HazardLevel.LOW);
        inspectionList.add(new Inspection());
    }

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

    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.inspectionListView);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.inspectionListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {

                Inspection clickedInspection = inspectionList.get(position);
                String message = "You clicked position " + position;
                Toast.makeText(RestaurantDetails.this, message, Toast.LENGTH_SHORT).show();

                Intent intent = InspectionDetails.makeLaunchIntent(RestaurantDetails.this, restaurantName);
                startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Inspection> {
        public MyListAdapter() {
            super(RestaurantDetails.this, R.layout.inspection_item_view, inspectionList);
        }

        public int getIconID(MainActivity.HazardLevel hazardLevel) {
            switch (hazardLevel){
                case LOW:
                    return R.drawable.icon_hazard_low;
                case MEDIUM:
                    return R.drawable.icon_hazard_medium;
                case HIGH:
                    return R.drawable.icon_hazard_high;
                default:
            }
            return R.drawable.icon_hazard_low;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspection_item_view, parent, false);
            }

            int index = position;
            // Fill the view
            ImageView imageView = (ImageView)itemView.findViewById(R.id.item_icon);
            imageView.setImageResource(getIconID(dummyHazardLevel.get(index)));

            // Make:
            TextView makeText = (TextView) itemView.findViewById(R.id.item_crit);
            makeText.setText("Critical issues found: " + dummyCriticalIssues.get(index));

            // Year:
            TextView yearText = (TextView) itemView.findViewById(R.id.item_noncrit);
            yearText.setText("Non-critical issues found: " + dummyNoncriticalIssues.get(index));

            // Condition:
            TextView condionText = (TextView) itemView.findViewById(R.id.item_date);
            condionText.setText("Inspection date: " + dummyDates.get(index));

            return itemView;
        }
    }
}
