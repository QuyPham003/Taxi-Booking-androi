package com.example.doancuoiky;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Taxi4Activity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvLocation;
    private Button btnBackToHome;

    private GoogleMap mMap;
    private LatLng currentLocation; // Declare currentLocation here
    private LatLng destinationLocation;
    private EditText editTextDestination;
    private TextView textViewDistance, textViewCost;
    private Button btnConfirm;
    private Spinner spinnerVehicleType; // Spinner for selecting vehicle type

    private static final double COST_TAXI_4_SEATS = 3000;  // 3000 VND per km
    private static final double COST_TAXI_6_SEATS = 6000;  // 6000 VND per km
    private static final double COST_VIP = 10000;          // 10000 VND per km
    private static final double COST_DELIVERY = 5000;      // 5000 VND per km

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi4);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Reference UI components
        tvLocation = findViewById(R.id.editCurrentLocation);
        btnBackToHome = findViewById(R.id.btnBackHome);
        editTextDestination = findViewById(R.id.editTextDestination);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewCost = findViewById(R.id.textViewCost);
        btnConfirm = findViewById(R.id.btnConfirm);
        spinnerVehicleType = findViewById(R.id.spinnerVehicleType);

        // Initialize Spinner with vehicle options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vehicle_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(adapter);

        // Add an item selected listener to the Spinner
        spinnerVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Recalculate the cost whenever the selected item changes
                calculateCost();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed here
            }
        });

        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is granted, get the location
            getCurrentLocation();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setUpMap();
        } else {
            // Yêu cầu quyền truy cập vị trí
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        btnConfirm.setOnClickListener(v -> {
            // Lấy giá trị được chọn từ Spinner (loại phương tiện)
            String selectedVehicleType = spinnerVehicleType.getSelectedItem().toString();

            // Kết hợp tất cả các thông tin thành một chuỗi
            String historyItem = "Điểm đặt: " + tvLocation.getText().toString() + "\n" +
                    "Điểm đến: " + editTextDestination.getText().toString() + "\n" +
                    "Quãng đường: " + textViewDistance.getText().toString() + "\n" +
                    "Chi phí: " + textViewCost.getText().toString() + "\n" +
                    "Loại phương tiện: " + selectedVehicleType;  // Thêm loại phương tiện

            // Lưu vào SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("TaxiHistory", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Lấy dữ liệu lịch sử hiện có
            Gson gson = new Gson();
            String json = sharedPreferences.getString("history", "[]");
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> historyList = gson.fromJson(json, type);

            // Thêm item mới vào danh sách
            historyList.add(historyItem);

            // Lưu lại danh sách lịch sử mới
            json = gson.toJson(historyList);
            editor.putString("history", json);
            editor.apply();

            // Chuyển sang HistoryActivity
            Intent intent = new Intent(Taxi4Activity.this, driverComeActivity.class);
            startActivity(intent);
        });




        // Set up button to return to HomeFragment
        btnBackToHome.setOnClickListener(v -> {
            finish(); // This will close Taxi4Activity and return to the previous screen (HomeFragment)
        });
    }

    private void setUpMap() {
        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }
    }

    private void calculateCost() {
        if (currentLocation == null || destinationLocation == null) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ vị trí", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected vehicle type from the spinner
        String selectedVehicleType = spinnerVehicleType.getSelectedItem().toString();
        double costPerKm = 0;

        switch (selectedVehicleType) {
            case "Taxi 4 chỗ":
                costPerKm = COST_TAXI_4_SEATS;
                break;
            case "Taxi 6 chỗ":
                costPerKm = COST_TAXI_6_SEATS;
                break;
            case "VIP":
                costPerKm = COST_VIP;
                break;
            case "Giao hàng":
                costPerKm = COST_DELIVERY;
                break;
        }

        float[] results = new float[1];
        Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                destinationLocation.latitude, destinationLocation.longitude,
                results
        );

        double distanceInKm = results[0] / 1000.0;
        double cost = distanceInKm * costPerKm;

        textViewDistance.setText(String.format("Quãng đường: %.2f km", distanceInKm));
        textViewCost.setText(String.format("Chi phí: %.0f VND", cost));
    }

    private void getCurrentLocation() {
        // Fetch the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // If location is not null, update UI with address
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        String locationInfo = "Latitude: " + latitude + "\nLongitude: " + longitude;

                        // Use Geocoder to get the address from the coordinates
                        Geocoder geocoder = new Geocoder(Taxi4Activity.this);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);

                                // Get full address, like "14 Đoàn Uẩn"
                                String addressString = address.getAddressLine(0);

                                // Update the UI with the address
                                tvLocation.setText("Địa chỉ: " + addressString);
                            } else {
                                tvLocation.setText("Không thể tìm thấy địa chỉ.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            tvLocation.setText("Lỗi khi lấy địa chỉ.");
                        }
                    } else {
                        tvLocation.setText("Không thể lấy vị trí hiện tại.");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission granted, continue
                getCurrentLocation();
                setUpMap();
            } else {
                // If permission denied
                Toast.makeText(this, "Bạn cần cấp quyền truy cập vị trí để tiếp tục", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Get current location after permission is granted
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Vị trí hiện tại"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                Toast.makeText(this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMapClickListener(latLng -> {
            destinationLocation = latLng;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Vị trí hiện tại"));
            mMap.addMarker(new MarkerOptions().position(destinationLocation).title("Điểm đến"));

            // Get address from the destination LatLng
            getAddressFromLatLng(destinationLocation);

            // Recalculate the cost
            calculateCost();
        });
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressString = address.getAddressLine(0);

                // Display the address in EditText
                editTextDestination.setText(addressString);
            } else {
                editTextDestination.setText("Không thể tìm thấy địa chỉ.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            editTextDestination.setText("Lỗi khi lấy địa chỉ.");
        }
    }
}


