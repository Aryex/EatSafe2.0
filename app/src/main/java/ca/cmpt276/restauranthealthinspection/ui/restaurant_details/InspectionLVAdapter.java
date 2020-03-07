package ca.cmpt276.restauranthealthinspection.ui.restaurant_details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.MainActivity;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;

public class InspectionLVAdapter extends ArrayAdapter<Inspection> {
    private List<Inspection> inspectionList;

    public InspectionLVAdapter(Context context, List<Inspection> list) {
        super(context, R.layout.inspection_item_view, list);
        inspectionList = list;
    }

    public int getIconID(String hazardLevel) {
        switch (hazardLevel) {
            case "LOW":
                return R.drawable.icon_hazard_low;
            case "MEDIUM":
                return R.drawable.icon_hazard_medium;
            case "HIGH":
                return R.drawable.icon_hazard_high;
            default:
        }
        return R.drawable.icon_hazard_low;
    }

    public String getLevelString(MainActivity.HazardLevel hazardLevel) {
        switch (hazardLevel) {
            case LOW:
                return "LOW";
            case MEDIUM:
                return "MEDIUM";
            case HIGH:
                return "HIGH";
            default:
        }
        return "LOW";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Make sure we have a view to work with (may have been given null)
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspection_item_view, parent, false);
        }

        Inspection currentInspection = inspectionList.get(position);

        // Fill the view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
        imageView.setImageResource(getIconID(currentInspection.getHazardRating()));

        // Hazard level:
        TextView levelText = (TextView) itemView.findViewById(R.id.levelTV);
        levelText.setText("- Hazard level: " + currentInspection.getHazardRating());

        // Critical issues found:
        TextView critText = (TextView) itemView.findViewById(R.id.item_crit);
        critText.setText("- Critical issues found: " + currentInspection.getNumCritical());

        // Non-critical issues found:
        TextView nonCritText = (TextView) itemView.findViewById(R.id.item_noncrit);
        nonCritText.setText("- Non-critical issues found: " + currentInspection.getNumNonCritical());

        // Date:
        TextView dateText = (TextView) itemView.findViewById(R.id.item_date);
        dateText.setText("- Inspection date: " + currentInspection.getFromCurrentDate());

        return itemView;
    }
}