package ca.cmpt276.restauranthealthinspection.ui.restaurant_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.ui.inspection_details.InspectionDetails;

public class RestaurantDetails extends AppCompatActivity {

    public static final String INTENT_TAG_RESTAURANT_NAME = "restaurantName";

    public static Intent makeLaunchIntent(Context c, String restaurantName){
        Intent i = new Intent(c,RestaurantDetails.class);
        i.putExtra(INTENT_TAG_RESTAURANT_NAME, restaurantName);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        Intent i = getIntent();
        String restaurantName = i.getStringExtra(INTENT_TAG_RESTAURANT_NAME);

        TextView textView = findViewById(R.id.textViewRestaurantDetail);
        textView.setText(restaurantName);

        Button b = findViewById(R.id.buttonRestaurantDetails);

        final Context c = this;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(InspectionDetails.makeLaunchIntent(c, restaurantName));
            }
        });
    }
}
