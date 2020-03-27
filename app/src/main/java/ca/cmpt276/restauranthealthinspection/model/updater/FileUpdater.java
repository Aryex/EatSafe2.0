package ca.cmpt276.restauranthealthinspection.model.updater;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import ca.cmpt276.restauranthealthinspection.model.updater.pojos.Resource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FileUpdater {

    private static final String RESTAURANTS_FILE = "restaurants.csv";
    private static final String INSPECTIONS_FILE = "inspections.csv";

    public static void downloadAndSave(Context context) {

        getUrl(context);
    }

    private static void getUrl(Context context) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        APIInterface apiInterface = retrofit.create(APIInterface.class);
        apiInterface.callInspections().enqueue(new Callback<JsonInfo>() {
            @Override
            public void onResponse(Call<JsonInfo> call, Response<JsonInfo> response) {
                Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();

                List<Resource> resources = response.body().getResult().getResources();
                Log.d("test", resources.get(0).getUrl());
            }

            @Override
            public void onFailure(Call<JsonInfo> call, Throwable throwable) {
                throwable.printStackTrace();

            }
        });
    }

    private static boolean writeToDisk(ResponseBody body, Context context) {

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
