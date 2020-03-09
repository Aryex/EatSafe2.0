package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static final String TAG = "RecyclerViewAdapter";

    private Context context;
    private RestaurantManager restaurantManager;

    RecyclerViewAdapter(Context context, RestaurantManager restaurantManager) {
        this.restaurantManager = restaurantManager;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_main, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int fPosition = position;
        Restaurant restaurant = restaurantManager.get(position);
        holder.setupViewTexts(restaurant);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingID = restaurant.getResTrackingNumber();
                Intent i = RestaurantDetails.makeLaunchIntent(context, trackingID);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantManager.restaurantCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView hazardIcon = itemView.findViewById(R.id.hazardIcon);
        private final TextView textViewRestaurantName = itemView.findViewById(R.id.recyclerRestaurantName);
        private final TextView textViewInspectionDate = itemView.findViewById(R.id.recyclerInspectionDate);
        private final TextView textViewHazardLevel = itemView.findViewById(R.id.textViewHazardLevel);
        private final TextView textViewAddress = itemView.findViewById(R.id.recyclerAddress);
        private final TextView textViewIssuesCount = itemView.findViewById(R.id.recyclerIssuesCount);

        private final RelativeLayout parentLayout = itemView.findViewById(R.id.recyclerItemParent);
        private final CardView warningBar = itemView.findViewById(R.id.warningBar);

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setupViewTexts(Restaurant restaurant) {

            textViewRestaurantName.setText(restaurant.getName());
            textViewAddress.setText(restaurant.getAddress());

            if (restaurant.hasBeenInspected()) {
                String latestInspectionDate = restaurant.getLatestInspectionDate();
                textViewInspectionDate.setText(String.format(context.getString(R.string.last_inspected_on), latestInspectionDate));

                String latestInspectionTotalIssues = restaurant.getLatestInspectionTotalIssues();
                textViewIssuesCount.setText(String.format(context.getString(R.string.found_num_issues), latestInspectionTotalIssues));

                MainActivity.HazardLevel hazardLevel = HazardLevelConverter(restaurant.getHazardLevel());
                setupWarningBar(hazardLevel);
            } else {
                textViewInspectionDate.setText(R.string.no_inspections_found);
                textViewIssuesCount.setText(R.string.empty_string);
                setupWarningBar(MainActivity.HazardLevel.LOW);
            }

        }

        private void setupWarningBar(MainActivity.HazardLevel hazardLevel) {

            switch (hazardLevel) {
                case LOW:
                    textViewHazardLevel.setText(R.string.hazard_level_low);
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardLowDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_low));
                    break;
                case MEDIUM:
                    textViewHazardLevel.setText(R.string.hazard_level_medium);
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardMediumDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_medium));
                    break;
                case HIGH:
                    textViewHazardLevel.setText(R.string.hazard_level_high);
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardHighDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_high));
                    break;
                default:
            }
        }
    }

    private MainActivity.HazardLevel HazardLevelConverter(String hazardLevel) {
        switch (hazardLevel.toLowerCase()) {
            case "low":
                return MainActivity.HazardLevel.LOW;
            case "moderate":
                return MainActivity.HazardLevel.MEDIUM;
            default:
                return MainActivity.HazardLevel.HIGH;
        }
    }
}
