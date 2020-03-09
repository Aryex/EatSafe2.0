package ca.cmpt276.restauranthealthinspection.ui.inspection_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;

/**
 * Display details of a single Inspection
 */

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
        RecyclerViolationAdapter adapter = new RecyclerViolationAdapter(this, inspection);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

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
