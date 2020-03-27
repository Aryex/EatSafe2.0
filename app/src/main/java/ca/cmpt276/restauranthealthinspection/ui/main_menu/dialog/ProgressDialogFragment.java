package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.updater.FileUpdater;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MapsActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG = "downloading";

    private ProgressBar progressBar;

    private Call<ResponseBody> restaurantsCall;
    private Call<ResponseBody> inspectionsCall;
    private Call<JsonInfo> urlCall;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.downloading_progress_fragment, null);
        setCancelable(false);
        progressBar = view.findViewById(R.id.updatingProgress);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                urlCall.cancel();
                if (restaurantsCall != null) {
                    restaurantsCall.cancel();
                }
                if (inspectionsCall != null) {
                    inspectionsCall.cancel();
                }

                Intent i = MapsActivity.makeLaunchIntent(getContext());
                startActivity(i);

            }
        };

        FileUpdater.startDownloadWithDelay(view.getContext(), this);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.cancel, listener)
                .create();
    }

    public void setProgress(int percent) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (getContext() != null) {
                    progressBar.setProgress(percent);

                    if (progressBar.getProgress() == 100) {
                        FileUpdater.completeDownload(getContext());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = MapsActivity.makeLaunchIntent(getContext());
                                startActivity(i);
                            }
                        }, 1000);

                        onStop();
                    }
                }

            }
        });
    }

    public int getProgress() {

        return progressBar.getProgress();
    }

    public Call<ResponseBody> getRestaurantsCall() {
        return restaurantsCall;
    }

    public void setRestaurantsCall(Call<ResponseBody> restaurantsCall) {
        this.restaurantsCall = restaurantsCall;
    }

    public Call<ResponseBody> getInspectionsCall() {
        return inspectionsCall;
    }

    public void setInspectionsCall(Call<ResponseBody> inspectionsCall) {
        this.inspectionsCall = inspectionsCall;
    }

    public Call<JsonInfo> getUrlCall() {
        return urlCall;
    }

    public void setUrlCall(Call<JsonInfo> urlCall) {
        this.urlCall = urlCall;
    }
}
