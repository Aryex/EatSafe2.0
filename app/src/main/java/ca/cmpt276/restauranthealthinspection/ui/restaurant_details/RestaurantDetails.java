package ca.cmpt276.restauranthealthinspection.ui.restaurant_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.inspection_details.InspectionDetails;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MapActivity;

/**
 * Display details of single restaurant
 */
public class RestaurantDetails extends AppCompatActivity {

    public static final String INTENT_TAG_TRACKING_ID = "tracking ID";
    private String trackingID;
    private int index;
    private RestaurantManager manager = RestaurantManager.getInstance(this);
    private List<Inspection> inspectionList;

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
        setupTextViews();
        setupCoordToMap();

        if (inspectionList.size() != 0) {
            TextView noInspectionText = findViewById(R.id.noInspectionTV);
            noInspectionText.setText(getString(R.string.empty_string));
            populateListView();
            registerClickCallback();
        }
    }

    private void setupCoordToMap() {
        LinearLayout linearLayout = findViewById(R.id.restaurantDetailCoordLayout);
        final Context context = this;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restaurant restaurant = manager.get(index);
                double lat = restaurant.getLatitude();
                double lng = restaurant.getLongitude();
                Intent intent = MapActivity.makeLaunchIntent(context, lat, lng, trackingID);
                startActivity(intent);
                finishAffinity(); // Pop stack
            }
        });
    }

    private void extractIntent() {
        Intent i = getIntent();
        trackingID = i.getStringExtra(INTENT_TAG_TRACKING_ID);
        index = manager.getRestaurantIndexByID(trackingID);
        inspectionList = manager.get(index).getInspections();
    }

    private void setupTextViews() {
        TextView nameTV = findViewById(R.id.resNameTV);
        nameTV.setText(manager.get(index).getName());

        TextView addressTV = findViewById(R.id.addressTV);
        addressTV.setText(manager.get(index).getAddress());

        TextView latitudeTV = findViewById(R.id.latitudeTV);
        String latitude = Double.toString(manager.get(index).getLatitude());
        latitudeTV.setText(getString(R.string.latitude, latitude));

        TextView longitudeTV = findViewById(R.id.longitudeTV);
        String longitude = Double.toString(manager.get(index).getLongitude());
        longitudeTV.setText(getString(R.string.longitude, longitude));
    }

    private void populateListView() {
        ArrayAdapter<Inspection> adapter = new InspectionLVAdapter(this, inspectionList);
        ListView list = findViewById(R.id.inspectionListView);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.inspectionListView);
        list.setOnItemClickListener((parent, viewClicked, position, id) -> {
            Inspection clickedInspection = inspectionList.get(position);

            Intent intent = InspectionDetails.makeLaunchIntent(this, clickedInspection);
            startActivity(intent);
        });
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
