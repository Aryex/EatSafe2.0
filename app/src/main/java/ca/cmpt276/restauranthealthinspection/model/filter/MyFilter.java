package ca.cmpt276.restauranthealthinspection.model.filter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;

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
    private Context context;

    private static final String NAME_SEARCH = "Name Search";
    private static final String HAZARD_LEVEL = "Hazard level";
    private static final String CRIT_VIO_NUM = "Number of critical violations";
    private static final String FAVOURITE_FLAG = "Favourite Flag";
    private static final String LESS_THAN_FLAG = "Less-than Flag";
    private static final String CLEAR_ALL = "Clear all";

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
        this.context = context;
    }

    public List<Restaurant> getFilteredList() {
        return filteredList;
    }

    public void setFilteredList(List<Restaurant> restaurantList) {
        filteredList = restaurantList;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void performSorting() {
        if (getClearAllPref(context)) {
            filteredList = new ArrayList<>();
            filteredList.addAll(restaurantList);
            setClearAllPref(false);
        } else {
            sortByRestaurantName();
            sortByHazardLevel();
            sortByCritVio();
            sortByFavorite();
        }
    }

    private void sortByRestaurantName() {
        filteredList = new ArrayList<>();

        String constraint = getNamePref(context);
        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(restaurantList);
        } else {
            String filterPattern = constraint.toLowerCase().trim();
            for (Restaurant restaurant : restaurantList) {
                if (restaurant.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(restaurant);
                }
            }
        }
    }

    private void sortByHazardLevel() {
        String hazardLevel = getHazardLevelPref(context);

        if (hazardLevel != null && hazardLevel.length() != 0
                && !hazardLevel.equals(context.getResources().getString(R.string.hazard_rating_all))) {
            List<Restaurant> oldList = filteredList;
            filteredList = new ArrayList<>();

            for (Restaurant restaurant : oldList) {
                if (restaurant.getHazardLevel(context).equals(hazardLevel)) {
                    filteredList.add(restaurant);
                }
            }
        }
    }

    private void sortByCritVio() {
        int critVioNum = getVioNumPref(context);

        List<Restaurant> oldList = filteredList;
        filteredList = new ArrayList<>();
        if (getLessThanPref(context)) {
            for (Restaurant restaurant : oldList) {
                if (restaurant.getCritVioLastYear() <= critVioNum) {
                    filteredList.add(restaurant);
                }
            }
        } else {
            for (Restaurant restaurant : oldList) {
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

            for (Restaurant restaurant : oldList) {
                if (restaurant.isFavourite()) {
                    filteredList.add(restaurant);
                }
            }
        }
    }

    private void setNamePref(String searchName) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SEARCH, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(NAME_SEARCH, searchName);
        editor.apply();
    }

    public static String getNamePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SEARCH, MODE_PRIVATE);
        String defaultVal = context.getResources().getString(R.string.empty_string);
        return pref.getString(NAME_SEARCH, defaultVal);
    }

    private void setHazardLevelPref(String hazardLevel) {
        SharedPreferences pref = context.getSharedPreferences(HAZARD_LEVEL, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(HAZARD_LEVEL, hazardLevel);
        editor.apply();
    }

    public static String getHazardLevelPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(HAZARD_LEVEL, MODE_PRIVATE);
        String defaultVal = context.getResources().getString(R.string.hazard_rating_all);
        return pref.getString(HAZARD_LEVEL, defaultVal);
    }

    private void setVioNumPref(int vioNum) {
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

    private void setFavoritePref(boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(FAVOURITE_FLAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FAVOURITE_FLAG, flag);
        editor.apply();
    }

    public static boolean getFavoritePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(FAVOURITE_FLAG, MODE_PRIVATE);
        return pref.getBoolean(FAVOURITE_FLAG, false);
    }

    private void setLessThanPref(boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(LESS_THAN_FLAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(LESS_THAN_FLAG, flag);
        editor.apply();
    }

    public static boolean getLessThanPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(LESS_THAN_FLAG, MODE_PRIVATE);
        return pref.getBoolean(LESS_THAN_FLAG, false);
    }

    public void setClearAllPref(boolean flag) {
        SharedPreferences pref = context.getSharedPreferences(CLEAR_ALL, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(CLEAR_ALL, flag);
        editor.apply();
    }

    private static boolean getClearAllPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CLEAR_ALL, MODE_PRIVATE);
        return pref.getBoolean(CLEAR_ALL, false);
    }

    public void resetAllFilterOptions(String searchName, String hazardLevel, int vioNum,
                                      boolean keepFavorite, boolean isLessThan, boolean clearAll) {
        setNamePref(searchName);
        setHazardLevelPref(hazardLevel);
        setVioNumPref(vioNum);
        setFavoritePref(keepFavorite);
        setLessThanPref(isLessThan);
        setClearAllPref(clearAll);
    }
}
