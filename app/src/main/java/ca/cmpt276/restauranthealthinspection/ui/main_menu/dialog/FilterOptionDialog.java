package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class FilterOptionDialog extends DialogFragment {

    public static final String FAVORITE_FLAG = "Favorite Flag";

    public interface OptionDialogListener {
        public void onOptionDialogApply();
        public void onOptionDialogCancel();
        public void onOptionDialogClearAll();
    }

    public static final String TAG = "Option Dialog";
    public static final String NAME_SEARCH = "Name Search";
    public static final String HAZARD_LEVEL = "Hazard level";
    public static final String CRIT_VIO_NUM = "Number of critical violations";

    private View view;
    private int critValue = 0;
    private boolean isLessThen = false;
    private String hazardLevel = "";
    private boolean keepFavorite = false;
    private OptionDialogListener optionDialogListener;
    private String vioNumString = "0";
    private String searchName;

    private RecyclerViewAdapter recyclerViewAdapter;

    public FilterOptionDialog(RecyclerViewAdapter recyclerViewAdapter) {
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    /**
     * From https://developer.android.com/guide/topics/ui/dialogs
     * */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            optionDialogListener = (OptionDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + ": caller must implement OptionDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_fragment, null);
        createInputs();
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.search_options)
                .setPositiveButton(R.string.apply_search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Apply");
                        Log.d(TAG, "onClick: hazard level " + hazardLevel);
                        optionDialogListener.onOptionDialogApply();

                        String constraints = searchName + "-" + hazardLevel + "-" + vioNumString;

                        setHazardLevelPref(hazardLevel);
                        setVioNumPref(Integer.parseInt(vioNumString));
                        setNamePref(searchName);
                        setFavoritePref(keepFavorite);

                        recyclerViewAdapter.getFilter().filter(constraints);

                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Cancel");
                        Log.d(TAG, "onClick: hazard level " + hazardLevel);
                        optionDialogListener.onOptionDialogCancel();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.clear_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Clear All");
                        Log.d(TAG, "onClick: hazard level " + hazardLevel);

                        String constraints = "" + "-" + "" + "-" + "0";

                        setHazardLevelPref(hazardLevel);
                        setVioNumPref(Integer.parseInt(vioNumString));
                        setNamePref(searchName);
                        setFavoritePref(false);

                        recyclerViewAdapter.getFilter().filter(constraints);
                        recyclerViewAdapter.notifyDataSetChanged();

                        optionDialogListener.onOptionDialogClearAll();
                        dismiss();
                    }
                })
                .create();
    }

    private void createInputs() {

        // Inequality spinner for number of critical violations
        Spinner spinnerInequality = view.findViewById(R.id.inequality_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.inequality_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInequality.setAdapter(adapter);
        spinnerInequality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = spinnerInequality.getSelectedItem().toString();
                isLessThen = value.equals(getString(R.string.less_than));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getCritVioOption();
        getSearchName();

        // Hazard spinner
        Spinner spinnerHazard = view.findViewById(R.id.hazard_spinner);
        adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.hazard_levels_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHazard.setAdapter(adapter);
        spinnerHazard.setSelection(getSpinnerPosition());
        spinnerHazard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hazardLevel = spinnerHazard.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Favorite checkbox
        CheckBox favoriteCheckBox = view.findViewById(R.id.show_favorite_check_box);
        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                keepFavorite = isChecked;
            }
        });
    }

    private int getSpinnerPosition() {
        String savedLevel = getHazardLevelPref(view.getContext());
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
                if (vioNumString.equals("")) {
                    vioNumString = "0";
                }
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

    public void setFavoritePref(boolean flag) {
        SharedPreferences pref = getContext().getSharedPreferences(FAVORITE_FLAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FAVORITE_FLAG, flag);
        editor.apply();
    }
    public static boolean getFavoritePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(FAVORITE_FLAG, MODE_PRIVATE);
        boolean defaultVal = false;
        return pref.getBoolean(FAVORITE_FLAG, defaultVal);
    }
}
