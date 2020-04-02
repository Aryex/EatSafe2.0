package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.fragment.app.DialogFragment;

import ca.cmpt276.restauranthealthinspection.R;

/**
 * Fragment for a filter dialog
 */

public class FilterFragment extends DialogFragment {

    public static final String TAG = "filter";
    private View view;
    private SearchView searchView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_fragment, null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Filter")
                .setPositiveButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.apply_search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }
}
