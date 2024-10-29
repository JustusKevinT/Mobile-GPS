package com.example.myapplication8;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView longitude, latitude, city, country, address;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        address = findViewById(R.id.address);

        // FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        } else {
            getLastKnownLocationFused();
        }

        // LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = location -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && addresses.size() > 0) {
                    Address addressObject = addresses.get(0);

                    latitude.setText(String.format("Latitude : %s", location.getLatitude()));
                    longitude.setText(String.format("Longitude : %s", location.getLongitude()));
                    country.setText(String.format("Country : %s", addressObject.getCountryName()));
                    city.setText(String.format("City : %s", addressObject.getLocality()));
                    address.setText(String.format("Address : %s",addressObject.getAddressLine(0)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses != null && addresses.size() > 0) {
                        Address addressObject = addresses.get(0);

                        latitude.setText(String.format("Latitude : %s", location.getLatitude()));
                        longitude.setText(String.format("Longitude : %s", location.getLongitude()));
                        country.setText(String.format("Country : %s", addressObject.getCountryName()));
                        city.setText(String.format("City : %s", addressObject.getLocality()));
                        address.setText(String.format("Address : %s",addressObject.getAddressLine(0)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }
    }

    private void getLastKnownLocationFused() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(this, location -> {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (addresses != null && addresses.size() > 0) {
                        Address addressObject = addresses.get(0);

                        latitude.setText(String.format("Latitude : %s", location.getLatitude()));
                        longitude.setText(String.format("Longitude : %s", location.getLongitude()));
                        country.setText(String.format("Country : %s", addressObject.getCountryName()));
                        city.setText(String.format("City : %s", addressObject.getLocality()));
                        address.setText(String.format("Address : %s",addressObject.getAddressLine(0)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }});
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                getLastKnownLocationFused();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
