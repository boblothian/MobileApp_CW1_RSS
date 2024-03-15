package com.example.lothian_robert_rlothi300;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class ThreeDayForecastActivity extends AppCompatActivity {
    private TextView[] dayTextViews;
    private TextView[] maxTempTextViews;
    private TextView[] minTempTextViews;
    private TextView[] windDirectionTextViews;
    private TextView[] windSpeedTextViews;
    private TextView[] visibilityTextViews;
    private TextView[] pressureTextViews;
    private TextView[] humidityTextViews;
    private TextView[] uvRiskTextViews;
    private TextView[] pollutionTextViews;
    private TextView[] sunriseTextViews;
    private TextView[] sunsetTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.three_day_forecast);



        // Initialize arrays of TextViews for each weather parameter
        initializeTextViewArrays();

        // Retrieve WeatherInfo objects from intent extras
        List<WeatherInfo> weatherInfoList1 = Arrays.asList((WeatherInfo[]) getIntent().getSerializableExtra("weatherInfo1"));
        List<WeatherInfo> weatherInfoList2 = Arrays.asList((WeatherInfo[]) getIntent().getSerializableExtra("weatherInfo2"));
        List<WeatherInfo> weatherInfoList3 = Arrays.asList((WeatherInfo[]) getIntent().getSerializableExtra("weatherInfo3"));

        // Display weather information for each day
        displayWeatherInfo(weatherInfoList1, 0);
        displayWeatherInfo(weatherInfoList2, 1);
        displayWeatherInfo(weatherInfoList3, 2);
    }

    private void initializeTextViewArrays() {
        // Initialize arrays of TextViews for each weather parameter
        dayTextViews = new TextView[]{findViewById(R.id.day1), findViewById(R.id.day2), findViewById(R.id.day3)};
        maxTempTextViews = new TextView[]{findViewById(R.id.maxTemp1), findViewById(R.id.maxTemp2), findViewById(R.id.maxTemp3)};
        minTempTextViews = new TextView[]{findViewById(R.id.minTemp1), findViewById(R.id.minTemp2), findViewById(R.id.minTemp3)};
        windDirectionTextViews = new TextView[]{findViewById(R.id.windDirection1), findViewById(R.id.windDirection2), findViewById(R.id.windDirection3)};
        windSpeedTextViews = new TextView[]{findViewById(R.id.windSpeed1), findViewById(R.id.windSpeed2), findViewById(R.id.windSpeed3)};
        visibilityTextViews = new TextView[]{findViewById(R.id.visibility1), findViewById(R.id.visibility2), findViewById(R.id.visibility3)};
        pressureTextViews = new TextView[]{findViewById(R.id.pressure1), findViewById(R.id.pressure2), findViewById(R.id.pressure3)};
        humidityTextViews = new TextView[]{findViewById(R.id.humidity1), findViewById(R.id.humidity2), findViewById(R.id.humidity3)};
        uvRiskTextViews = new TextView[]{findViewById(R.id.uvRisk1), findViewById(R.id.uvRisk2), findViewById(R.id.uvRisk3)};
        pollutionTextViews = new TextView[]{findViewById(R.id.pollution1), findViewById(R.id.pollution2), findViewById(R.id.pollution3)};
        sunriseTextViews = new TextView[]{findViewById(R.id.sunrise1), findViewById(R.id.sunrise2), findViewById(R.id.sunrise3)};
        sunsetTextViews = new TextView[]{findViewById(R.id.sunset1), findViewById(R.id.sunset2), findViewById(R.id.sunset3)};
    }

    private void displayWeatherInfo(List<WeatherInfo> weatherInfoList, int dayIndex) {
        // Check if weatherInfoList is not null and contains data
        if (weatherInfoList != null && !weatherInfoList.isEmpty()) {
            // Retrieve weather data for the specific day
            WeatherInfo weatherInfo = weatherInfoList.get(0); // Assuming only one entry for each day

            // Populate corresponding TextViews with weather information
            dayTextViews[dayIndex].setText("Day: " + weatherInfo.getDay());
            maxTempTextViews[dayIndex].setText("Maximum Temperature: " + weatherInfo.getMaxTemperature());
            minTempTextViews[dayIndex].setText("Minimum Temperature: " + weatherInfo.getMinTemperature());
            windDirectionTextViews[dayIndex].setText("Wind Direction: " + weatherInfo.getWindDirection());
            windSpeedTextViews[dayIndex].setText("Wind Speed: " + weatherInfo.getWindSpeed());
            visibilityTextViews[dayIndex].setText("Visibility: " + weatherInfo.getVisibility());
            pressureTextViews[dayIndex].setText("Pressure: " + weatherInfo.getPressure());
            humidityTextViews[dayIndex].setText("Humidity: " + weatherInfo.getHumidity());
            uvRiskTextViews[dayIndex].setText("UV Risk: " + weatherInfo.getUvRisk());
            pollutionTextViews[dayIndex].setText("Pollution: " + weatherInfo.getPollution());
            sunriseTextViews[dayIndex].setText("Sunrise: " + weatherInfo.getSunrise());
            sunsetTextViews[dayIndex].setText("Sunset: " + weatherInfo.getSunset());
        }
    }
}
