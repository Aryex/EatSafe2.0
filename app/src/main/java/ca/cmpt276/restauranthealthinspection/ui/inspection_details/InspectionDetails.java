package ca.cmpt276.restauranthealthinspection.ui.inspection_details;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;

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
        populateViews();
    }

    private void populateViews() {

        setupHazard();
        setupViolations();

        TextView date = findViewById(R.id.inspectionDate);
        date.setText(inspection.getFromCurrentDate());

        TextView type = findViewById(R.id.inspectionType);
        type.setText(this.getString(R.string.inspectionTypeDisplay, inspection.getInsType()));

        TextView critical = findViewById(R.id.numberCritical);
        critical.setText(this.getString(R.string.critical_issues_found, inspection.getNumCritical()));

        TextView nonCritical = findViewById(R.id.numberNonCritical);
        nonCritical.setText(this.getString(R.string.non_critical_issues_found, inspection.getNumNonCritical()));


    }

    //set up the RecyclerView
    private void setupViolations() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViolationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViolationAdapter adapter = new RecyclerViolationAdapter(this, inspection.getViolations());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

    }

    private void setupHazard() {
        //set up hazard level depending on the hazard rating
        TextView hazardLevel = findViewById(R.id.inspectionHazardLevel);
        ImageView hazardIcon = findViewById(R.id.inspectionHazardIcon);
        LinearLayout layout = findViewById(R.id.hazardDisplay);

        hazardLevel.setText(this.getString(R.string.hazardLevelDisplay, inspection.getHazardRating()));

        switch(inspection.getHazardRating()) {
            case Inspection.HAZARD_RATING_LOW:
                hazardIcon.setImageResource(R.drawable.icon_hazard_low);
                layout.setBackgroundColor(ContextCompat.getColor(this, R.color.hazardLowInspection));
                break;
            case Inspection.HAZARD_RATING_MODERATE:
                hazardIcon.setImageResource(R.drawable.icon_hazard_medium);
                layout.setBackgroundColor(ContextCompat.getColor(this, R.color.hazardMediumInspection));
                break;
            case Inspection.HAZARD_RATING_HIGH:
                hazardIcon.setImageResource(R.drawable.icon_hazard_high);
                layout.setBackgroundColor(ContextCompat.getColor(this, R.color.hazardHighInspection));
                break;
        }
    }

    private void getExtras() {
        //get the extras from intent
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
