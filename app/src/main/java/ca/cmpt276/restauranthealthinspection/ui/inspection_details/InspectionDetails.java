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
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;

import static ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails.INTENT_TAG_TRACKING_ID;

public class InspectionDetails extends AppCompatActivity {

    public static final String INTENT_TAG_INSPECTION = "Inspection chosen";

    private Inspection inspection;

    public static Intent makeLaunchIntent(Context c, Inspection inspection) {
        Intent i = new Intent(c, InspectionDetails.class);
        i.putExtra(INTENT_TAG_INSPECTION, inspection);
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

        getExtras();
    }

    private void getExtras() {
        Intent i = getIntent();
        inspection = (Inspection) i.getSerializableExtra(INTENT_TAG_INSPECTION);
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
