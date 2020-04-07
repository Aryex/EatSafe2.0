package ca.cmpt276.restauranthealthinspection.model.filter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.RestaurantListActivity;

/**
 * Singleton class for storing filtered results after searching
 */
/* Filter implementation's reference:
 * https://www.youtube.com/redirect?v=sJ-Z9G0SDhc&event=video_description&q=https%3A%2F%2Fcodinginflow.com%2Ftutorials%2Fandroid%2Fsearchview-recyclerview&redir_token=tVrFuM57AHYwXm4Wz_N6gnbE7v18MTU4NTg4OTc0NEAxNTg1ODAzMzQ0
 */
public class MyFilter {

    private List<Restaurant> restaurantList;
    private List<Restaurant> filteredList;
    private String constraint;
    private String hazardLevel;
    private int critVioNum;
    private Context context;

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
        RestaurantManager restaurantManager = RestaurantManager.getInstance(context);
        this.restaurantList = new ArrayList<>(restaurantManager.getRestaurants());
        this.filteredList = new ArrayList<>(restaurantManager.getRestaurants());
        this.constraint = "";
        this.hazardLevel = "";
        this.critVioNum = 0;
        this.context = context;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public List<Restaurant> getFilteredList() {
        return filteredList;
    }

    public String getConstraint() {
        return constraint;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public int getCritVioNum() { return critVioNum; }


    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public void setConstraint(CharSequence constraint) {
        this.constraint = constraint.toString();
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public void setCritVioNum(int critVioNum) {
        this.critVioNum = critVioNum;
    }

    public void performSorting() {
        sortByRestaurantName();
        sortByHazardLevel();
        sortByCritVio();
    }

    private void sortByRestaurantName() {
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

    private void sortByHazardLevel() {
        if (hazardLevel != null && hazardLevel.length() != 0) {
            List<Restaurant> oldList = filteredList;
            filteredList = new ArrayList<>();

            for (Restaurant restaurant: oldList) {
                if (restaurant.getHazardLevel(context).equals(hazardLevel)) {
                    filteredList.add(restaurant);
                }
            }
        }
    }

    private void sortByCritVio() {
        if (critVioNum != 0) {
            List<Restaurant> oldList = filteredList;
            filteredList = new ArrayList<>();

            for (Restaurant restaurant: oldList) {
                if (restaurant.getCritVioLastYear() >= critVioNum) {
                    filteredList.add(restaurant);
                }
            }
        }
    }
}
