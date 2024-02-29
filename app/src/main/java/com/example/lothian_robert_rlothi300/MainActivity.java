package com.example.lothian_robert_rlothi300;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private TextView rawDataDisplay;
    private TextView weatherDataDisplay;
    private Button startButton;
    private String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643123";

    public WeatherInfo weatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = findViewById(R.id.rawDataDisplay);
        weatherDataDisplay = findViewById(R.id.weatherData);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        // More Code goes here
    }

    public void onClick(View aview) {
        startProgress();
    }

    public void startProgress() {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";
            String result = ""; // Initialize result

            Log.e("MyTag","in run");

            try {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            Log.e("MyTag","Xml Data"+ result);

            parseData(result);
        }
    }

    private void parseData(String dataToParse) {
        List<WeatherInfo> weatherInfoList = new ArrayList<>();


        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(dataToParse));

            int eventType = parser.getEventType();

            String tagName = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("item")) {
                            weatherInfo = new WeatherInfo();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (tagName != null && weatherInfo != null) {
                            switch (tagName) {
                                case "description":
                                    parseDescription(text, weatherInfo);
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }


            if (weatherInfo != null) {
                weatherInfoList.add(weatherInfo);
            }

            // Update UI with parsed weather information
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Update UI components here
                    displayWeatherInfo(weatherInfoList);
                }
            });

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private void parseDescription(String description, WeatherInfo weatherInfo){
        Log.e("Description", description);
        String[] parts = description.split(", ");

        for (String part : parts) {
            String[] keyValue = part.split(": ");
            if (keyValue.length ==2) {
                String key = keyValue[0];
                String value = keyValue[1];

                switch (key) {
                    case "Maximum Temperature":
                        weatherInfo.setMaxTemperature(value);
                        break;
                    case "Minimum Temperature":
                        weatherInfo.setMinTemperature(value);
                        break;
                    case "Wind Direction":
                        weatherInfo.setWindDirection(value);
                        break;
                    case "Wind Speed":
                        weatherInfo.setWindSpeed(value);
                        break;
                    case "Visibility":
                        weatherInfo.setVisibility(value);
                        break;
                    case "Pressure":
                        weatherInfo.setPressure(value);
                        break;
                    case "Humidity":
                        weatherInfo.setHumidity(value);
                        break;
                    case "UV Risk":
                        weatherInfo.setUvRisk(value);
                        break;
                    case "Pollution":
                        weatherInfo.setPollution(value);
                        break;
                    case "Sunrise":
                        weatherInfo.setSunrise(value);
                        break;
                    case "Sunset":
                        weatherInfo.setSunset(value);
                        break;
                    default:
                        // Handle unknown key or skip
                        break;
                }
            }
        }
    }

    private void displayWeatherInfo(List<WeatherInfo> weatherInfoList) {
        StringBuilder weatherData = new StringBuilder();

        // Append weather information for each WeatherInfo object in the list
        for (WeatherInfo weatherInfo : weatherInfoList) {
            // Example: Append max and min temperature
            Log.d("WeatherInfo", "Max Temperature: " + weatherInfo.getMaxTemperature());
            weatherData.append("Max Temperature: ").append(weatherInfo.getMaxTemperature()).append("\n");
            weatherData.append("Min Temperature: ").append(weatherInfo.getMinTemperature()).append("\n");
            weatherData.append("Wind Direction: ").append(weatherInfo.getWindDirection()).append("\n");
            weatherData.append("Wind Speed: ").append(weatherInfo.getWindSpeed()).append("\n");
            weatherData.append("Visibility: ").append(weatherInfo.getVisibility()).append("\n");
            weatherData.append("Pressure: ").append(weatherInfo.getPressure()).append("\n");
            weatherData.append("Humidity: ").append(weatherInfo.getHumidity()).append("\n");
            weatherData.append("UV Risk: ").append(weatherInfo.getUvRisk()).append("\n");
            weatherData.append("Pollution: ").append(weatherInfo.getPollution()).append("\n");
            weatherData.append("Sunrise: ").append(weatherInfo.getSunrise()).append("\n");
            weatherData.append("Sunset: ").append(weatherInfo.getSunset()).append("\n");
            // Append other weather information as needed
        }

        // Update the weatherDataDisplay TextView with the formatted weather data
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weatherDataDisplay.setText(weatherData.toString());
            }
        });
    }
}
