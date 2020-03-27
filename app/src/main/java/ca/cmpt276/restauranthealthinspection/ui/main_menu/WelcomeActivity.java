package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.UpdaterFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        showDialogs();
    }

    private void showDialogs() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        UpdaterFragment updaterFragment = new UpdaterFragment();
        updaterFragment.show(fragmentManager, UpdaterFragment.TAG);
    }

}
