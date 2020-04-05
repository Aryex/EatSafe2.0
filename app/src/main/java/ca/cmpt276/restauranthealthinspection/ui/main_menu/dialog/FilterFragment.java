package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.model.filter.MyFilter;

/**
 * Fragment for a filter dialog
 */

public class FilterFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "filter";
    private View view;
    private Spinner spinner;
    private RestaurantManager restaurantManager;
    /*
     * Filter support
     */
    private MyFilter myFilter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_fragment, null);

        //List<Restaurant> restaurantListFull = new ArrayList<>(restaurantManager.getRestaurants());
        myFilter = MyFilter.getInstance(view.getContext());

        createSpinner();
        getAdvancedOptions();

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Filter")
                .setPositiveButton(R.string.apply_search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //myFilter.sortByHazardLevel();
                        //myFilter.sortByHazardLevel();
                        //myFilter.sortByCritVio();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }

    private void createSpinner() {
        spinner = (Spinner) view.findViewById(R.id.hazard_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.hazard_levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String hazardLevel = spinner.getItemAtPosition(position).toString();
        switch (hazardLevel.toLowerCase()) {
            case "Low":
                myFilter.setHazardLevel("Low");
                break;
            case "Moderate":
                myFilter.setHazardLevel("Moderate");
                break;
            default:
                myFilter.setHazardLevel("High");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getAdvancedOptions() {
        EditText editText = view.findViewById(R.id.crit_vio_editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String vioNumString = editText.getText().toString();
                if (vioNumString != "") {
                    int critVioNum = Integer.parseInt(vioNumString);
                    myFilter.setCritVioNum(critVioNum);
                }
            }
        });

    }
}
