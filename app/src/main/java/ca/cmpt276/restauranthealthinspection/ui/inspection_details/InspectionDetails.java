package ca.cmpt276.restauranthealthinspection.ui.inspection_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ca.cmpt276.restauranthealthinspection.R;

public class InspectionDetails extends AppCompatActivity {

    public static final String INTENT_TAG_RESTAURANT_NAME = "restaurantName";

    public static Intent makeLaunchIntent(Context c, String restaurantName){
        Intent i = new Intent(c,InspectionDetails.class);
        i.putExtra(INTENT_TAG_RESTAURANT_NAME, restaurantName);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);

        Intent i = getIntent();
        String restaurantName = i.getStringExtra(INTENT_TAG_RESTAURANT_NAME);

        TextView textView = findViewById(R.id.textViewInspectionDetails);
        textView.setText(restaurantName);
    }
}
