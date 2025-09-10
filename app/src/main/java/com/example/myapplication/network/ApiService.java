package com.example.myapplication.network;

import com.example.myapplication.model.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface ApiService {
    @GET("search.php")
    Call<List<Place>> searchPlaces(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("format") String format
    );
}
