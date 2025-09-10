package com.example.myapplication.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.Place;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceRepository {
    private static final String API_KEY = "pk.1acb6f3a7a2d99f2532ca213e5835b56";
    private ApiService apiService;

    public PlaceRepository() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<List<Place>> searchPlaces(String query) {
        MutableLiveData<List<Place>> data = new MutableLiveData<>();

        apiService.searchPlaces(API_KEY, query, "json")
                .enqueue(new Callback<List<Place>>() {
                    @Override
                    public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Place>> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }
}
