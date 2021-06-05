package com.rodnog.rogermiddenway.restaurantmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressLint("MissingPermission")
public class AddPlaceActivity extends AppCompatActivity {

    private static final String TAG = "AddPlaceActivity";
    LocationManager locationManager;
    LocationListener locationListener;

    EditText restaurantNameEditText;
    AutocompleteSupportFragment autocompleteFragment;

    LatLng userLatLng;
    LatLng locationLatLng;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        restaurantNameEditText = findViewById(R.id.restaurantNameEditText);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        Button getLocation = findViewById(R.id.getLocationButton);
        Button showAllOnMap = findViewById(R.id.showOnMapButton);
        Button saveButton = findViewById(R.id.saveButton);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        // If location has changed, set locationLatLng to current location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("ADD", "onLocationChanged " + String.valueOf(location.getLatitude()));

                userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        };
        // If permissions not granted for GPS access, request permissions
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        // Else, set current location
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        Places.initialize(getApplicationContext(), getString(R.string.Places_API));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locationLatLng = place.getLatLng();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("PLACE", "An error occurred: " + status);
            }
        });
        // getLocation button gets current location and sets text field to current address
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationLatLng = userLatLng;
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                autocompleteFragment.setText(getCompleteAddressString(locationLatLng.latitude, locationLatLng.longitude));
            }
        });
        showAllOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationLatLng != null) {
                    Intent mapIntent = new Intent(AddPlaceActivity.this, MapsActivity.class);
                    mapIntent.putExtra("LATITUDE", locationLatLng.latitude);
                    mapIntent.putExtra("LONGITUDE", locationLatLng.longitude);
                    startActivity(mapIntent);
                }
                else{
                    Toast.makeText(AddPlaceActivity.this, "Choose a location first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddPlaceActivity.this, "Location saved", Toast.LENGTH_SHORT).show();
                DatabaseHelper db = new DatabaseHelper(AddPlaceActivity.this);
                db.insertRestaurant(new Restaurant(restaurantNameEditText.getText().toString(),
                        locationLatLng.longitude, locationLatLng.latitude));
                finish();
            }
        });
    }

    // Gets address from longitude and latitude
    private String getCompleteAddressString(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {
            Log.d(TAG, "Error getting address");
        }
        return strAdd;
    }
}