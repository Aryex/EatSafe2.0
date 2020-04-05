package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import ca.cmpt276.restauranthealthinspection.R;
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
    private String hazardLevel;
    private String vioNumString;

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
        getCritVioOption();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Filter");
        builder.setPositiveButton(R.string.apply_search, (dialogInterface, i) -> {
            myFilter.setHazardLevel(hazardLevel);
            setCritVioOption();
            dismiss();
        });
        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder
                .create();
    }

    private void createSpinner() {
        spinner = view.findViewById(R.id.hazard_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.hazard_levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedLevel = spinner.getItemAtPosition(position).toString();

        if (selectedLevel.equals(view.getContext().getString(R.string.hazard_rating_low))) {
            hazardLevel = view.getContext().getString(R.string.hazard_rating_low);
        } else if (selectedLevel.equals(view.getContext().getString(R.string.hazard_rating_medium))) {
            hazardLevel = view.getContext().getString(R.string.hazard_rating_medium);
        } else if (selectedLevel.equals(view.getContext().getString(R.string.hazard_rating_high))) {
            hazardLevel = view.getContext().getString(R.string.hazard_rating_high);
        } else {
            hazardLevel = view.getContext().getString(R.string.empty_string);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void getCritVioOption() {
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
                vioNumString = editText.getText().toString();
            }
        });
    }

    private void setCritVioOption() {
        if (!(vioNumString == null || vioNumString.length() == 0)) {
            int critVioNum = Integer.parseInt(vioNumString);
            myFilter.setCritVioNum(critVioNum);
        }
    }
}
