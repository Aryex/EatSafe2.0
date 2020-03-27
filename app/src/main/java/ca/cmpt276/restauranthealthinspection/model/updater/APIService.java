package ca.cmpt276.restauranthealthinspection.model.updater;

import com.google.gson.JsonElement;

import ca.cmpt276.restauranthealthinspection.model.updater.pojos.JsonInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface APIService {

    static String BASE_URL = "http://data.surrey.ca/";

    @GET("/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports")
    Call<JsonInfo> getInspectionsUrl();

    @GET("/api/3/action/package_show?id=restaurants")
    Call<JsonInfo> getRestaurantsUrl();

    @GET
    Call<ResponseBody> downloadInspections(@Url String file);

    @GET
    Call<ResponseBody> downloadRestaurants(@Url String file);
}
