// Name                 Robert Lothian
// Student ID           S2225607
// Programme of Study   Computer Science
//
package com.example.lothian_robert_rlothi300;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ThreeDayForecastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_day_forecast); // Change the layout to activity_three_day_forecast.xml

        // Retrieve WeatherInfo objects from intent extras
        List<WeatherInfo> weatherInfoList1 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo1")));
        List<WeatherInfo> weatherInfoList2 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo2")));
        List<WeatherInfo> weatherInfoList3 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo3")));

        // Retrieve location ID from intent extras
        String locationID = getIntent().getStringExtra("locationID");
        String locationName = "";
        
        //convert location identify to place location name.
        if ("2648579".equals(locationID)) {
            locationName = "Glasgow";
        } else if ("8224580".equals(locationID)) {
            locationName = "London";
        } else if ("5128581".equals(locationID)) {
            locationName = "New York";
        } else if ("287286".equals(locationID)) {
            locationName = "Oman";
        } else if ("934154".equals(locationID)) {
            locationName = "Mauritius";
        } else if ("1185241".equals(locationID)) {
            locationName = "Bangladesh";
        }

        // Set location text
        TextView locationTextView = findViewById(R.id.locationTextView);
        locationTextView.setText(locationName);

        // Display weather information for each day
        displayWeatherInfo(weatherInfoList1, R.id.fragmentContainer1);
        displayWeatherInfo(weatherInfoList2, R.id.fragmentContainer2);
        displayWeatherInfo(weatherInfoList3, R.id.fragmentContainer3);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_three_day_forecast_land);
            updateFragmentContainers(); // Update fragment containers
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_three_day_forecast);
            updateFragmentContainers(); // Update fragment containers
        }
    }

    private void updateFragmentContainers() {
        List<WeatherInfo> weatherInfoList1 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo1")));
        List<WeatherInfo> weatherInfoList2 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo2")));
        List<WeatherInfo> weatherInfoList3 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo3")));

        displayWeatherInfo(weatherInfoList1, R.id.fragmentContainer1);
        displayWeatherInfo(weatherInfoList2, R.id.fragmentContainer2);
        displayWeatherInfo(weatherInfoList3, R.id.fragmentContainer3);
    }


    private void displayWeatherInfo(List<WeatherInfo> weatherInfoList, int containerId) {
        if (weatherInfoList != null && !weatherInfoList.isEmpty()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (WeatherInfo weatherInfo : weatherInfoList) {
                if (weatherInfo != null) {
                    WeatherInfoFragment fragment = new WeatherInfoFragment();

                    // Create a bundle to pass the WeatherInfo object to the fragment
                    Bundle args = new Bundle();
                    args.putSerializable("weatherInfo", weatherInfo);
                    fragment.setArguments(args);

                    // Set the weather condition in the fragment
                    fragment.setWeatherCondition(weatherInfo.getCurrentCondition());

                    transaction.add(containerId, fragment);
                } else {
                    Log.e("displayWeatherInfo", "WeatherInfo object is null");
                    // Handle the null case, such as logging an error or skipping the update
                }
            }
            transaction.commit();
        }
    }
}
