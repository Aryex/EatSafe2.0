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
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;

import androidx.fragment.app.FragmentManager;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.model.updater.FileUpdater;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MapActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Displays progress bar for downloading
 */

public class ProgressDialogFragment extends DialogFragment {

    static final String TAG = "downloading";

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

                //cancel all calls
                urlCall.cancel();
                if (restaurantsCall != null) {
                    restaurantsCall.cancel();
                }
                if (inspectionsCall != null) {
                    inspectionsCall.cancel();
                }

                //start the map activity
                Intent i = MapActivity.makeLaunchIntent(getContext());
                startActivity(i);

            }
        };

        //start download
        FileUpdater.startDownloadWithDelay(view.getContext(), this);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.cancel, listener)
                .create();
    }

    public void setProgress(int percent) {

        //switch to main thread and change progress bar
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (getContext() != null) {
                    progressBar.setProgress(percent);

                    //signal the FileUpdater that the download is complete, and change to Map activity
                    if (progressBar.getProgress() == 100) {

//                        FileUpdater.completeDownload(getContext());
//                        getFragmentManager().beginTransaction().remove(ProgressDialogFragment.this).commit();
//                        Intent i = MapActivity.makeLaunchIntent(getContext());
//                        startActivity(i);

                        List<Restaurant> updatedRestaurants = getFavourites();

                        if (!updatedRestaurants.isEmpty()) {
                            getFragmentManager().beginTransaction().remove(ProgressDialogFragment.this).commit();
                            FragmentManager fragmentManager = getFragmentManager();
                            FavouriteNotificationFragment favouriteNotificationFragment = new FavouriteNotificationFragment(updatedRestaurants);
                            favouriteNotificationFragment.show(fragmentManager, FavouriteNotificationFragment.TAG);
                        }
                        else {
                            getFragmentManager().beginTransaction().remove(ProgressDialogFragment.this).commit();
                            Intent i = MapActivity.makeLaunchIntent(getContext());
                            startActivity(i);
                        }
                    }
                }
            }
        });
    }

    private List<Restaurant> getFavourites() {

        RestaurantManager oldManager = RestaurantManager.getInstance(getActivity());
        List<Restaurant> favourites = new ArrayList<>(oldManager.getFavourites());
        FileUpdater.completeDownload(getActivity());

        RestaurantManager.invalidate(getActivity());
        RestaurantManager updatedManager = RestaurantManager.getInstance(getActivity());
        List<Restaurant> newRestaurants = updatedManager.getRestaurants();
        List<Restaurant> updatedFavourites = new ArrayList<>();

        for (Restaurant currentFavourite : favourites) {
            for (Restaurant restaurant : newRestaurants) {

                if (restaurant.getResTrackingNumber().equals(currentFavourite.getResTrackingNumber())
                && currentFavourite.getInspections().size() != restaurant.getInspections().size()) {
                        updatedFavourites.add(restaurant);

                }
            }
        }

        return updatedFavourites;
    }

    public int getProgress() {
        return progressBar.getProgress();
    }

    public void setRestaurantsCall(Call<ResponseBody> restaurantsCall) {
        this.restaurantsCall = restaurantsCall;
    }


    public void setInspectionsCall(Call<ResponseBody> inspectionsCall) {
        this.inspectionsCall = inspectionsCall;
    }

    public void setUrlCall(Call<JsonInfo> urlCall) {
        this.urlCall = urlCall;
    }

    public void fail() {
        Intent i = MapActivity.makeLaunchIntent(getContext());
        startActivity(i);
    }
}
