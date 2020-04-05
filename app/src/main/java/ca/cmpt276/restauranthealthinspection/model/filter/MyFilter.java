package ca.cmpt276.restauranthealthinspection.model.filter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;

/**
 * Singleton class for storing filtered results after searching
 */
/* Filter implementation's reference:
 * https://www.youtube.com/redirect?v=sJ-Z9G0SDhc&event=video_description&q=https%3A%2F%2Fcodinginflow.com%2Ftutorials%2Fandroid%2Fsearchview-recyclerview&redir_token=tVrFuM57AHYwXm4Wz_N6gnbE7v18MTU4NTg4OTc0NEAxNTg1ODAzMzQ0
 */
public class MyFilter {

    private RestaurantManager restaurantManager;
    private List<Restaurant> restaurantList;
    private List<Restaurant> filteredList;
    private CharSequence constraint;
    private String hazardLevel;
    private int critVioNum;

    /*
     * Singleton support
     */
    private static MyFilter instance;

    public static MyFilter getInstance(Context context) {
        if (instance == null) {
            return new MyFilter(context);
        }
        return instance;
    }

    /*
     * Normal code
     */
    private MyFilter(Context context) {
        restaurantManager = RestaurantManager.getInstance(context);
        this.restaurantList = new ArrayList<>(restaurantManager.getRestaurants());
        this.constraint = null;
        this.hazardLevel = null;
        this.critVioNum = 0;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public List<Restaurant> getFilteredList() {
        return filteredList;
    }

    public CharSequence getConstraint() {
        return constraint;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public int getCritVioNum() { return  critVioNum; }


    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public void setConstraint(CharSequence constraint) {
        this.constraint = constraint;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public void setCritVioNum(int critVioNum) {
        this.critVioNum = critVioNum;
    }

    public void performSorting() {
        sortByRestaurantName();
        //sortByHazardLevel();
    }

    public void sortByRestaurantName() {
        filteredList = new ArrayList<>();

        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(restaurantList);
        } else {
            String filterPattern = constraint.toString().toLowerCase().trim();
            for (Restaurant restaurant: restaurantList) {
                if (restaurant.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(restaurant);
                }
            }
        }
    }

    /*public void sortByHazardLevel() {
        List<Restaurant> oldList = filteredList;
        filteredList = new ArrayList<>();

        if (hazardLevel == null || hazardLevel.length() == 0) {
            filteredList.addAll(oldList);
        } else {
            for (Restaurant restaurant: oldList) {
                if (restaurant.getHazardLevel().equals(hazardLevel)) {
                    filteredList.add(restaurant);
                }
            }
        }
    }*/

    /*public void sortByCritVio() {
        if (critVioNum == 0) {
            return;
        }

    }*/
}
