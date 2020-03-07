package ca.cmpt276.restauranthealthinspection.ui.inspection_details;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;

import static ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails.INTENT_TAG_TRACKING_ID;

public class InspectionDetails extends AppCompatActivity {

    public static final String INTENT_TAG_CALENDAR = "Calendar date";

    public static Intent makeLaunchIntent(Context c, String trackingID, Calendar calendar) {
        Intent i = new Intent(c, InspectionDetails.class);
        i.putExtra(INTENT_TAG_TRACKING_ID, trackingID);
        i.putExtra(INTENT_TAG_CALENDAR, calendar);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        String trackingID = i.getStringExtra(INTENT_TAG_TRACKING_ID);

        TextView textView = findViewById(R.id.textViewInspectionDetails);
        textView.setText(trackingID);
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
