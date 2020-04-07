package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
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
public class RestaurantListActivity extends AppCompatActivity implements FilterOptionDialog.OptionDialogListener {

    private RestaurantManager restaurantManager;

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

        restaurantManager = RestaurantManager.getInstance(this);
        setupRecyclerView();
        Log.d("test", "created");
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerViewAdapter = new RecyclerViewAdapter(this, restaurantManager.getRestaurants());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onOptionDialogApply() {
        Toast.makeText(this, "Dialog Apply!", Toast.LENGTH_SHORT).show();
        //TODO: update list pls
    }

    @Override
    public void onOptionDialogCancel() {
        Toast.makeText(this, "Dialog Cancel :(", Toast.LENGTH_SHORT).show();
        //TODO: update list pls

    }

    @Override
    public void onOptionDialogClearAll() {
        Toast.makeText(this, "Dialog Clear All!", Toast.LENGTH_SHORT).show();
        //TODO: update list pls

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    public void onResume() {
        setupRecyclerView();
        super.onResume();
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
        RestaurantManager searchEngine = RestaurantManager.getInstance(this);
        if(searchEngine.hasQuery){
            searchItem.expandActionView();
            searchView.setQuery(getString(R.string.pizza),true);
        }
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

    public enum HazardLevel {
        LOW, MEDIUM, HIGH
    }
}
