package com.example.myapplication.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Place;
import com.example.myapplication.utils.DebounceTextWatcher;
import com.example.myapplication.viewmodel.PlaceViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private PlaceViewModel viewModel;
    private PlaceAdapter adapter;
    private FusedLocationProviderClient fusedLocation;

    @Nullable
    private Place pendingPlace; // lưu place người dùng vừa chọn nếu còn thiếu quyền

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted && pendingPlace != null) {
                    navigateTo(pendingPlace);
                } else if (!granted) {
                    Toast.makeText(this, "Cần quyền vị trí để dẫn đường", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        EditText editSearch = findViewById(R.id.editSearch);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new PlaceAdapter(place -> {
            pendingPlace = place;
            // xin quyền nếu chưa có, xong thì navigate
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                navigateTo(place);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);
        viewModel.getPlaces().observe(this, places ->
                adapter.submit(places, editSearch.getText().toString().trim()));

        // Debounce 600–1000ms là hợp lý
        editSearch.addTextChangedListener(new DebounceTextWatcher(700, text -> {
            String q = text.trim();
            if (q.length() >= 2) {
                runOnUiThread(() -> viewModel.search(q));
            } else {
                runOnUiThread(() -> adapter.submit(java.util.Collections.emptyList(), q));
            }
        }));
    }

    private void navigateTo(Place place) {
        // lấy last location (nhanh, đủ cho mở Google Maps). Có thể null nếu máy chưa có fix.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocation.getLastLocation().addOnSuccessListener(location -> {
            double sLat = location != null ? location.getLatitude() : 0d;
            double sLon = location != null ? location.getLongitude() : 0d;

            String uri;
            if (location != null) {
                // có vị trí hiện tại → chỉ đường từ here → đích
                uri = "https://www.google.com/maps/dir/?api=1"
                        + "&origin=" + sLat + "," + sLon
                        + "&destination=" + place.getLat() + "," + place.getLon()
                        + "&travelmode=driving";
            } else {
                // không có vị trí hiện tại → vẫn mở chỉ đường tới đích (Google Maps tự lấy 'My Location')
                uri = "https://www.google.com/maps/dir/?api=1"
                        + "&destination=" + place.getLat() + "," + place.getLon()
                        + "&travelmode=driving";
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // fallback nếu máy không có Google Maps
                intent.setPackage(null);
                startActivity(intent);
            }
        });
    }
}
