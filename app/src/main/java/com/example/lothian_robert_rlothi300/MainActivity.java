package com.example.lothian_robert_rlothi300;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
//TODO Implement double tap to cycle locations, animate google map to new location as a transition (This can be used to query the RSS feed). Use viewFlipper -> Place -> Map transition -> Place etc. etc.
//TODO accessibility options, dark mode font size, update feed
public class MainActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView weatherDataDisplay;
    private Button startButton;

    private Button threeDayForecast;

    private Spinner spinner;
    private ArrayList<WeatherInfo> weatherInfoList1 = new ArrayList<>();
    private ArrayList<WeatherInfo> weatherInfoList2 = new ArrayList<>();
    private ArrayList<WeatherInfo> weatherInfoList3 = new ArrayList<>();
    private static String[] paths = {"Glasgow", "London", "New York", "Oman", "Mauritius", "Bangladesh"}; // this displays names in spinner
    public String locationID = "2648579"; //sets Glasgow as default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherDataDisplay = findViewById(R.id.weatherData);
        //This button starts the Parser
        startButton = findViewById(R.id.startButton);
        //Hide the button when the activity is created
        startButton.setVisibility(View.GONE);
        startButton.setOnClickListener(this);

        //
        threeDayForecast = findViewById(R.id.threeDayButton);
        threeDayForecast.setOnClickListener(this);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    //When an option on the spinner is picked it changes the locationID and acts as an onClick
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                //Glasgow
                locationID = "2648579";
                break;
            case 1:
                //London
                locationID = "2643743";
                break;
            case 2:
                //New York
                locationID = "5128581";
                break;
            case 3:
                //Oman
                locationID = "287286";
                break;
            case 4:
                //Mauritius
                locationID = "934154";
                break;
            case 5:
                //Bangladesh
                locationID = "1185241";
                break;
        }
        startProgress();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //nothing happens
    }

    public void onClick(View aview) {

        Intent intent = new Intent(MainActivity.this, ThreeDayForecastActivity.class);
        intent.putExtra("weatherInfo1", weatherInfoList1.toArray(new WeatherInfo[0]));
        intent.putExtra("weatherInfo2", weatherInfoList2.toArray(new WeatherInfo[0]));
        intent.putExtra("weatherInfo3", weatherInfoList3.toArray(new WeatherInfo[0]));
        startActivity(intent);
    }

    public void startProgress() {
        String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationID + "/"; //calls the RSS feed with location modifier chosen by spinner
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
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

            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            Log.e("MyTag", "Xml Data" + result);

            parseData(result);
        }
    }

    //this is where the parser starts and finds the tag <item> where it creates a new WeatherInfo object
    // and then the tag <description> where it sends that data to another parser in the parseDescription method
    // Update the weatherDataDisplay TextView with the formatted weather data
    private void parseData(String dataToParse) {
        //this clears the default data so new location data can be added to the list
        weatherInfoList1.clear();
        weatherInfoList2.clear();
        weatherInfoList3.clear();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(dataToParse));

            int eventType = parser.getEventType();

            String tagName = null;
            WeatherInfo currentWeatherInfo = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("item")) {
                            currentWeatherInfo = new WeatherInfo();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (tagName != null && currentWeatherInfo != null) {
                            switch (tagName) {
                                case "description":
                                    parseDescription(text, currentWeatherInfo);
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("item")) {
                            if (currentWeatherInfo != null) {
                                if (weatherInfoList1.size() == 0) {
                                    weatherInfoList1.add(currentWeatherInfo);
                                } else if (weatherInfoList2.size() == 0) {
                                    weatherInfoList2.add(currentWeatherInfo);
                                } else {
                                    weatherInfoList3.add(currentWeatherInfo);
                                }
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    //this method parses through the Description tag of the RSS feed, it splits the data by category
    //by looking for a comma space ", " and then into key values by identifying when a colon space ": " occurs
    private void parseDescription(String description, WeatherInfo weatherInfo) {
        Log.e("Description", description);
        String[] parts = description.split(", ");

        //This identifies the Key Values and splits them
        for (String part : parts) {
            String[] keyValue = part.split(": ");
            // this checks to ensure only the keypair only has 2 parts (to match how the RSS feed is formatted)
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                //this switch statement assigns the value just separated to the setMaxTemperature variable in the WeatherInfo object
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
}