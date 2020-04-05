package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.FilterFragment;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;

/**
 * Map activity serves as the first entry point into the app.
 * The map uses Google Map API.
 * Map activity uses the Fused Location API to to track user location.
 * <p>
 * Location tracking Codes were adapted from the following resources:
 * https://developer.android.com/training/location
 * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
 * <p>
 * Cluster items codes were adapted from the following resources:
 * https://stackoverflow.com/questions/25968486/how-to-add-info-window-for-clustering-marker-in-android
 * https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering
 * <p>
 * note: restaurant tracking id is stored in a cluster marker's snippet.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //  Surrey Central's Lat Lng
    public static final LatLng DEFAULT_LAT_LNG = new LatLng(49.1866939, -122.8494363);

    //Debug
    private static final String TAG = "MapsActivity";
    private TextView debugTextView;
    private boolean debugOn = false;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private RestaurantManager restaurants;

    private static final float DEFAULT_ZOOM_FOCUS = 19f;
    private static final float DEFAULT_ZOOM_HIGH = 12f;
    private static final double DEFAULT_PRECISION = 0.0001;
    private float currentCameraZoom = DEFAULT_ZOOM_FOCUS;
    private boolean cameraLocked = true;

    private static GoogleMap map;
    private static ClusterManager<MyClusterItem> clusterManager;
    private static ArrayList<MyClusterItem> myClusterItemList;
    private static MyClusterItem clickedClusterItem;
    private static Marker requestedMarker;
    private static boolean clusterItemsRendered = false;

    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LatLng deviceLocation = DEFAULT_LAT_LNG;// SFU Surrey;


    private static String INTENT_KEY_LAT = "lat";
    private static String INTENT_KEY_LNG = "lng";
    private static String INTENT_KEY_RESTAURANT_ID = "id";

    public static Intent makeLaunchIntent(Context context, double lat, double lng, String trackingId) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(INTENT_KEY_LAT, lat);
        intent.putExtra(INTENT_KEY_LNG, lng);
        intent.putExtra(INTENT_KEY_RESTAURANT_ID, trackingId);
        return intent;
    }

    public static BitmapDescriptor getHazardLevelBitmapDescriptor(String hazardlevel) {
        switch (hazardlevel.toLowerCase()) {
            case "moderate":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            case "high":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            default:
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }
    }

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, MapActivity.class);
    }

    // What to do when map appear on screen
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map loaded");

        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        Intent data = getIntent();
        if (data.hasExtra(INTENT_KEY_RESTAURANT_ID)) {
            //initialize camera position above default position.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LAT_LNG, DEFAULT_ZOOM_HIGH));
            createRequestedMarker(data);
        } else {
            populateMap();
            setupLocationTracking();
            moveCamera(deviceLocation, DEFAULT_ZOOM_HIGH);
        }
    }

    private void setupLocationTracking() {
        if (locationPermissionGranted) {
            getLastKnownLocation();
            map.setMyLocationEnabled(true);
            map.setOnCameraMoveListener(new OnCameraMove());
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Log.d(TAG, "onMyLocationButtonClick: camera locked");
                    currentCameraZoom = map.getCameraPosition().zoom;
                    //Note: smaller zoom == higher camera
                    if (currentCameraZoom != DEFAULT_ZOOM_FOCUS) {
                        currentCameraZoom = DEFAULT_ZOOM_FOCUS;
                    }
                    moveCamera(deviceLocation, DEFAULT_ZOOM_FOCUS);
                    cameraLocked = true;
                    return true;
                }
            });
//          When btn pressed, move camera to user location and zoom
        }
    }

    private void createRequestedMarker(Intent data) {
        LatLng latLng;
        Log.d(TAG, "onMapReady: THERE IS INTENT");
        cameraLocked = false;
        clusterItemsRendered = false;

        latLng = new LatLng(data.getDoubleExtra(INTENT_KEY_LAT, 0), data.getDoubleExtra(INTENT_KEY_LNG, 0));
        String trackingId = data.getStringExtra(INTENT_KEY_RESTAURANT_ID);

        String hazardLevel = restaurants.getRestaurant(trackingId).getHazardLevel(getApplicationContext());
        BitmapDescriptor markerIcon = getHazardLevelBitmapDescriptor(hazardLevel);

        requestedMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Title")
                .snippet(trackingId)
                .icon(markerIcon));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String trackingID = marker.getSnippet();
                Intent startIntent = RestaurantDetails.makeLaunchIntent(MapActivity.this, trackingID);
                startActivity(startIntent);
            }
        });

        map.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                if (!clusterItemsRendered) {
                    marker.remove();
                    //renderer will update on move.
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, map.getCameraPosition().zoom));
                    populateMap();
                    setupLocationTracking();
                }
            }
        });
        map.setInfoWindowAdapter(new InfoWindowAdapter(this));
        requestedMarker.showInfoWindow();
        moveCamera(latLng, currentCameraZoom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDebug(debugOn);

        restaurants = RestaurantManager.getInstance(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationRequest();

        getLocationPermission();
    }

    private void setupLocationRequest() {
        locationRequest = LocationRequest.create();
        this.locationRequest.setInterval(100);
        this.locationRequest.setFastestInterval(100);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(this.locationRequest);
    }

    private void setupDebug(boolean debugOn) {
        debugTextView = findViewById(R.id.debugTextview);
        Button button = findViewById(R.id.surreyButton);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        if (!debugOn) {
            button.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.GONE);
            debugTextView.setVisibility(View.GONE);
            return;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCamera(new LatLng(49.188808, -122.847992), 12f);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraLocked = !cameraLocked;
                moveCamera(deviceLocation, currentCameraZoom);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeLocationCallback();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop updating location.
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void populateMap() {
        clusterItemsRendered = true;
        clusterManager = new ClusterManager<MyClusterItem>(this, map);
        clusterManager.setRenderer(new MyDefaultRenderer(this, map, clusterManager));
        myClusterItemList = new ArrayList<>();

        setupClusterMarkers();

        clusterManager.setOnClusterItemClickListener(item -> {
            clickedClusterItem = new MyClusterItem(item.getPosition(), item.getTitle(), item.getSnippet(), item.getHazardLevel());
            return false;
        });

        clusterManager.getMarkerCollection().setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) marker -> {
            String trackingID = (String) clickedClusterItem.getSnippet();
            Intent startIntent = RestaurantDetails.makeLaunchIntent(MapActivity.this, trackingID);
            startActivity(startIntent);
        });

        //set clusterManager's infoWindowAdapter
        clusterManager.getMarkerCollection().setInfoWindowAdapter(new InfoWindowAdapter(this));

//      Map will use markerCluster's implementations.
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
    }

    private boolean approximateEqual(double value1, double value2, double precision) {
        double valueDiff = value1 - value2;
        return (valueDiff > -precision && valueDiff < precision);
    }

    /**
     * Adapted from:
     * https://developer.android.com/training/location/request-updates
     */
    private void makeLocationCallback() {
        Log.d(TAG, "setupLocationCallBack: called");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "onLocationResult: NULL");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
//                    Log.d(TAG, "onLocationResult: called + cameraLocked: " + cameraLocked);

                    if (cameraLocked) {
                        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        double latPrecision = deviceLocation.latitude - location.getLatitude();
                        double lngPrecision = deviceLocation.longitude - location.getLongitude();
                        if (!deviceLocation.equals(newLocation)) {
                            deviceLocation = newLocation;
                            moveCamera(newLocation, currentCameraZoom);
                        }
                    }
                }
            }
        };
    }

    /**
     * Reference:
     * https://developer.android.com/training/location
     * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
     */
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: Called");
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got the last known location which may be null
                        if (location != null) {
                            LatLng lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            deviceLocation = lastKnownLocation;
                        } else {
                            Log.d(TAG, "getLastKnownLocation: current location is null");
                        }
                    }
                });
    }

    /**
     * Reference:
     * https://developer.android.com/training/location
     * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: locationPermission granted");
                locationPermissionGranted = true;
                createMap();
            } else {
                //If we do not have location permission, request one.
                //override onRequestPermissionsResult() to check.
                Log.d(TAG, "getLocationPermission: locationPermission not granted");
                //Android will create the dialog to ask for permission
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            //If we do not have location permission, request one.
            //override onRequestPermissionsResult() to check.
            Log.d(TAG, "getLocationPermission: locationPermission not granted");
            //Android will create the dialog to ask for permission
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Reference:
     * https://developer.android.com/training/location
     * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called");
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: permission failed");
                        locationPermissionGranted = false;
                        createMap();
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: permission granted");
                locationPermissionGranted = true;
                createMap();
            }
        }
    }

    /**
     * Reference:
     * https://developer.android.com/training/location
     * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
     */
    private void createMap() {
        Log.d(TAG, "createMap: creating map");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MapActivity.this);
    }

    private void setupClusterMarkers() {
        for (Restaurant restaurant : restaurants) {

            double lat = restaurant.getLatitude();
            double lng = restaurant.getLongitude();
            String name = restaurant.getName();
            String snippet = restaurant.getResTrackingNumber();
            String hazardLevel = restaurant.getHazardLevel(getApplicationContext());

            MyClusterItem myClusterItem = new MyClusterItem(lat, lng, name, snippet, hazardLevel);
            myClusterItemList.add(myClusterItem);
            clusterManager.addItem(myClusterItem);
        }
    }

    private static void moveCamera(LatLng newLocation, float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoom));
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void debugDisplay(String s) {
        debugTextView.setText(s);
    }

    public class OnCameraMove implements GoogleMap.OnCameraMoveListener {
        @Override
        public void onCameraMove() {
            CameraPosition cameraPosition = map.getCameraPosition();
            LatLng cameraLaLng = cameraPosition.target;

            if (approximateEqual(deviceLocation.latitude, cameraLaLng.latitude, DEFAULT_PRECISION)
                    && approximateEqual(deviceLocation.longitude, cameraLaLng.longitude, DEFAULT_PRECISION)) {
                debugDisplay("onCameraMove: locked");
                cameraLocked = true;
            } else {
                debugDisplay("onCameraMove: unlocked");
                cameraLocked = false;
            }
        }
    }

    // Adapter to render view for info window
    public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private Context context;

        InfoWindowAdapter(Context context) {
            this.context = context;
        }

        @Override
        public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
            //None
            return null;
        }

        @Override
        public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
            View view = getLayoutInflater().inflate(R.layout.info_window_restaurant, null);
            String trackingNumber;
            if (!clusterItemsRendered) {
                Log.d(TAG, "InfoWindowAdapter: cluster not rendered");
                trackingNumber = (String) marker.getSnippet();
            } else {
                Log.d(TAG, "InfoWindowAdapter: cluster rendered");
                trackingNumber = (String) clickedClusterItem.getSnippet();
            }

            Log.d(TAG, "InfoWindowAdapter: filling activity");

            Restaurant restaurant = restaurants.getRestaurant(trackingNumber);

            String restaurantName = restaurant.getName();
            String address = restaurant.getAddress();
            String hazardLevel = restaurant.getHazardLevel(context);
            String lastInspected = restaurant.getLatestInspectionDate(context);
            String lastInspectedTotalIssues = restaurant.getLatestInspectionTotalIssues();

            ImageView logoIV = view.findViewById(R.id.logoImageView);
            logoIV.setImageResource(restaurant.getLogo());

            TextView textViewRestaurantName = view.findViewById(R.id.infoWindowRestaurantName);
            textViewRestaurantName.setText(restaurantName);

            TextView textViewRestaurantAddress = view.findViewById(R.id.infoWindowAddress);
            textViewRestaurantAddress.setText(address);

            TextView textViewInspectionDate = view.findViewById(R.id.infoWindowInspectionDate);
            TextView textViewTotalIssues = view.findViewById(R.id.infoWindowIssuesNum);

            if (restaurant.hasBeenInspected()) {
                textViewInspectionDate.setText(lastInspected);
                textViewTotalIssues.setText(lastInspectedTotalIssues);
            } else {
                textViewInspectionDate.setText(R.string.no_inspections_found);
                textViewTotalIssues.setText(R.string.no);
            }

            TextView textViewRestaurantHazardLevel = view.findViewById(R.id.infoWindowHazardLevel);
            ImageView imageViewHazardIcon = view.findViewById(R.id.infoWindowHazardIcon);
            CardView warningBar = view.findViewById(R.id.infoWindowWarningBar);

            switch (hazardLevel.toLowerCase()) {
                case "high":
                    Log.d(TAG, "getInfoContents: hazardLevel " + hazardLevel);
                    textViewRestaurantHazardLevel.setText(R.string.hazard_level_high);
                    imageViewHazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_high));
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardHighDark));
                    break;
                case "moderate":
                    Log.d(TAG, "getInfoContents: hazardLevel " + hazardLevel);
                    textViewRestaurantHazardLevel.setText(R.string.hazard_level_medium);
                    imageViewHazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_medium));
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardMediumDark));
                    break;
                default:
                    Log.d(TAG, "getInfoContents: hazardLevel " + hazardLevel);
                    textViewRestaurantHazardLevel.setText(R.string.hazard_level_low);
                    imageViewHazardIcon.setImageDrawable(context.getDrawable(R.drawable.icon_hazard_low));
                    warningBar.setCardBackgroundColor(context.getColor(R.color.hazardLowDark));
            }

            return view;
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    //Toolbar setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        /*final MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_show_list) {
            Intent intent = RestaurantListActivity.makeLaunchIntent(this);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.menu_filter) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FilterFragment filterFragment = new FilterFragment();
            filterFragment.show(fragmentManager, FilterFragment.TAG);
        }
        return super.onOptionsItemSelected(item);
    }
}
