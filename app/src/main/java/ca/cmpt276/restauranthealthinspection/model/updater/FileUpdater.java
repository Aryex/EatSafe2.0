package ca.cmpt276.restauranthealthinspection.model.updater;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonRestaurant;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.Resource;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.Result;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FileUpdater {
    public static void connectToServer() {
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

                Log.i("test", "Format: " + resources.get(0).getFormat());
                Log.i("test", "URL: " + resources.get(0).getUrl());
                Log.i("test", "Last modified: " + resources.get(0).getLastModified());
            }

            @Override
            public void onFailure(Call<JsonInfo> call, Throwable t) {
                Log.e("APICouldNotConnect", "Could not connect to api to API");
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

                Log.i("test", "Format: " + resources.get(0).getFormat());
                Log.i("test", "URL: " + resources.get(0).getUrl());
                Log.i("test", "Last modified: " + resources.get(0).getLastModified());
            }

            @Override
            public void onFailure(Call<JsonInfo> call, Throwable throwable) {

            }
        });



    }
}
