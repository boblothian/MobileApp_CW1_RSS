// Name                 Robert Lothian
// Student ID           S2225607
// Programme of Study   Computer Science
//

package com.example.lothian_robert_rlothi300;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class WeatherInfoFragment extends Fragment {

    private ImageView weatherImage;
    private TextView dayTextView;
    private TextView maxTempTextView;
    private TextView minTempTextView;
    private TextView windDirectionTextView;
    private TextView windSpeedTextView;
    private TextView visibilityTextView;
    private TextView pressureTextView;
    private TextView humidityTextView;
    private TextView uvRiskTextView;
    private TextView pollutionTextView;
    private TextView sunriseTextView;
    private TextView sunsetTextView;
    private String weatherCondition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_info_fragment, container, false);

        weatherImage = view.findViewById(R.id.weatherImage);
        dayTextView = view.findViewById(R.id.dayFrag);
        maxTempTextView = view.findViewById(R.id.maxTempFrag);
        minTempTextView = view.findViewById(R.id.minTempFrag);
        windDirectionTextView = view.findViewById(R.id.windDirectionFrag);
        windSpeedTextView = view.findViewById(R.id.windSpeedFrag);
        visibilityTextView = view.findViewById(R.id.visibilityFrag);
        pressureTextView = view.findViewById(R.id.pressureFrag);
        humidityTextView = view.findViewById(R.id.humidityFrag);
        uvRiskTextView = view.findViewById(R.id.uvRiskFrag);
        pollutionTextView = view.findViewById(R.id.pollutionFrag);
        sunriseTextView = view.findViewById(R.id.sunriseFrag);
        sunsetTextView = view.findViewById(R.id.sunsetFrag);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            WeatherInfo weatherInfo = (WeatherInfo) args.getSerializable("weatherInfo");
            if (weatherInfo != null) {
                updateWeatherInfo(weatherInfo);
                setImageForWeatherCondition(weatherInfo.getWeatherCondition());
            }
        }
    }

        void setImageForWeatherCondition(String weatherCondition) {
            if (weatherImage != null) {
                switch (weatherCondition) {
                    case "Sunny":
                    case "Clear Sky":
                        Glide.with(this).load(R.drawable.sunny_gif).into(weatherImage);
                        break;
                    case "Partly Cloudy":
                    case "Sunny Intervals":
                        Glide.with(this).load(R.drawable.sunny_intervals_gif).into(weatherImage);
                        break;
                    case "Light Cloud":
                    case "Thick Cloud":
                        Glide.with(this).load(R.drawable.cloud).into(weatherImage);
                        break;
                    case "Drizzle":
                    case "Light Rain":
                        Glide.with(this).load(R.drawable.light_rain_gif).into(weatherImage);
                        break;
                    case "Light Rain Showers":
                        Glide.with(this).load(R.drawable.light_rain_showers_gif).into(weatherImage);
                        break;
                    case "Rain":
                        Glide.with(this).load(R.drawable.rain_gif).into(weatherImage);
                        break;
                    case "Heavy Rain":
                        Glide.with(this).load(R.drawable.heavy_rain_gif).into(weatherImage);
                        break;
                    case "Thundery Showers":
                        Glide.with(this).load(R.drawable.lightning_gif).into(weatherImage);
                        break;
                    case "Snow":
                        Glide.with(this).load(R.drawable.snow_gif).into(weatherImage);
                        break;
                    case "Not available":
                        weatherImage.setImageResource(R.drawable.default_image);
                    default:
                        weatherImage.setImageResource(R.drawable.default_image);
                        break;
                }
            }
        }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String condition) {
        // Set the weather condition in the fragment
        weatherCondition = condition;
    }

    private void updateWeatherInfo(WeatherInfo weatherInfo) {
        // Update the TextViews with weather information
        dayTextView.setText(weatherInfo.getDate());
        maxTempTextView.setText(String.format("Maximum Temperature: %s", weatherInfo.getMaxTemperature()));
        minTempTextView.setText(String.format("Minimum Temperature: %s", weatherInfo.getMinTemperature()));
        windDirectionTextView.setText(String.format("Wind Direction: %s", weatherInfo.getWindDirection()));
        windSpeedTextView.setText(String.format("Wind Speed: %s", weatherInfo.getWindSpeed()));
        visibilityTextView.setText(String.format("Visibility: %s", weatherInfo.getVisibility()));
        pressureTextView.setText(String.format("Pressure: %s", weatherInfo.getPressure()));
        humidityTextView.setText(String.format("Humidity: %s", weatherInfo.getHumidity()));
        uvRiskTextView.setText(String.format("UV Risk: %s", weatherInfo.getUvRisk()));
        pollutionTextView.setText(String.format("Pollution: %s", weatherInfo.getPollution()));
        sunriseTextView.setText(String.format("Sunrise: %s", weatherInfo.getSunrise()));
        sunsetTextView.setText(String.format("Sunset: %s", weatherInfo.getSunset()));
    }
}