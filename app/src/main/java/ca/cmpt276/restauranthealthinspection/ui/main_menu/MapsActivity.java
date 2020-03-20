package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;


/**
 * Map activity serves as the first entry point into the app.
 * The map uses Google Map API.
 * Map activity uses the Fused Location API to to track user location.
 * <p>
 * Codes were adapted from the following resources.
 * https://developer.android.com/training/location
 * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    //Debug
    private static final String TAG = "MapsActivity";
    private TextView debugTextview;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private boolean cameraLocked = true;

    private GoogleMap map;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;


    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LatLng deviceLocation;
    private float cameraZoom = DEFAULT_ZOOM;

    //What to do when map appear on screen
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map loaded");
        Toast.makeText(this, "Eat Safe!", Toast.LENGTH_SHORT).show();
        map = googleMap;

        if (locationPermissionGranted) {
            getLastKnownLocation();
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Log.d(TAG, "onMyLocationButtonClick: camera locked");
                    cameraZoom = map.getCameraPosition().zoom;
                    moveCamera(deviceLocation,cameraZoom);
                    cameraLocked = true;
                    return true;
                }
            });
            map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    CameraPosition cameraPosition = map.getCameraPosition();
                    LatLng cameraLaLng = cameraPosition.target;

                    //Log.d(TAG, "setOnCameraMoveListener: device: " + deviceLocation + " / camera: " + cameraLaLng);
                    double latPrecision = deviceLocation.latitude - cameraLaLng.latitude;
                    double lngPrecision = deviceLocation.longitude - cameraLaLng.longitude;

                    if(inbetween(0.0001, latPrecision)
                            && inbetween(0.0001, lngPrecision)){
                        Log.d(TAG, "setOnCameraMoveListener: locked");
                        debugDisplay("setOnCameraMoveListener: locked");
                        cameraLocked = true;
                    }else{
                        Log.d(TAG, "setOnCameraMoveListener: unlocked");
                        debugDisplay("setOnCameraMoveListener: unlocked");
                        cameraLocked = false;
                    }

                    /*if (!deviceLocation.equals(cameraLaLng)) {
                        Log.d(TAG, "setOnCameraMoveListener: unlocked");
                        cameraLocked = false;
                    }else{
                        Log.d(TAG, "setOnCameraMoveListener: locked");
                        cameraLocked = true;
                    }*/
                }
            });

        }
    }



    private boolean inbetween(double absolute, double value){
        return value > - absolute && value < absolute;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        debugTextview = findViewById(R.id.debugTextview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupModel();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        deviceLocation = new LatLng(49.246292, -123.116226);

        getLocationPermission();
        createLocationRequest();

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
//                    Toast.makeText(MapsActivity.this, "onLocationResult: Lat: " + location.getLatitude() + " Long: " + location.getLongitude(), Toast.LENGTH_SHORT)
//                            .show();
                    if (cameraLocked) {
                        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
                            LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            moveCamera(newLocation, DEFAULT_ZOOM);
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

    private void setupModel() {
        Log.i("Start parsing", "Starting to parse data....");

        RestaurantManager restaurants = RestaurantManager.getInstance(this);

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
}
