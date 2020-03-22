package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.ui.restaurant_details.RestaurantDetails;


/**
 * Map activity serves as the first entry point into the app.
 * The map uses Google Map API.
 * Map activity uses the Fused Location API to to track user location.
 * <p>
 * Codes were adapted from the following resources.
 * https://developer.android.com/training/location
 * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
 */
public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter,
        ClusterManager.OnClusterClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemClickListener {

    //  Surrey Central's Lat Lng
    public static final LatLng DEFAULT_LAT_LNG = new LatLng(49.188808, -122.847992);

    //Debug
    private static final String TAG = "MapsActivity";
    private TextView debugTextview;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 19f;
    private static final double DEFAULT_PRECISION = 0.0001;

    private RestaurantManager restaurants;

    private boolean cameraLocked = true;

    private GoogleMap map;
    private ClusterManager<ClusterMarker> markerClusterManager;
    private ClusterMarker clickedClusterItem;

    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LatLng deviceLocation;
    private float cameraZoom = DEFAULT_ZOOM;


    public static Intent makeLaunchIntent(Context context, LatLng position) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("position", position);
        return intent;
    }

    //What to do when map appear on screen
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map loaded");
        Toast.makeText(this, "Eat Safe!", Toast.LENGTH_SHORT).show();
        map = googleMap;
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        setupMarkers(map);
        /*map.setOnInfoWindowClickListener(MapsActivity.this);
        map.setInfoWindowAdapter(MapsActivity.this);*/

        //markerClusterManager.setOnClusterItemClickListener(MapsActivity.this);


        markerClusterManager.setOnClusterItemClickListener(MapsActivity.this);
        markerClusterManager.getMarkerCollection().setInfoWindowAdapter(MapsActivity.this);
        markerClusterManager.getMarkerCollection().setOnInfoWindowClickListener(MapsActivity.this);

        //Map will use markerCluster's implementations.
        map.setOnCameraIdleListener(markerClusterManager);
        map.setOnMarkerClickListener(markerClusterManager);
        map.setOnInfoWindowClickListener(markerClusterManager);

        if (locationPermissionGranted) {
            getLastKnownLocation();
            map.setMyLocationEnabled(true);
            map.setOnCameraMoveListener(MapsActivity.this);
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Log.d(TAG, "onMyLocationButtonClick: camera locked");
                    cameraZoom = map.getCameraPosition().zoom;
                    if (cameraZoom < DEFAULT_ZOOM) {
                        cameraZoom = DEFAULT_ZOOM;
                    }
                    moveCamera(deviceLocation, cameraZoom);
                    cameraLocked = true;
                    return true;
                }
            });

        }
    }


    private boolean inBetweenAbsolutes(double absolute, double value) {
        return value > -absolute && value < absolute;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        debugTextview = findViewById(R.id.debugTextview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupManualLock();
        setupModel();
        setupDebug();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        deviceLocation = new LatLng(49.246292, -123.116226);

        getLocationPermission();
        createLocationRequest();

    }

    private void setupDebug() {
        Button button = findViewById(R.id.surreyButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCamera(new LatLng(49.188808, -122.847992), 12f);
            }
        });
    }

    private void setupManualLock() {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraLocked = !cameraLocked;
                moveCamera(deviceLocation, cameraZoom);
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
        this.fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

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
                    Log.d(TAG, "onLocationResult: called + cameraLocked: " + cameraLocked);

                    //Log.d(TAG, "setOnCameraMoveListener: device: " + deviceLocation + " / camera: " + cameraLaLng);

                    if (cameraLocked) {
                        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        double latPrecision = deviceLocation.latitude - location.getLatitude();
                        double lngPrecision = deviceLocation.longitude - location.getLongitude();

                        if (!deviceLocation.equals(newLocation)) {
                            deviceLocation = newLocation;
                            moveCamera(newLocation, cameraZoom);
                        }
                    }
                }
            }
        };
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: Called");

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got the last known location which may be null
                        if (location != null) {
                            // Logic to handle location object
                            Log.d(TAG, "getLastKnownLocation: got current location");
                            LatLng lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            deviceLocation = lastKnownLocation;
                            moveCamera(lastKnownLocation, 12f);
                        } else {
                            Log.d(TAG, "getLastKnownLocation: current location is null");
                        }
                    }
                });
    }

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
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            //If we do not have location permission, request one.
            //override onRequestPermissionsResult() to check.
            Log.d(TAG, "getLocationPermission: locationPermission not granted");
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    locationPermissionGranted = true;
                    createMap();
                }
            }
            default:
        }
    }

    private void createMap() {
        Log.d(TAG, "createMap: creating map");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(MapsActivity.this);
    }

    private void setupMarkers(GoogleMap map) {
        //      Marker cluster setup
        markerClusterManager = new ClusterManager<ClusterMarker>(this, map);

        for (Restaurant restaurant : restaurants) {

            double lat = restaurant.getLatitude();
            double lng = restaurant.getLongitude();
            LatLng restaurantLatLng = new LatLng(lat, lng);
            String name = restaurant.getName();
            String snippet = restaurant.getResTrackingNumber();

            //Setup Markers
/*            Marker marker = map.addMarker(new MarkerOptions()
                    .position(restaurantLatLng)
                    .title(name)
                    .snippet(snippet));
            marker.setTag(restaurant.getResTrackingNumber());*/

            //Add marker to ClusterManager
            ClusterMarker clusterMarker = new ClusterMarker(lat, lng, name, snippet);
            markerClusterManager.addItem(clusterMarker);

        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
    }

    private void moveCamera(LatLng newLocation, float zoom) {
        Log.d(TAG, "moveCamera: called");
        //Toast.makeText(this, "moveCamera: called", Toast.LENGTH_SHORT).show();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoom));

    }

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void debugDisplay(String s) {
        debugTextview.setText(s);
    }

    @Override
    public void onCameraMove() {
        CameraPosition cameraPosition = map.getCameraPosition();
        LatLng cameraLaLng = cameraPosition.target;

        //Log.d(TAG, "setOnCameraMoveListener: device: " + deviceLocation + " / camera: " + cameraLaLng);
        double latPrecision = deviceLocation.latitude - cameraLaLng.latitude;
        double lngPrecision = deviceLocation.longitude - cameraLaLng.longitude;

        if (inBetweenAbsolutes(DEFAULT_PRECISION, latPrecision)
                && inBetweenAbsolutes(DEFAULT_PRECISION, lngPrecision)) {
            Log.d(TAG, "setOnCameraMoveListener: locked");
            debugDisplay("setOnCameraMoveListener: locked");
            cameraLocked = true;
        } else {
            Log.d(TAG, "setOnCameraMoveListener: unlocked");
            debugDisplay("setOnCameraMoveListener: unlocked");
            cameraLocked = false;
        }

    }

    public void onInfoWindowClick(com.google.android.gms.maps.model.Marker marker) {
//        String trackingID = (String) marker.getTag();
        String trackingID = (String) clickedClusterItem.getSnippet();
        Intent intent = RestaurantDetails.makeLaunchIntent(MapsActivity.this, trackingID);
        startActivity(intent);
    }

    private void setupModel() {
        Log.i("Start parsing", "Starting to parse data....");

        restaurants = RestaurantManager.getInstance(this);

        for (Restaurant r : restaurants) {
            Log.d("Main Activity", "onCreate: " + r);
        }

        Restaurant restaurant = restaurants.get(2);
        Log.d("MainActivity", restaurant.getResTrackingNumber());
        List<Inspection> inspections = restaurant.getInspections();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: Called");
        Toast.makeText(this, "onLocationChanged: Called", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
        //None
        return null;
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.info_window_restaurant, null);
//        String trackingNumber = (String) marker.getTag();
//        Restaurant restaurant = restaurants.getRestaurant(trackingNumber);
//
//        String restauranName = restaurant.getName();
//        String address = restaurant.getAddress();
//        String hazardLevel = restaurant.getHazardLevel();
//
//        TextView textViewRestaurantName = view.findViewById(R.id.infoWindowRestaurantName);
//        textViewRestaurantName.setText(restauranName);
//
//        TextView textViewRestaurantAddr = view.findViewById(R.id.infoWindowAddress);
//        textViewRestaurantAddr.setText(address);

        String trackingNumber = (String) clickedClusterItem.getSnippet();
        Restaurant restaurant = restaurants.getRestaurant(trackingNumber);

        String restauranName = restaurant.getName();
        String address = restaurant.getAddress();
        String hazardLevel = restaurant.getHazardLevel();

        TextView textViewRestaurantName = view.findViewById(R.id.infoWindowRestaurantName);
        textViewRestaurantName.setText(restauranName);

        TextView textViewRestaurantAddr = view.findViewById(R.id.infoWindowAddress);
        textViewRestaurantAddr.setText(address);

        TextView textViewRestaurantHazardLevel = view.findViewById(R.id.infoWindowHazardLevel);
        ImageView imageViewHazardIcon = view.findViewById(R.id.infoWindowHazardIcon);
        CardView warningBar = view.findViewById(R.id.infoWindowWarningBar);

        switch (hazardLevel.toLowerCase()) {
            case "high":
                Log.d(TAG, "getInfoContents: hazardLevel " + hazardLevel);
                textViewRestaurantHazardLevel.setText(R.string.hazard_level_high);
                imageViewHazardIcon.setImageDrawable(this.getDrawable(R.drawable.icon_hazard_high));
                warningBar.setCardBackgroundColor(this.getColor(R.color.hazardHighDark));
                break;
            case "moderate":
                Log.d(TAG, "getInfoContents: hazardLevel " + hazardLevel);
                textViewRestaurantHazardLevel.setText(R.string.hazard_level_medium);
                imageViewHazardIcon.setImageDrawable(this.getDrawable(R.drawable.icon_hazard_medium));
                warningBar.setCardBackgroundColor(this.getColor(R.color.hazardMediumDark));
                break;
            default:
                Log.d(TAG, "getInfoContents: hazardLevel " + hazardLevel);
                textViewRestaurantHazardLevel.setText(R.string.hazard_level_low);
                imageViewHazardIcon.setImageDrawable(this.getDrawable(R.drawable.icon_hazard_low));
                warningBar.setCardBackgroundColor(this.getColor(R.color.hazardLowDark));
        }

        return view;
    }

    @Override
    public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
        return false;
    }

    @Override
    public boolean onClusterItemClick(ClusterItem item) {
        clickedClusterItem = new ClusterMarker(item.getPosition(), item.getTitle(), item.getSnippet());
        return false;
    }


    //Toolbar setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_show_list:
                Intent intent = RestaurantListActivity.makeLaunchIntent(this);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
