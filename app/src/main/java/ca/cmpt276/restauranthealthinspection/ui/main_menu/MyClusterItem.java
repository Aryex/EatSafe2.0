package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import com.google.android.gms.maps.model.LatLng;

/**
 * Represents the cluster markers on the map activity.
 *
 * Reference:
 * https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering
 */
@SuppressWarnings("WeakerAccess")
public class MyClusterItem implements com.google.maps.android.clustering.ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final String hazardLevel;

    public MyClusterItem(double lat, double lng, String title, String snippet, String hazardLevel) {
        this.position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.hazardLevel = hazardLevel;
    }

    public MyClusterItem(LatLng position, String title, String snippet, String hazardLevel) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.hazardLevel = hazardLevel;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }
}