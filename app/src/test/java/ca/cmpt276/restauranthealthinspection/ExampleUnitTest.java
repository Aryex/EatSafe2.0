package ca.cmpt276.restauranthealthinspection;

import android.renderscript.ScriptGroup;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void checkRestaurantStructure() throws FileNotFoundException {

        File inspectionData = new File("\\L:\\\\276-Vanadium\\\\app\\\\src\\\\main\\\\res\\\\raw\\\\inspectionreports_itr1.csv");
        File restaurantData = new File("\\L:\\\\276-Vanadium\\\\app\\\\src\\\\main\\\\res\\\\raw\\\\restaurants_itr1.csv");

        InputStreamReader inspectionDataReader = new InputStreamReader(new FileInputStream(inspectionData));
        InputStreamReader restaurantDataReader = new InputStreamReader(new FileInputStream(restaurantData));

        RestaurantManager restaurants = RestaurantManager.getInstance(restaurantDataReader, inspectionDataReader);

        assertEquals(4, 2 + 2);
    }
}