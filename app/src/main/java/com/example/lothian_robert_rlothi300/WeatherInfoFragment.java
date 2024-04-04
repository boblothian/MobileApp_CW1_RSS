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
                        weatherImage.setImageResource(R.drawable.sunny);
                        break;
                    case "Partly Cloudy":
                    case "Sunny Intervals":
                        weatherImage.setImageResource(R.drawable.sunny_intervals);
                        break;
                    case "Light Cloud":
                    case "Thick Cloud":
                        weatherImage.setImageResource(R.drawable.light_cloud);
                        break;
                    case "Drizzle":
                    case "Light Rain":
                    case "Light Rain Showers":
                        weatherImage.setImageResource(R.drawable.light_rain);
                        break;
                    case "Rain":
                        weatherImage.setImageResource(R.drawable.rain);
                        break;
                    case "Heavy Rain":
                        weatherImage.setImageResource(R.drawable.heavy_rain);
                        break;
                    case "Thundery Showers":
                        weatherImage.setImageResource(R.drawable.thundery_showers);
                        break;
                    case "Snow":
                        weatherImage.setImageResource(R.drawable.snow);
                        break;
                    case "Not available":
                        weatherImage.setImageResource(R.drawable.default_image);
                    default:
                        weatherImage.setImageResource(R.drawable.default_image);
                        break;
                }
            }
        }

    private void updateWeatherInfo(WeatherInfo weatherInfo) {
        // Update the TextViews with weather information
        dayTextView.setText(weatherInfo.getDate());
        maxTempTextView.setText("Maximum Temperature: " + weatherInfo.getMaxTemperature());
        minTempTextView.setText("Minimum Temperature: " + weatherInfo.getMinTemperature());
        windDirectionTextView.setText("Wind Direction: " + weatherInfo.getWindDirection());
        windSpeedTextView.setText("Wind Speed: " + weatherInfo.getWindSpeed());
        visibilityTextView.setText("Visibility: " + weatherInfo.getVisibility());
        pressureTextView.setText("Pressure: " + weatherInfo.getPressure());
        humidityTextView.setText("Humidity: " + weatherInfo.getHumidity());
        uvRiskTextView.setText("UV Risk: " + weatherInfo.getUvRisk());
        pollutionTextView.setText("Pollution: " + weatherInfo.getPollution());
        sunriseTextView.setText("Sunrise: " + weatherInfo.getSunrise());
        sunsetTextView.setText("Sunset: " + weatherInfo.getSunset());
    }
}
