package com.example.lothian_robert_rlothi300;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ThreeDayForecastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.three_day_forecast);

        // Retrieve WeatherInfo objects from intent extras
        List<WeatherInfo> weatherInfoList1 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo1")));
        List<WeatherInfo> weatherInfoList2 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo2")));
        List<WeatherInfo> weatherInfoList3 = Arrays.asList((WeatherInfo[]) Objects.requireNonNull(getIntent().getSerializableExtra("weatherInfo3")));

        // Display weather information for each day
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