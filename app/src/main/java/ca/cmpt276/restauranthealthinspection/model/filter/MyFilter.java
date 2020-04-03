package ca.cmpt276.restauranthealthinspection.model.filter;

import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.Restaurant;

/**
 * Singleton class for storing filtered results after searching
 */
/* Filter implementation's reference:
 * https://www.youtube.com/redirect?v=sJ-Z9G0SDhc&event=video_description&q=https%3A%2F%2Fcodinginflow.com%2Ftutorials%2Fandroid%2Fsearchview-recyclerview&redir_token=tVrFuM57AHYwXm4Wz_N6gnbE7v18MTU4NTg4OTc0NEAxNTg1ODAzMzQ0
 */
public class MyFilter {

    private List<Restaurant> restaurantList;
    private CharSequence constraint;

    /*
     * Singleton support
     */
    private static MyFilter instance;

    public static MyFilter getInstance() {
        if (instance == null) {
            return new MyFilter();
        }
        return instance;
    }

    /*
     * Normal code
     */
    private MyFilter() {
        this.restaurantList = null;
        this.constraint = "";
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public CharSequence getConstraint() {
        return constraint;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public void setConstraint(CharSequence constraint) {
        this.constraint = constraint;
    }
}
