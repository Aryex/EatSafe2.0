package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.FilterOptionDialog;

/**
 * Main menu display a list of restaurants and their appropriate information.
 * It will also initializes the model.
 */
public class RestaurantListActivity extends AppCompatActivity {

    private RestaurantManager restaurants;

    private RecyclerViewAdapter recyclerViewAdapter;

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, RestaurantListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        restaurants = RestaurantManager.getInstance(this);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerViewAdapter = new RecyclerViewAdapter(this, restaurants);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public enum HazardLevel {
        LOW, MEDIUM, HIGH
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    //Menu setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_restuarant, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_return_map) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.menu_filter) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FilterOptionDialog filterOptionDialog = new FilterOptionDialog();
            filterOptionDialog.show(fragmentManager, FilterOptionDialog.TAG);
            return true;
        }
        if (item.getItemId() == R.id.menu_action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
