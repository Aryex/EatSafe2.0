package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

class MyDefaultRenderer extends DefaultClusterRenderer<MyClusterItem> {
    public MyDefaultRenderer(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        String hazardLevel = item.getHazardLevel();

        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
        markerOptions.icon(MapActivity.getBitmapDescriptor(hazardLevel));
    }
}
