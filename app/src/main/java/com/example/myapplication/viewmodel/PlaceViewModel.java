package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Place;
import com.example.myapplication.repository.PlaceRepository;

import java.util.List;

public class PlaceViewModel extends ViewModel {
    private final PlaceRepository repository = new PlaceRepository();
    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final LiveData<List<Place>> places =
            Transformations.switchMap(query, repository::searchPlaces);

    public void search(String q) { query.setValue(q); }
    public LiveData<List<Place>> getPlaces() { return places; }
}
