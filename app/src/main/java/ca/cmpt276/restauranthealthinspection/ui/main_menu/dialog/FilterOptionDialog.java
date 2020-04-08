package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

/**
 * Fragment for a filter dialog
 */

public class FilterOptionDialog extends DialogFragment {

    public interface OptionDialogListener {
        public void onOptionDialogApply();
        public void onOptionDialogCancel();
        public void onOptionDialogClearAll();
    }

    public static final String TAG = "Option Dialog";

    private View view;
    private int vioCritNum = 0;
    private boolean isLessThan = false;
    private String hazardLevel = "All";
    private boolean keepFavorite = false;
    private OptionDialogListener optionDialogListener;
    private String vioNumString = "0";
    private String searchName = "";
    private MyFilter myFilter;
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
        myFilter = MyFilter.getInstance(view.getContext());

        createInputs();

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.search_options)
                .setPositiveButton(R.string.apply_search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Apply");
                        Log.d(TAG, "onClick: hazard level " + hazardLevel);

                        myFilter.resetAllFilterOptions(searchName, hazardLevel, vioCritNum, keepFavorite, isLessThan, false);

                        if (recyclerViewAdapter != null) {
                            // Filter options applied on RecycleView
                            recyclerViewAdapter.getFilter().filter((view.getContext().getResources().getString(R.string.empty_string)));
                        }

                        optionDialogListener.onOptionDialogApply();
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

                        myFilter.resetAllFilterOptions((view.getContext().getResources().getString(R.string.empty_string)),
                                (view.getContext().getResources().getString(R.string.hazard_rating_all)),
                                0, false, false, true);
                        if (recyclerViewAdapter != null) {
                            // Filter options applied on RecycleView
                            recyclerViewAdapter.getFilter().filter((view.getContext().getResources().getString(R.string.empty_string)));
                        }

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
        spinnerInequality.setSelection(1);
        spinnerInequality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = spinnerInequality.getSelectedItem().toString();
                isLessThan = value.equals(getString(R.string.less_than));
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
        spinnerHazard.setSelection(3);
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

    private void getCritVioOption() {
        EditText editText = view.findViewById(R.id.crit_vio_editText);
        editText.setText("0");
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
                if (vioNumString.equals(view.getContext().getResources().getString(R.string.empty_string))) {
                    vioCritNum = 0;
                } else {
                    vioCritNum = Integer.parseInt(vioNumString);
                }
            }
        });
    }

    private void getSearchName() {
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
}
