package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MapActivity;

/**
 * Asks user if they want to download data now
 */

public class UpdaterFragment extends DialogFragment {

    public static final String TAG = "updating";

    public interface UpdaterDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    private UpdaterDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);

        builder.setTitle(R.string.update_available)
                .setMessage(R.string.update_message)
                .setPositiveButton(R.string.dialog_button_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //switch fragments
                        FragmentManager fragmentManager = getFragmentManager();
                        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
                        progressDialogFragment.show(fragmentManager, ProgressDialogFragment.TAG);
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //on cancel start map activity
                        Intent i = MapActivity.makeLaunchIntent(getContext());
                        startActivity(i);
                    }
                });


        return builder.create();
    }
}
