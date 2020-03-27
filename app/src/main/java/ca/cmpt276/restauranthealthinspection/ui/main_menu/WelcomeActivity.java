package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.updater.FileUpdater;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.UpdaterFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Date;

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
        Log.d("test", "" + FileUpdater.lastUpdated(this));
        if (current - FileUpdater.lastUpdated(this) > UPDATE_TIME_THRESHOLD) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            UpdaterFragment updaterFragment = new UpdaterFragment();
            updaterFragment.show(fragmentManager, UpdaterFragment.TAG);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = MapsActivity.makeLaunchIntent(WelcomeActivity.this);
                    startActivity(i);
                }
            }, 1000);
        }

    }

}
