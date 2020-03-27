package ca.cmpt276.restauranthealthinspection.model.updater;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.Resource;
import ca.cmpt276.restauranthealthinspection.ui.main_menu.dialog.ProgressDialogFragment;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FileUpdater {

    private static final String RESTAURANTS_FILE = "restaurants.csv";
    private static final String INSPECTIONS_FILE = "inspections.csv";

    public static boolean checkUpdate() {
        return true;
    }

    public static void startDownloadWithDelay(Context context, ProgressDialogFragment progressDialogFragment) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadFiles(context, progressDialogFragment);
            }
        }, 500);
    }

    private static void downloadFiles(Context context, ProgressDialogFragment progressDialogFragment) {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        APIService apiService = retrofit.create(APIService.class);

        apiService.getInspectionsUrl().enqueue(new Callback<JsonInfo>() {

            @Override
            public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {

                List<Resource> resources = response.body().getResult().getResources();
                Log.d("test", resources.get(0).getUrl());
                String url = resources.get(0).getUrl();

                if (url != null) {
                    getInspections(url, context, progressDialogFragment);
                }

                apiService.getRestaurantsUrl().enqueue(new Callback<JsonInfo>() {
                    @Override
                    public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {

                        List<Resource> resources = response.body().getResult().getResources();
                        Log.d("test", resources.get(0).getUrl());
                        String url = resources.get(0).getUrl();

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
    }

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

//                        progressDialogFragment.setProgress(percent);
//                        Log.d("test", percent + "");
                    }
                }))
                .build();
        APIService apiService = retrofit.create(APIService.class);
        apiService.downloadInspections(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean done = writeRestaurantsToDisk(response.body(), context);
                    Toast.makeText(context, "restaurants done", Toast.LENGTH_SHORT).show();
                    progressDialogFragment.setProgress(progressDialogFragment.getProgress() + 50);
                }
                else {
                    Toast.makeText(context, "restaurants error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(context, "big error restaurants", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        });

    }

    private static void getInspections(String url, Context context, ProgressDialogFragment progressDialogFragment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .build();
        APIService apiService = retrofit.create(APIService.class);
        apiService.downloadInspections(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean done = writeInspectionsToDisk(response.body(), context);
                    Toast.makeText(context, "inspections done", Toast.LENGTH_SHORT).show();
                    progressDialogFragment.setProgress(progressDialogFragment.getProgress() + 50);
                }
                else {
                    Toast.makeText(context, "Inspections error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Toast.makeText(context, "big error inspections", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private static boolean writeInspectionsToDisk(ResponseBody body, Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(INSPECTIONS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(body.bytes());
            fileOutputStream.close();
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean writeRestaurantsToDisk(ResponseBody body, Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(RESTAURANTS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(body.bytes());
            fileOutputStream.close();
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
}
