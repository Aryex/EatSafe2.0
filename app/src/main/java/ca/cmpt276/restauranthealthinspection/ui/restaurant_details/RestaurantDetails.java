package ca.cmpt276.restauranthealthinspection.ui.restaurant_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.restauranthealthinspection.R;

public class RestaurantDetails extends AppCompatActivity {

    public static final String INTENT_TAG_RESTAURANT_ID = "restaurantID";

    public static Intent makeLaunchIntent(Context c, String restaurantID){
        Intent i = new Intent(c,RestaurantDetails.class);
        i.putExtra(INTENT_TAG_RESTAURANT_ID, restaurantID);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
    }
}
