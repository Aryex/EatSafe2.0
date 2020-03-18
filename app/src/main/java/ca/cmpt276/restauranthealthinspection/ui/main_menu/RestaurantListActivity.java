package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.*;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.UpdaterDialogFragment;

/**
 * Main menu display a list of restaurants and their appropriate information.
 * It will also initializes the model.
 */
public class RestaurantListActivity extends AppCompatActivity {

    private RestaurantManager restaurants;

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, RestaurantListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showUpdaterDialog();

        setupModel();

        setupRecyclerView();
    }

    public void showUpdaterDialog() {
        DialogFragment updaterDialog = new UpdaterDialogFragment();
        updaterDialog.show(getSupportFragmentManager(), "Updater Dialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.setTitle("Updating...");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);

        progressDialog.show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }


    private void setupModel() {
        Log.i("Start parsing", "Starting to parse data....");

        restaurants = RestaurantManager.getInstance(this);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, restaurants);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public enum HazardLevel {
        LOW, MEDIUM, HIGH
    }
}
