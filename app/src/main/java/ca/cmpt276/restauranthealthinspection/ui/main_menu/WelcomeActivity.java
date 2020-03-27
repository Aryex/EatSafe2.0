package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.updater.FileUpdater;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.CheckUpdateFragment;

/**
 * Welcome activity before the Map Activity
 */

public class WelcomeActivity extends AppCompatActivity {

    private final long UPDATE_TIME_THRESHOLD = 72000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        showDialogs();
    }

    private void showDialogs() {
        long current = System.currentTimeMillis();
        //if longer than 20 hours
        if (current - FileUpdater.lastUpdated(this) > UPDATE_TIME_THRESHOLD) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            CheckUpdateFragment checkUpdateFragment = new CheckUpdateFragment();
            checkUpdateFragment.show(fragmentManager, CheckUpdateFragment.TAG);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //start map activity
                    Intent i = MapActivity.makeLaunchIntent(WelcomeActivity.this);
                    startActivity(i);
                }
            }, 1000);
        }

    }

}
