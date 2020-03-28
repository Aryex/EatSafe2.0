package ca.cmpt276.restauranthealthinspection.model.updater;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.Resource;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.CheckUpdateFragment;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.ProgressDialogFragment;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Downloads data from server and saves in disk
 */

public class FileUpdater {

    //    public static final String TEMP_RESTAURANTS_FILE = "temp_restaurants.csv";
//    public static final String TEMP_INSPECTIONS_FILE = "temp_inspections.csv";
    public static final String RESTAURANTS_FILE = "restaurants.csv";
    public static final String INSPECTIONS_FILE = "inspections.csv";

    private static final String FILE_UPDATING_KEY = "file updating";
    private static final String TEMP_LAST_MODIFIED_KEY = "temp_last_modified";
    private static final String LAST_MODIFIED_KEY = "last_modified";

    //get when the local files were last modified
    public static long lastUpdated(Context context) {
        File restaurantsFile = new File(context.getApplicationContext().getFilesDir(), RESTAURANTS_FILE);
        File inspectionsFile = new File(context.getApplicationContext().getFilesDir(), INSPECTIONS_FILE);

        if (restaurantsFile.exists() && inspectionsFile.exists()) {
            return Math.max(restaurantsFile.lastModified(), inspectionsFile.lastModified());
        }
        return 0L;
    }

    //start download after 500 millisecond delay
    public static void startDownloadWithDelay(Context context, ProgressDialogFragment progressDialogFragment) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadFiles(context, progressDialogFragment);
            }
        }, 500);
    }

    //get when the data on server was last modified asynchronously
    public static void lastDateServerModified(CheckUpdateFragment checkUpdateFragment) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Call<JsonInfo> inspectionsUrlCall = retrofit.create(APIService.class).getInspectionsUrl();
        inspectionsUrlCall.enqueue(new Callback<JsonInfo>() {
            @Override
            public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {
                List<Resource> resources = response.body().getResult().getResources();
                long lastServerModified = resources.get(0).getLastModified().getTime();
                long lastLocalModified = getLastLocalModified(checkUpdateFragment.getContext());

                if (lastLocalModified != lastServerModified) {
                    checkUpdateFragment.update();
                    setTempServerModified(checkUpdateFragment.getContext(), lastServerModified);
                } else {
                    checkUpdateFragment.cancel();
                }
            }

            @Override
            public void onFailure(Call<JsonInfo> call, Throwable t) {
                checkUpdateFragment.cancel();
            }
        });
    }

    //download all files
    private static void downloadFiles(Context context, ProgressDialogFragment progressDialogFragment) {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Call<JsonInfo> inspectionsUrlCall = retrofit.create(APIService.class).getInspectionsUrl();

        //get the inspections url
        inspectionsUrlCall.enqueue(new Callback<JsonInfo>() {

            @Override
            public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {

                List<Resource> resources = response.body().getResult().getResources();
                String url = resources.get(0).getUrl();

                //start another call for getting inspections
                if (url != null) {
                    getInspections(url, context, progressDialogFragment);
                }

                //get url for restaurants
                Call<JsonInfo> restaurantsUrlCall = retrofit.create(APIService.class).getRestaurantsUrl();

                restaurantsUrlCall.enqueue(new Callback<JsonInfo>() {
                    @Override
                    public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {

                        List<Resource> resources = response.body().getResult().getResources();
                        String url = resources.get(0).getUrl();

                        //start another call for getting inspections
                        if (url != null) {
                            getRestaurants(url, context, progressDialogFragment);
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonInfo> call, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(Call<JsonInfo> call, Throwable throwable) {
                throwable.printStackTrace();

            }
        });

        progressDialogFragment.setUrlCall(inspectionsUrlCall);
    }

    //client for progress bar
    private static OkHttpClient getOkHttpClient(DownloadListener downloadListener) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                if (downloadListener == null) {
                    return chain.proceed(chain.request());
                }

                okhttp3.Response response = chain.proceed(chain.request());

                return response.newBuilder()
                        .body(new RestaurantsResponseBody(response.body(), downloadListener)).build();
            }
        });

        return httpClientBuilder.build();
    }


    private static void getRestaurants(String url, Context context, ProgressDialogFragment progressDialogFragment) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .client(getOkHttpClient(new DownloadListener() {
                    @Override
                    public void downloadUpdate(int percent) {

                        //WIP, server side does not provide content length 100% of the time
//                        progressDialogFragment.setProgress(percent);
//                        Log.d("test", percent + "");

                    }
                }))
                .build();
        Call<ResponseBody> restaurantsCall = retrofit.create(APIService.class).downloadInspections(url);
        restaurantsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //write all restaurants to disk
                    boolean done = writeRestaurantsToDisk(response.body(), context);
                    //update progress bar
                    progressDialogFragment.setProgress(progressDialogFragment.getProgress() + 50);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        progressDialogFragment.setRestaurantsCall(restaurantsCall);

    }

    private static void getInspections(String url, Context context, ProgressDialogFragment progressDialogFragment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .build();
        Call<ResponseBody> inspectionsCall = retrofit.create(APIService.class).downloadInspections(url);
        inspectionsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //write all inspections to disk
                if (response.isSuccessful()) {
                    boolean done = writeInspectionsToDisk(response.body(), context);
                    progressDialogFragment.setProgress(progressDialogFragment.getProgress() + 50);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        progressDialogFragment.setInspectionsCall(inspectionsCall);
    }


    private static boolean writeInspectionsToDisk(ResponseBody body, Context context) {
        //write to disk
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(INSPECTIONS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(body.bytes());
            fileOutputStream.close();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean writeRestaurantsToDisk(ResponseBody body, Context context) {
        //write to disk
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(RESTAURANTS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(body.bytes());
            fileOutputStream.close();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    //save when the server was last modified as a temp value
    private static void setTempServerModified(Context context, long l) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_UPDATING_KEY, Context.MODE_PRIVATE).edit();
        editor.putLong(TEMP_LAST_MODIFIED_KEY, l);
        editor.apply();
    }

    //get when server was last modified
    private static long getLastLocalModified(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_UPDATING_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_MODIFIED_KEY, 0L);
    }

    //set when server was last modified to temp value
    public static void completeDownload(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FILE_UPDATING_KEY, Context.MODE_PRIVATE).edit();

        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_UPDATING_KEY, Context.MODE_PRIVATE);
        long tempLastModified = sharedPreferences.getLong(TEMP_LAST_MODIFIED_KEY, 0L);

        editor.putLong(LAST_MODIFIED_KEY, tempLastModified);
        editor.apply();
    }
}
