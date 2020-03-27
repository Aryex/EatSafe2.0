package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.updater.FileUpdater;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MapActivity;

/**
 * Fragment for a checking update dialog
 */

public class CheckUpdateFragment extends DialogFragment {

    public static final String TAG = "check_update";
    private final int TIMEOUT = 5000;
    private boolean doneChecking = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.checking_updating_fragment, null);
        setCancelable(false);

        //check for new data on separate thread
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                checkForNewData();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    private void checkForNewData() {

        //get when the server was last changed
        FileUpdater.lastDateServerModified(CheckUpdateFragment.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!doneChecking) {
                    Toast.makeText(getContext(), "Server timed out", Toast.LENGTH_SHORT).show();
                    cancel();
                }
            }
        }, TIMEOUT);
    }

    public void cancel() {
        //change activities
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!doneChecking) {
                    Intent i = MapActivity.makeLaunchIntent(getContext());
                    startActivity(i);
                    doneChecking = true;
                    onStop();
                }
            }
        }, 500);

    }

    //go to next fragment and destroy this one
    public void update() {
        FragmentManager fragmentManager = getFragmentManager();
        UpdaterFragment updaterFragment = new UpdaterFragment();
        updaterFragment.show(fragmentManager, UpdaterFragment.TAG);
        doneChecking = true;
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
