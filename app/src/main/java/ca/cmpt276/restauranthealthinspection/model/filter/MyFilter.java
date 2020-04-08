package ca.cmpt276.restauranthealthinspection.model.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.RestaurantListActivity;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.FilterOptionDialog;

import static android.content.Context.MODE_PRIVATE;

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

    private static final String NAME_SEARCH = "Name Search";
    private static final String HAZARD_LEVEL = "Hazard level";
    private static final String CRIT_VIO_NUM = "Number of critical violations";
    private static final String FAVORITE_FLAG = "Favorite Flag";

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
        sortByFavorite();
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
        if (hazardLevel != null && hazardLevel.length() != 0 && !hazardLevel.equals("All")) {
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

    private void sortByFavorite() {
        if (getFavoritePref(context)) {
            List<Restaurant> oldList = filteredList;
            filteredList = new ArrayList<>();

            for (Restaurant restaurant: oldList) {
                if (restaurant.isFavourite()) {
                    filteredList.add(restaurant);
                }
            }
        }
    }

    public void setNamePref(String searchName) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SEARCH, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(NAME_SEARCH, searchName);
        editor.apply();
    }
    public static String getNamePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SEARCH, MODE_PRIVATE);
        String defaultVal = "";
        return pref.getString(NAME_SEARCH, defaultVal);
    }

    public void setHazardLevelPref(String hazardLevel) {
        SharedPreferences pref = context.getSharedPreferences(HAZARD_LEVEL, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(HAZARD_LEVEL, hazardLevel);
        editor.apply();
    }
    public static String getHazardLevelPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(HAZARD_LEVEL, MODE_PRIVATE);
        String defaultVal = "";
        return pref.getString(HAZARD_LEVEL, defaultVal);
    }

    public void setVioNumPref(int vioNum) {
        SharedPreferences pref = context.getSharedPreferences(CRIT_VIO_NUM, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(CRIT_VIO_NUM, vioNum);
        editor.apply();
    }
    public static int getVioNumPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CRIT_VIO_NUM, MODE_PRIVATE);
        int defaultVal = 0;
        return pref.getInt(CRIT_VIO_NUM, defaultVal);
    }

    public void setFavoritePref(boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(FAVORITE_FLAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FAVORITE_FLAG, flag);
        editor.apply();
    }
    public static boolean getFavoritePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(FAVORITE_FLAG, MODE_PRIVATE);
        boolean defaultVal = false;
        return pref.getBoolean(FAVORITE_FLAG, defaultVal);
    }
}
