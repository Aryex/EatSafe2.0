package ca.cmpt276.restauranthealthinspection.ui.inspection_details;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Violation;

import static ca.cmpt276.restauranthealthinspection.ui.main_menu.RecyclerViewAdapter.TAG;

public class RecyclerViolationAdapter extends RecyclerView.Adapter<RecyclerViolationAdapter.ViolationViewHolder> {

    private Context context;
    private List<Violation> violations;

    RecyclerViolationAdapter(Context context, List<Violation> violations) {
        this.context = context;
        this.violations = violations;
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
        Log.d(TAG, "onBindViewHolder position: " + position);
        Violation violation = violations.get(position);
        holder.setViews(violation);
    }

    @Override
    public int getItemCount() {
        return violations.size();
    }

    public class ViolationViewHolder extends RecyclerView.ViewHolder {

        private TextView violationDescription;
        private ImageView violationIcon;
        private ConstraintLayout layout;

        ViolationViewHolder(@NonNull View itemView, TextView violationDescription, ImageView violationIcon, ConstraintLayout layout) {
            super(itemView);
            this.violationDescription = violationDescription;
            this.violationIcon = violationIcon;
            this.layout = layout;
        }

        private void setViews(Violation violation) {

                //get violation type and set image and description accordingly
                String codeCategory = "code_" + (violation.getNumber() / 100) * 100;
                int stringId = itemView.getResources().getIdentifier(codeCategory, "string", itemView.getContext().getPackageName());
                int imageId = itemView.getResources().getIdentifier(codeCategory, "drawable", itemView.getContext().getPackageName());
                violationDescription.setText(stringId);
                violationIcon.setImageResource(imageId);

                //change background colour
                switch (violation.getCriticalStatus()) {
                    case Violation.NON_CRITICAL_STATUS:
                        layout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.hazardMediumInspection));
                        break;
                    case Violation.CRITICAL_STATUS:
                        layout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.hazardHighInspection));
                        violationDescription.setTypeface(null, Typeface.BOLD);
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

    }

}

