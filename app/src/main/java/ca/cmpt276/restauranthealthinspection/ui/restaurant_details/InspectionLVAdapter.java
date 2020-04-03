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

/**
 * Adapter for Inspection ListView
 */
public class InspectionLVAdapter extends ArrayAdapter<Inspection> {
    private List<Inspection> inspectionList;
    private Context context;

    InspectionLVAdapter(Context context, List<Inspection> list) {
        super(context, R.layout.inspection_item_view, list);
        this.inspectionList = list;
        this.context = context;
    }

    private int getIconID(String hazardLevel) {
        switch (hazardLevel) {
            case "Low":
                return R.drawable.icon_hazard_low;
            case "Moderate":
                return R.drawable.icon_hazard_medium;
            case "High":
                return R.drawable.icon_hazard_high;
            default:
        }
        return R.drawable.icon_hazard_low;
    }

    private int getBackgroundColor(String hazardLevel) {
        switch (hazardLevel) {
            case "Low":
                return context.getResources().getColor(R.color.hazardLowInspection);
            case "Moderate":
                return context.getResources().getColor(R.color.hazardMediumInspection);
            case "High":
                return context.getResources().getColor(R.color.hazardHighInspection);
            default:
        }
        return context.getResources().getColor(R.color.hazardLowInspection);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Make sure we have a view to work with (may have been given null)
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspection_item_view, parent, false);
        }

        Inspection currentInspection = inspectionList.get(position);
        itemView.setBackgroundColor(getBackgroundColor(currentInspection.getHazardRating(context)));

        // Fill the view
        ImageView imageView = itemView.findViewById(R.id.item_icon);
        imageView.setImageResource(getIconID(currentInspection.getHazardRating(context)));

        // Hazard level:
        TextView levelText = itemView.findViewById(R.id.levelTV);
        levelText.setText(context.getString(R.string.hazard_level, currentInspection.getHazardRating(context)));

        // Critical issues found:
        TextView critText = itemView.findViewById(R.id.item_crit);
        critText.setText(context.getString(R.string.crit_issues, currentInspection.getNumCritical()));

        // Non-critical issues found:
        TextView nonCritText = itemView.findViewById(R.id.item_noncrit);
        nonCritText.setText(context.getString(R.string.non_crit_issues, currentInspection.getNumNonCritical()));

        // Date:
        TextView dateText = itemView.findViewById(R.id.item_date);
        dateText.setText(context.getString(R.string.date, currentInspection.getFromCurrentDate(getContext())));

        return itemView;
    }
}
