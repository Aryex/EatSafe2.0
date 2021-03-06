package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import ca.cmpt276.restauranthealthinspection.model.filter.MyFilter;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.RecyclerViewAdapter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragment for a filter dialog
 */
public class FilterFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "filter";
    private static final String NAME_SEARCH = "Name Search";
    private static final String HAZARD_LEVEL = "Hazard level";
    private static final String CRIT_VIO_NUM = "Number of critical violations";
    private View view;
    private Spinner spinner;
    private String hazardLevel;
    private String vioNumString;
    private String searchName;

    private RecyclerViewAdapter recyclerViewAdapter;

    /*
     * Filter support
     */
    private MyFilter myFilter;

    public FilterFragment(RecyclerViewAdapter recyclerViewAdapter) {
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_fragment, null);

        myFilter = MyFilter.getInstance(view.getContext());

        createSpinner();
        getCritVioOption();
        getSearchName();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(getString(R.string.menu_filter));

        builder.setPositiveButton(R.string.apply_search, (dialogInterface, i) -> {
            String constraints = searchName + "-" + hazardLevel + "-" + vioNumString;

            setHazardLevelPref(hazardLevel);
            setVioNumPref(Integer.parseInt(vioNumString));
            setNamePref(searchName);

            recyclerViewAdapter.getFilter().filter(constraints);

            dismiss();
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }

    private void createSpinner() {
        spinner = view.findViewById(R.id.hazard_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.hazard_levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(getSpinnerPosition());
        spinner.setOnItemSelectedListener(this);
    }

    private int getSpinnerPosition() {
        String savedLevel = MyFilter.getHazardLevelPref(view.getContext());
        if (savedLevel.equals(view.getContext().getString(R.string.hazard_rating_low))) {
            return 0;
        } else if (savedLevel.equals(view.getContext().getString(R.string.hazard_rating_medium))) {
            return 1;
        } else if (savedLevel.equals(view.getContext().getString(R.string.hazard_rating_high))) {
            return 2;
        } else {
            return 3;
        }
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
        vioNumString = Integer.toString(MyFilter.getVioNumPref(view.getContext()));
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
                /*if (vioNumString.length() == 0) {
                    vioNumString = Integer.toString(myFilter.getCritVioNum());
                }*/
            }
        });
    }

    private void getSearchName() {
        searchName = "";
        EditText editText = view.findViewById(R.id.nameEditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchName = editText.getText().toString();
            }
        });
    }

    public void setNamePref(String searchName) {
        SharedPreferences pref = getContext().getSharedPreferences(NAME_SEARCH, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(NAME_SEARCH, searchName);
        editor.apply();
    }

    public static String getNamePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SEARCH, MODE_PRIVATE);
        String defaultVal = "";
        return pref.getString(NAME_SEARCH, defaultVal);
    }

    public void setHazardLevelPref(String hazardLevel) {
        SharedPreferences pref = getContext().getSharedPreferences(HAZARD_LEVEL, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(HAZARD_LEVEL, hazardLevel);
        editor.apply();
    }

    public static String getHazardLevelPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(HAZARD_LEVEL, MODE_PRIVATE);
        String defaultVal = "";
        return pref.getString(HAZARD_LEVEL, defaultVal);
    }

    public void setVioNumPref(int vioNum) {
        SharedPreferences pref = getContext().getSharedPreferences(CRIT_VIO_NUM, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(CRIT_VIO_NUM, vioNum);
        editor.apply();
    }

    public static int getVioNumPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CRIT_VIO_NUM, MODE_PRIVATE);
        int defaultVal = 0;
        return pref.getInt(CRIT_VIO_NUM, defaultVal);
    }
}
