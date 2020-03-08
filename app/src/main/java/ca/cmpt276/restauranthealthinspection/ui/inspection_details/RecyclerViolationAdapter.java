package ca.cmpt276.restauranthealthinspection.ui.inspection_details;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Violation;

public class RecyclerViolationAdapter extends RecyclerView.Adapter  {

    private LayoutInflater layoutInflater;
    private List<Violation> violations;

    RecyclerViolationAdapter(Context context, List<Violation> violations) {
        this.layoutInflater = LayoutInflater.from(context);
        this.violations = violations;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_violation, parent, false);
        return new ViolationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Violation violation = violations.get(position);
        ((ViolationViewHolder) holder).setViews(violation);
    }

    @Override
    public int getItemCount() {
        return violations.size();
    }
}

class ViolationViewHolder extends RecyclerView.ViewHolder {

    private TextView violationDescription;
    private ImageView violationIcon;
    private ConstraintLayout layout;

    ViolationViewHolder(@NonNull View itemView) {
        super(itemView);
        violationDescription = itemView.findViewById(R.id.violationDescription);
        violationIcon = itemView.findViewById(R.id.violationIcon);
        layout = itemView.findViewById(R.id.recyclerViolation);
    }

    void setViews(Violation violation) {
        violationDescription.setText(violation.getDescription());

        switch (violation.getCriticalStatus()) {
            case Violation.NON_CRITICAL_STATUS:
                layout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.hazardMediumInspection));
                break;
            case Violation.CRITICAL_STATUS:
                layout.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.hazardHighInspection));
                break;
        }

    }


}
