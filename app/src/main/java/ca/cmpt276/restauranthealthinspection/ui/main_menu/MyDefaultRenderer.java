package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

class MyDefaultRenderer extends DefaultClusterRenderer<MyClusterItem> {
    private Context context;

    public MyDefaultRenderer(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        String hazardLevel = item.getHazardLevel();

        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
        markerOptions.icon(MapActivity.getHazardLevelBitmapDescriptor(hazardLevel, context));
    }
}
