package com.example.lothian_robert_rlothi300;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{
    private TextView location;
    private TextView temperature;
    private TextView windDirection;
    private TextView windSpeed;
    private TextView humidity;
    private TextView pressure;
    private TextView visibility;
    private ImageView weatherImg;
    private ImageView windIcon;
    private Button startButton;
    private GoogleMap map;
    private MapView mapView;
    private Button threeDayForecast;

    private Spinner spinner;
    private ArrayList<WeatherInfo> weatherInfoList1 = new ArrayList<>();
    private ArrayList<WeatherInfo> weatherInfoList2 = new ArrayList<>();
    private ArrayList<WeatherInfo> weatherInfoList3 = new ArrayList<>();
    private static String[] paths = {"Glasgow", "London", "New York", "Oman", "Mauritius", "Bangladesh"}; // this displays names in spinner
    public String locationID = "2648579"; //sets Glasgow as default

    private double latitude;
    private double longitude;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextView elements
        location = findViewById(R.id.location);
        temperature = findViewById(R.id.temperature);
        windDirection = findViewById(R.id.windDirection);
        windSpeed = findViewById(R.id.windSpeed);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        visibility = findViewById(R.id.visibility);

        //initialize ImageViews
        weatherImg =findViewById(R.id.weatherImg);
        windIcon = findViewById(R.id.windDirectionIcon);

        // Initialize Spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Initialize Buttons
        startButton = findViewById(R.id.startButton);
        startButton.setVisibility(View.GONE); // Hide the button initially
        startButton.setOnClickListener(this);

        threeDayForecast = findViewById(R.id.threeDayButton);
        threeDayForecast.setOnClickListener(this);

        latitude = 55.8617;
        longitude = -4.2583;


        //map
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Start progress to fetch and populate weather data
        startProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



    //When an option on the spinner is picked it changes the locationID and acts as an onClick
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        String selectedLocation = paths[position]; // Get the selected location from the spinner
        location.setText(selectedLocation);
        String location = "";
        double latitude = 0;
        double longitude = 0;
        switch (position) {
            case 0:
                //Glasgow
                location = "Glasgow";
                locationID = "2648579";
                latitude = 55.8617;
                longitude = -4.2583;
                break;
            case 1:
                //London
                location = "London";
                locationID = "8224580";
                latitude = 51.5072;
                longitude = 0.1276;
                break;
            case 2:
                //New York
                location = "New York";
                locationID = "5128581";
                latitude = 40.7128;
                longitude = -74.0060;
                break;
            case 3:
                //Oman
                location = "Oman";
                locationID = "287286";
                latitude = 23.6032;
                longitude = 58.4471;
                break;
            case 4:
                //Mauritius
                location = "Mauritius";
                locationID = "934154";
                latitude = -20.1587;
                longitude = 57.5033;
                break;
            case 5:
                //Bangladesh
                location = "Bangladesh";
                locationID = "1185241";
                latitude = 23.6850;
                longitude = 90.3563;
                break;
        }

        if (map != null) {
            LatLng myLocation = new LatLng(latitude, longitude);
            map.clear(); // Clear existing markers
            map.addMarker(new MarkerOptions().position(myLocation).title("You are here"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12f)); // Zoom level can be adjusted as needed
        }

        // Clear the lists before fetching new data
        weatherInfoList1.clear();
        weatherInfoList2.clear();
        weatherInfoList3.clear();

        startProgress();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Now you can use the map object to add markers and perform other map-related operations
        // For example:
        if (map != null) {
            LatLng location = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(location).title("You are here"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f)); // Zoom level can be adjusted as needed
        }
    }


    public void onNothingSelected(AdapterView<?> parent) {
        //nothing happens
    }

    public void onClick(View aview) {
        if (aview.getId() == R.id.startButton) {
            // Fetch current weather data when the start button is clicked
            String urlWeatherNow = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + locationID + "/";
            Task task = new Task(urlWeatherNow, "weatherNow");
            new Thread(task).start();
        } else if (aview.getId() == R.id.threeDayButton) {
            // Start ThreeDayForecastActivity when the Three Day Forecast button is clicked
            Intent intent = new Intent(MainActivity.this, ThreeDayForecastActivity.class);
            // Pass the location ID to the ThreeDayForecastActivity
            intent.putExtra("locationID", locationID);
            intent.putExtra("weatherInfo1", weatherInfoList1.toArray(new WeatherInfo[0]));
            intent.putExtra("weatherInfo2", weatherInfoList2.toArray(new WeatherInfo[0]));
            intent.putExtra("weatherInfo3", weatherInfoList3.toArray(new WeatherInfo[0]));
            String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationID + "/";
            // Create a new Task object with the appropriate feedType
            Task task = new Task(urlSource, "threeDayForecast");
            // Start the thread
            new Thread(task).start();
            startActivity(intent);
        }
    }


    public void startProgress() {

        String urlWeatherNow = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/"+ locationID;
        String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationID;
        new Thread(new Task(urlWeatherNow, "weatherNow")).start();
        new Thread(new Task(urlSource, "threeDayForecast")).start();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Handle your navigation actions here
        // For example:
        if (id == R.id.nav_item1) {
            // Perform the same action as the Three Day Button click
            Intent intent = new Intent(MainActivity.this, ThreeDayForecastActivity.class);
            intent.putExtra("weatherInfo1", weatherInfoList1.toArray(new WeatherInfo[0]));
            intent.putExtra("weatherInfo2", weatherInfoList2.toArray(new WeatherInfo[0]));
            intent.putExtra("weatherInfo3", weatherInfoList3.toArray(new WeatherInfo[0]));
            String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationID + "/";
            // Create a new Task object with the appropriate feedType
            Task task = new Task(urlSource, "threeDayForecast");
            // Start the thread
            new Thread(task).start();
            startActivity(intent);
        } else if (id == R.id.nav_item2) {
            // Cycle through the spinner options
            int currentPosition = spinner.getSelectedItemPosition();
            int newPosition = (currentPosition + 1) % paths.length;
            spinner.setSelection(newPosition);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.

    private class Task implements Runnable {
        private String url;
        private String feedType;
        public Task(String aurl, String feedType) {
            url = aurl;
            // Assign "weatherNow" as the default feedType if not provided
            this.feedType = feedType;
        }

        public String getFeedType() {
            return feedType;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in;
            String inputLine;
            String result = ""; // Initialize result
            Task task = new Task(url, feedType);

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

            parseData(result, task);
        }
    }

    //this is where the parser starts and finds the tag <item> where it creates a new WeatherInfo object
    // and then the tag <description> where it sends that data to another parser in the parseDescription method
    // Update the weatherDataDisplay TextView with the formatted weather data
    private void parseData(String dataToParse, Task task) {
        String feedType = task.getFeedType(); // Retrieve feedType from the Task instance

        switch (feedType) {
            case "weatherNow":
                parseWeatherNow(dataToParse);
                break;
            case "threeDayForecast":
                parseThreeDayForecast(dataToParse);
                break;
            default:
                break;
        }
    }

    private ArrayList<WeatherInfo> currentWeatherInfoList = new ArrayList<>();

    private void parseWeatherNow(String dataToParse) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(dataToParse));

            int eventType = parser.getEventType();

            String tagName = null;
            WeatherInfo weatherInfo = new WeatherInfo(); // Create a new WeatherInfo object for current weather

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (tagName != null) {
                            switch (tagName) {
                                case "title":
                                    parseTitle(text, weatherInfo);
                                    break;
                                case "description":
                                    parseDescriptionWeatherNow(text, weatherInfo);
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("item")) {
                            if (weatherInfo != null) {
                                currentWeatherInfoList.add(weatherInfo); // Add the parsed weather info to the list
                                updateWeatherViews(weatherInfo); // Update UI with the latest weather info
                                // Reset weatherInfo after processing
                                weatherInfo = new WeatherInfo(); // Create a new WeatherInfo object for the next weather info
                            }
                        }
                        break;
                }
                eventType = parser.next();

                setImageForWindIcon(weatherInfo);
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
    private void parseThreeDayForecast(String dataToParse) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(dataToParse));

            int eventType = parser.getEventType();

            String tagName = null;
            WeatherInfo currentWeatherInfo = null;
            int dayCount = 0; // Variable to keep track of the day count

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
                                case "title":
                                    parseTitle(text, currentWeatherInfo);
                                    break;
                                case "description":
                                    parseDescriptionThreeDayForecast(text, currentWeatherInfo);
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("item")) {
                            if (currentWeatherInfo != null) {
                                setImageForWeatherCondition(currentWeatherInfo);
                                // Add the currentWeatherInfo to the appropriate list based on day count
                                if (dayCount == 0) {
                                    weatherInfoList1.add(currentWeatherInfo);
                                } else if (dayCount == 1) {
                                    weatherInfoList2.add(currentWeatherInfo);
                                } else if (dayCount == 2) {
                                    weatherInfoList3.add(currentWeatherInfo);
                                }
                                currentWeatherInfo = null; // Reset currentWeatherInfo after processing
                                dayCount++; // Increment day count for the next iteration
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


    private void parseTitle(String title, WeatherInfo weatherInfo) {
        if (title != null && !title.isEmpty()) {
            // Split the title by colon (":") to separate different parts
            String[] parts = title.split(":");

            // Ensure there is at least one part
            if (parts.length > 1) {
                // Extract the first part, trim leading/trailing whitespaces, and set as the day
                String day = parts[0].trim();

                // Set the day in the WeatherInfo object
                weatherInfo.setDay(day);
                Log.d("ParseTitle", "Day extracted: " + day);

                // Extract the weather condition from the title
                String weatherAndTemp = parts[1].trim(); // Extract the weather condition after the colon
                // Split the weatherAndTemp by comma (",") to separate weather condition and minimum temperature
                String[] weatherParts = weatherAndTemp.split(",");
                if (weatherParts.length > 0) {
                    String weatherCondition = weatherParts[0].trim(); // Extract the weather condition
                    weatherInfo.setWeatherCondition(weatherCondition);
                    Log.d("ParseTitle", "Weather Condition extracted: " + weatherCondition);

                    // Set the image for the weather condition
                    setImageForWeatherCondition(weatherInfo);

                } else {
                    Log.d("ParseTitle", "Invalid weather format: " + weatherAndTemp);
                }
            } else {
                Log.d("ParseTitle", "Invalid title format: " + title);
            }
        } else {
            Log.d("ParseTitle", "Empty or null title string provided.");
        }
    }

    private void setImageForWindIcon(WeatherInfo weatherInfo) {
        String direction = weatherInfo.getWindDirection();

        runOnUiThread(() -> {
            Log.d("set wind direction", "Clearing wind image");
            if (windIcon != null && direction != null) { // Check if windIcon and direction are not null
                windIcon.setImageDrawable(null);

                int rotation = 0; // Default rotation value

                switch (direction) {
                    case "Northerly":
                        rotation = 0;
                        break;
                    case "North North Easterly":
                        rotation = 23;
                        break;
                    case "North Easterly":
                        rotation = 45;
                        break;
                    case "East North Easterly":
                        rotation = 68;
                        break;
                    case "Easterly":
                        rotation = 90;
                        break;
                    case "East South Easterly":
                        rotation = 113;
                        break;
                    case "South Easterly":
                        rotation = 135;
                        break;
                    case "South South Easterly":
                        rotation = 158;
                        break;
                    case "Southerly":
                        rotation = 180;
                        break;
                    case "South South Westerly":
                        rotation = 203;
                        break;
                    case "South Westerly":
                        rotation = 225;
                        break;
                    case "West South Westerly":
                        rotation = 248;
                        break;
                    case "Westerly":
                        rotation = 270;
                        break;
                    case "West North Westerly":
                        rotation = 293;
                        break;
                    case "North Westerly":
                        rotation = 315;
                        break;
                    case "North North Westerly":
                        rotation = 338;
                        break;
                    default:
                        Log.e("setImageForWindIcon", "Unknown direction: " + direction);
                        break;
                }

                // Set the image resource for the wind icon
                windIcon.setImageResource(R.drawable.wind_icon);
                // Apply rotation to the wind icon
                windIcon.setRotation(rotation);
            } else {
                Log.e("setImageForWindIcon", "windIcon is null or direction is null");
            }
        });
    }


    private void setImageForWeatherCondition(WeatherInfo weatherInfo) {
        // Fetching condition from WeatherInfo object
        String condition = weatherInfo.getWeatherCondition();

        String finalCondition = condition;
        runOnUiThread(() -> {

            // Clear the ImageView only once before setting the image
            Log.d("setImageForWeatherCondition", "Clearing ImageView");
            weatherImg.setImageDrawable(null);

            switch (finalCondition) {
                case "Sunny":
                    weatherImg.setImageResource(R.drawable.sunny);
                    Log.d("setImageForWeatherCondition", "Setting image for Sunny condition");
                    break;
                case "Partly Cloudy":
                case "Sunny Intervals":
                    weatherImg.setImageResource(R.drawable.sunny_intervals);
                    Log.d("setImageForWeatherCondition", "Setting image for Sunny Intervals condition");
                    break;
                case "Light Cloud":
                    weatherImg.setImageResource(R.drawable.light_cloud);
                    break;
                case "Light Rain":
                case "Light Rain Showers":
                    weatherImg.setImageResource(R.drawable.light_rain);
                    Log.d("setImageForWeatherCondition", "Setting image for Light Rain condition");
                    break;
                case "Rain":
                    weatherImg.setImageResource(R.drawable.rain);
                    Log.d("setImageForWeatherCondition", "Setting image for Rain condition");
                    break;
                case "Heavy Rain":
                    weatherImg.setImageResource(R.drawable.heavy_rain);
                    Log.d("setImageForWeatherCondition", "Setting image for Heavy Rain condition");
                    break;
                case "Thundery Showers":
                    weatherImg.setImageResource((R.drawable.thundery_showers));
                    break;
                case "Snow":
                    weatherImg.setImageResource(R.drawable.snow);
                    Log.d("setImageForWeatherCondition", "Setting image for Snow condition");
                    break;
                default:
                    // Set the default image if the condition is not recognized
                    weatherImg.setImageResource(R.drawable.default_image);
                    Log.d("setImageForWeatherCondition", "Setting default image as condition not recognized");
                    break;
            }
        });
    }



    private void updateWeatherViews(WeatherInfo weatherInfo) {
        if (weatherInfo != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    temperature.setText(weatherInfo.getCurrentTemperature());
                    windDirection.setText("Wind Direction: " + weatherInfo.getWindDirection());
                    windSpeed.setText("Wind Speed: " + weatherInfo.getWindSpeed());
                    humidity.setText("Humidity: " + weatherInfo.getHumidity());
                    pressure.setText("Pressure: " + weatherInfo.getPressure());
                    visibility.setText("Visibility: " + weatherInfo.getVisibility());
                }
            });
        } else {
            Log.e("updateWeatherViews", "WeatherInfo object is null");
            // Handle null WeatherInfo object, such as showing an error message or retrying data retrieval
        }
    }

    private void parseDescriptionWeatherNow(String description, WeatherInfo weatherInfo) {
        // Split the description by comma and space to separate key-value pairs
        String[] parts = description.split(", ");

        // Loop through each key-value pair
        for (String part : parts) {
            // Split the key-value pair by colon and space to separate the key and value
            String[] keyValue = part.split(": ");

            // Ensure the key-value pair is valid
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                Log.d("WeatherNowParsing", "Key: " + key + ", Value: " + value);

                // Parse each key-value pair and set the corresponding fields in the WeatherInfo object
                switch (key) {
                    case "Temperature":
                        weatherInfo.setCurrentTemperature(value);
                        break;
                    case "Wind Direction":
                        weatherInfo.setWindDirection(value);
                        break;
                    case "Wind Speed":
                        weatherInfo.setWindSpeed(value);
                        break;
                    case "Humidity":
                        weatherInfo.setHumidity(value);
                        break;
                    case "Pressure":
                        weatherInfo.setPressure(value);
                        break;
                    case "Visibility":
                        weatherInfo.setVisibility(value);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //this method parses through the Description tag of the RSS feed, it splits the data by category
    //by looking for a comma space ", " and then into key values by identifying when a colon space ": " occurs
    private void parseDescriptionThreeDayForecast(String description, WeatherInfo weatherInfo) {
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