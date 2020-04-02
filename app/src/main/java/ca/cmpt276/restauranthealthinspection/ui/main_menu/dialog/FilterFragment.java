package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import ca.cmpt276.restauranthealthinspection.R;

/**
 * Fragment for a filter dialog
 */

public class FilterFragment extends DialogFragment {

    public static final String TAG = "filter";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_fragment, null);
        setCancelable(false);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
