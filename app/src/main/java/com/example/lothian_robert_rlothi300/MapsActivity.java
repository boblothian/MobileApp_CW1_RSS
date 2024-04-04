// Name                 Robert Lothian
// Student ID           S2225607
// Programme of Study   Computer Science
//

package com.example.lothian_robert_rlothi300;

import androidx.fragment.app.FragmentActivity;


import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lothian_robert_rlothi300.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.lothian_robert_rlothi300.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        String latitude = getIntent().getStringExtra("Latitude");
        String longitude = getIntent().getStringExtra("Longitude");
        assert latitude != null;
        double lat =Double.parseDouble(latitude);
        assert longitude != null;
        double lng = Double.parseDouble(longitude);

        LatLng location = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(location).title("You are here"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}