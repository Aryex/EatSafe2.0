package ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MapActivity;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.RecyclerViewAdapter;


/**
 * Represents the fragment for displaying the updated inspections
 * for the user's favourite restaurants.
 */
public class FavouriteNotificationFragment extends DialogFragment {
    public static final String TAG = "notify favourites";
    private List<Restaurant> updatedRestaurants;

    FavouriteNotificationFragment(List<Restaurant> updatedRestaurants) {
        this.updatedRestaurants = updatedRestaurants;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.favourite_notification_fragment, null);
        setCancelable(false);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFragmentManager().beginTransaction().remove(FavouriteNotificationFragment.this).commit();
                Intent i = MapActivity.makeLaunchIntent(getContext());
                startActivity(i);
            }
        };

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFavourites);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), updatedRestaurants);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setTitle(R.string.new_inspections_for_your_favourite_restaurants)
                .create();
    }
}
