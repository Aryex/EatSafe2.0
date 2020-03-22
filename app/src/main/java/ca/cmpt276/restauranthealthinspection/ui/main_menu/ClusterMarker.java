package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import com.google.android.gms.maps.model.LatLng;

/**
 * Reference:
 * https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering
 */
public class ClusterMarker implements com.google.maps.android.clustering.ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;

    public ClusterMarker(double lat, double lng, String title, String snippet) {
        this.position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    public ClusterMarker(LatLng position, String title, String snippet) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
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

}