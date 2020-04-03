package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import ca.cmpt276.restauranthealthinspection.R;

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
    private int critValue = 0;
    private boolean isLessThen = false;
    private String hazardLevel = "";
    private boolean keepFavorite = false;
    private OptionDialogListener optionDialogListener;


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
                .setTitle("Search options")
                .setPositiveButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Cancel");
                        Log.d(TAG, "onClick: hazard level " + hazardLevel);
                        optionDialogListener.onOptionDialogCancel();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.apply_search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: Apply");
                        Log.d(TAG, "onClick: hazard level " + hazardLevel);
                        optionDialogListener.onOptionDialogApply();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.clear_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Clear All");
                        Log.d(TAG, "onClick: hazard level " + hazardLevel);
                        optionDialogListener.onOptionDialogClearAll();
                        dismiss();
                    }
                })
                .create();
    }

    private void createInputs() {

        Spinner spinnerInequality = view.findViewById(R.id.inequality_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.inequality_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInequality.setAdapter(adapter);
        spinnerInequality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = spinnerInequality.getSelectedItem().toString();
                if(value.toLowerCase().equals("less than")){
                    isLessThen = true;
                }else{
                    isLessThen = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinnerHazard = (Spinner) view.findViewById(R.id.hazard_spinner);
        adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.hazard_levels_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHazard.setAdapter(adapter);
        spinnerHazard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hazardLevel = spinnerHazard.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CheckBox favoriteCheckBox = view.findViewById(R.id.show_favorite_check_box);
        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                keepFavorite = isChecked;
            }
        });
    }

}
