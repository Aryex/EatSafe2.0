package ca.cmpt276.restauranthealthinspection.ui.inspection_details;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.model.Violation;

/**
 * Adapter for the list of violations
 */
public class RecyclerViolationAdapter extends RecyclerView.Adapter<RecyclerViolationAdapter.ViolationViewHolder> {

    private Context context;
    private Inspection inspection;

    RecyclerViolationAdapter(Context context, Inspection inspection) {
        this.context = context;
        this.inspection = inspection;
    }

    @NonNull
    @Override
    public ViolationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_violation, parent, false);

        //find views and pass into holder
        TextView violationDescription = view.findViewById(R.id.violationDescription);
        ImageView violationIcon = view.findViewById(R.id.violationIcon);
        ConstraintLayout layout = view.findViewById(R.id.recyclerViolation);

        return new ViolationViewHolder(view, violationDescription, violationIcon, layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationViewHolder holder, int position) {
        if (position == 0) {
            holder.setHazardLevel(inspection.getHazardRating(context));
        } else {
            Violation violation = inspection.get(position - 1);
            holder.setViews(violation);
        }
    }

    @Override
    public int getItemCount() {
        return inspection.getViolations().size() + 1;
    }

    class ViolationViewHolder extends RecyclerView.ViewHolder {

        private TextView description;
        private ImageView icon;
        private ConstraintLayout layout;

        ViolationViewHolder(@NonNull View itemView, TextView description, ImageView icon, ConstraintLayout layout) {
            super(itemView);
            this.description = description;
            this.icon = icon;
            this.layout = layout;
        }

        private void setViews(Violation violation) {

            //get violation type and set image and description accordingly
            String codeCategory = "code_" + (violation.getNumber() / 100) * 100;

            int stringId = itemView
                    .getResources()
                    .getIdentifier(codeCategory, "string", itemView.getContext().getPackageName());

            int imageId = itemView
                    .getResources()
                    .getIdentifier(codeCategory, "drawable", itemView.getContext().getPackageName());

            description.setText(stringId);
            icon.setImageResource(imageId);

            //change background colour
            switch (violation.getCriticalStatus()) {
                case Violation.NON_CRITICAL_STATUS:
                    layout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.hazardMediumInspection));
                    break;
                case Violation.CRITICAL_STATUS:
                    layout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.hazardHighInspection));
                    description.setTypeface(ResourcesCompat.getFont(layout.getContext(), R.font.roboto_light), Typeface.BOLD);
                    break;
            }

            //set onclick listener for each item on the list
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(),
                            itemView.getContext().getString(R.string.violation_detail, violation.getNumber(), violation.getDescription()),
                            Toast.LENGTH_LONG).show();
                }
            });

        }

        void setHazardLevel(String hazardRating) {
            //set up hazard level depending on the hazard rating

            description.setText(layout.getContext().getString(R.string.hazardLevelDisplay, hazardRating));
            description.setTypeface(ResourcesCompat.getFont(layout.getContext(), R.font.roboto_light), Typeface.BOLD);
            layout.setForeground(null);

            String hazardLevel = inspection.getHazardRating(context);

            if (hazardLevel.equals(context.getString(R.string.hazard_rating_low))) {
                icon.setImageResource(R.drawable.icon_hazard_low);
                layout.setBackgroundColor(ContextCompat.getColor(layout.getContext(), R.color.hazardLowInspection));
            } else if (hazardLevel.equals(context.getString(R.string.hazard_rating_medium))) {
                icon.setImageResource(R.drawable.icon_hazard_medium);
                layout.setBackgroundColor(ContextCompat.getColor(layout.getContext(), R.color.hazardMediumInspection));
            } else if (hazardLevel.equals(context.getString(R.string.hazard_rating_high))) {
                icon.setImageResource(R.drawable.icon_hazard_high);
                layout.setBackgroundColor(ContextCompat.getColor(layout.getContext(), R.color.hazardHighInspection));
            }
        }
    }
}
