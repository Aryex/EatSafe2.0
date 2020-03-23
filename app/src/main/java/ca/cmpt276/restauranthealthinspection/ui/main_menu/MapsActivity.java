package ca.cmpt276.restauranthealthinspection.ui.main_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.R;
import ca.cmpt276.restauranthealthinspection.model.Inspection;
import ca.cmpt276.restauranthealthinspection.model.Restaurant;
import ca.cmpt276.restauranthealthinspection.model.RestaurantManager;
import ca.cmpt276.restauranthealthinspection.model.updater.APIInterface;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInspection;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonRestaurant;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.Resource;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.Result;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.UpdaterDialogFragment;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Map activity serves as the first entry point into the app.
 * The map uses Google Map API.
 * Map activity uses the Fused Location API to to track user location.
 *
 * Codes were adapted from the following resources.
 * https://developer.android.com/training/location
 * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, UpdaterDialogFragment.UpdaterDialogListener {

    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private boolean cameraLocked = true;

    private GoogleMap map;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private JsonInspection jsonInspection;
    private JsonRestaurant jsonRestaurant;

    //What to do when map appear on screen
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map loaded");
        Toast.makeText(this, "Eat Safe!", Toast.LENGTH_SHORT).show();
        map = googleMap;

        if (locationPermissionGranted) {
            getDeviceCurrentLocation();
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectToServer();



        setupModel();

        setupLocationCallBack();
        getLocationPermission();
        createLocationRequest();

    }

    public void showUpdaterDialog() {
        DialogFragment updaterDialog = new UpdaterDialogFragment();
        updaterDialog.show(getSupportFragmentManager(), "Updater Dialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.setTitle("Updating data...");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);

        progressDialog.show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
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
                    Toast.makeText(MapsActivity.this, "onLocationResult: Lat: " + location.getLatitude() + " Long: " + location.getLongitude(), Toast.LENGTH_SHORT)
                            .show();
                    if (cameraLocked) {
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
                    }
                }
            }

            ;
        };
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

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: called");
        Toast.makeText(this, "moveCamera: called", Toast.LENGTH_SHORT).show();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
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

//        for (Restaurant r : restaurants) {
//            Log.d("Main Activity", "onCreate: " + r);
//        }

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

    public void connectToServer() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://data.surrey.ca")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client);

        Retrofit retrofit = builder.build();

        APIInterface apiInterface = retrofit.create(APIInterface.class);
        Call<JsonInfo> call = apiInterface.callRestaurants();

        call.enqueue(new Callback<JsonInfo>() {
            @Override
            public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {
                Log.i("APIConnected", "Response code: " + response.code());
                JsonInfo jsonInfo = response.body();

                Result result = jsonInfo.getResult();
                List<Resource> resources = result.getResources();

                JsonRestaurant jsonRestaurant = new JsonRestaurant(
                        resources.get(0).getFormat(),
                        resources.get(0).getUrl(),
                        resources.get(0).getLastModified(),
                        resources.get(0).getPosition()
                );

                Log.i("jsonRestaurant", "Format: " + resources.get(0).getFormat());
                Log.i("jsonRestaurant", "URL: " + resources.get(0).getUrl());
                Log.i("jsonRestaurant", "Last modified: " + resources.get(0).getLastModified());
            }

            @Override
            public void onFailure(Call<JsonInfo> call, Throwable t) {
                Log.e("APICouldNotConnect", "Could not connect to api to API");
                Toast.makeText(MapsActivity.this, R.string.api_error, Toast.LENGTH_LONG).show();
            }
        });

        call = apiInterface.callInspections();
        call.enqueue(new Callback<JsonInfo>() {
            @Override
            public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {
                Log.i("APIConnected", "Response code: " + response.code());
                JsonInfo jsonInfo = response.body();

                Result result = jsonInfo.getResult();
                List<Resource> resources = result.getResources();

                JsonInspection jsonInspection = new JsonInspection(
                        resources.get(0).getFormat(),
                        resources.get(0).getUrl(),
                        resources.get(0).getLastModified(),
                        resources.get(0).getPosition()
                );

                Log.i("jsonInspection", "Format: " + resources.get(0).getFormat());
                Log.i("jsonInspection", "URL: " + resources.get(0).getUrl());
                Log.i("jsonInspection", "Last modified: " + resources.get(0).getLastModified());
            }

            @Override
            public void onFailure(Call<JsonInfo> call, Throwable throwable) {
                Log.e("APICouldNotConnect", "Could not connect to api to API");
                Toast.makeText(MapsActivity.this, R.string.api_error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private int getHoursInBetween(long lastModified) {
        long hours = Calendar.getInstance().getTimeInMillis() - lastModified;

        return (int) Math.round(hours / (60.0 * 60  * 1000)); // 60 seconds * 60 minutes * 24 hours * 1000 ms per second
    }
}
