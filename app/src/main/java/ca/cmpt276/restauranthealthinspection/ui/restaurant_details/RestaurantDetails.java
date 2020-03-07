package ca.cmpt276.restauranthealthinspection.ui.restaurant_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.inspection_details.InspectionDetails;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MainActivity;

public class RestaurantDetails extends AppCompatActivity {

    public static final String INTENT_TAG_TRACKING_ID = "trackingID";
    private String trackingID;
    private int index;
    private RestaurantManager manager = RestaurantManager.getInstance(this);;
    /*// DUMMY CLASS AND VARIABLES
    private class Inspection {
    }

    private ArrayList<Inspection> inspectionList = new ArrayList<>();
    private ArrayList<Integer> dummyCriticalIssues = new ArrayList<>();
    private ArrayList<Integer> dummyNoncriticalIssues = new ArrayList<>();
    private ArrayList<String> dummyDates = new ArrayList<>();
    private ArrayList<MainActivity.HazardLevel> dummyHazardLevel = new ArrayList<>();*/

    public static Intent makeLaunchIntent(Context c, String trackingID) {
        Intent i = new Intent(c, RestaurantDetails.class);
        i.putExtra(INTENT_TAG_TRACKING_ID, trackingID);
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

        //setupDummyData();
        setupTextViews();

        populateListView();
        registerClickCallback();
    }

    private void extractIntent() {
        Intent i = getIntent();
        trackingID = i.getStringExtra(INTENT_TAG_TRACKING_ID);
        index = manager.getRestaurantIndexByID(trackingID);
    }

    /*private void setupDummyData() {
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
    }*/

    private void setupTextViews() {
        TextView nameTV = findViewById(R.id.resNameTV);
        nameTV.setText(manager.get(index).getName());

        TextView addressTV = findViewById(R.id.addressTV);
        addressTV.setText(manager.get(index).getAddress());

        TextView latitudeTV = findViewById(R.id.latitudeTV);
        latitudeTV.setText(""+manager.get(index).getLatitude());

        TextView longitudeTV = findViewById(R.id.longitudeTV);
        longitudeTV.setText(""+manager.get(index).getLongitude());
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
            switch (hazardLevel) {
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

        public String getLevelString(MainActivity.HazardLevel hazardLevel) {
            switch (hazardLevel) {
                case LOW:
                    return "LOW";
                case MEDIUM:
                    return "MEDIUM";
                case HIGH:
                    return "HIGH";
                default:
            }
            return "LOW";
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
            ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            imageView.setImageResource(getIconID(dummyHazardLevel.get(index)));

            // Hazard level:
            TextView levelText = (TextView) itemView.findViewById(R.id.levelTV);
            levelText.setText("- Hazard level: " + getLevelString(dummyHazardLevel.get(index)));

            // Make:
            TextView critText = (TextView) itemView.findViewById(R.id.item_crit);
            critText.setText("- Critical issues found: " + dummyCriticalIssues.get(index));

            // Year:
            TextView nonCritText = (TextView) itemView.findViewById(R.id.item_noncrit);
            nonCritText.setText("- Non-critical issues found: " + dummyNoncriticalIssues.get(index));

            // Date:
            TextView dateText = (TextView) itemView.findViewById(R.id.item_date);
            dateText.setText("- Inspection date: " + dummyDates.get(index));

            return itemView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Returns to previous activity
            // Needed to keep passed intent
            onBackPressed();
            finish();
            return true;
        }

        return (super.onOptionsItemSelected(item));
    }
}
