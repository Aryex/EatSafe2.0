package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;

import static android.telephony.CellLocation.requestLocationUpdate;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap map;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location currentLocation;
    private  LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupModel();

        setupLocationCallBack();
        getLocationPermission();
        createLocationRequest();

    }



    private void setupLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "onLocationResult: called");
                    Toast.makeText(MapsActivity.this,  "onLocationResult: Lat: " + location.getLatitude() + " Long: " + location.getLongitude(), Toast.LENGTH_SHORT)
                            .show();
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
                }
            };
        };
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void getDeviceCurrentLocation() {
        Log.d(TAG, "getDeviceCurrentLocation: getting device location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getDeviceCurrentLocation: got current location");
                        currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                    } else {
                        Log.d(TAG, "getDeviceCurrentLocation: current location is null");
                        Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceCurrentLocation: " + e.getMessage());
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map loaded");
        Toast.makeText(this, "Eat Safe!", Toast.LENGTH_SHORT).show();
        map = googleMap;

        if (locationPermissionGranted) {
            //setupDeviceLocationUpdate();

            getDeviceCurrentLocation();
            map.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "OnLocationChanged: creating map");
        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: called");
        Toast.makeText(this, "moveCamera: called", Toast.LENGTH_SHORT).show();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

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
}
