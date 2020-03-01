package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> restaurantNames = new ArrayList<>();
    private ArrayList<String> inspectionDates = new ArrayList<>();
    private ArrayList<Integer> issuesCount = new ArrayList<>();
    private ArrayList<MainActivity.HazardLevel> hazardLevels = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<String> restaurantNames, ArrayList<String> inspectionDates, ArrayList<MainActivity.HazardLevel> hazardLevels) {
        this.restaurantNames = restaurantNames;
        this.inspectionDates = inspectionDates;
        this.hazardLevels = hazardLevels;
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
//        holder.textViewRestaurantName.setText(restaurantNames.get(position));
        holder.setupViewTexts(position);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                Toast.makeText(context, "Hi", Toast.LENGTH_SHORT).show();
                String restaurantName = holder.textViewRestaurantName.getText().toString();
                Intent i = RestaurantDetails.makeLaunchIntent(context, restaurantName);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView hazardIcon;
        private final TextView textViewRestaurantName;
        private final TextView textVievInspectionDate;
        private final TextView textViewHazardLevel;

        private final RelativeLayout parentLayout;
        private final CardView warningBar;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            hazardIcon = itemView.findViewById(R.id.hazardIcon);

            textViewRestaurantName = itemView.findViewById(R.id.textViewRestaurantName);
            textVievInspectionDate = itemView.findViewById(R.id.textViewInspectionDate);
            textViewHazardLevel = itemView.findViewById(R.id.textViewHazardLevel);

            warningBar = itemView.findViewById(R.id.warningBar);
            parentLayout = itemView.findViewById(R.id.recyclerItemParent);
        }

        private void setupViewTexts(int position) {
            MainActivity.HazardLevel hazardLevel = hazardLevels.get(position);

            textViewRestaurantName.setText(restaurantNames.get(position));
            textVievInspectionDate.setText("Last inspected: " + inspectionDates.get(position));
            setupWarningBar(hazardLevel);

        }

        private void setupWarningBar(MainActivity.HazardLevel hazardLevel) {

            switch (hazardLevel){
                case LOW:
                    textViewHazardLevel.setText("Low Hazard");
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardLowDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_low));
                    break;
                case MEDIUM:
                    textViewHazardLevel.setText("Medium Hazard");
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardMediumDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_medium));
                    break;
                case HIGH:
                    textViewHazardLevel.setText("High Hazard");
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardHighDark));
                    hazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_high));
                    break;
                default:
            }
        }
    }
}
