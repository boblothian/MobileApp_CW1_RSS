// Name                 Robert Lothian
// Student ID           S2225607
// Programme of Study   Computer Science
//

package com.example.lothian_robert_rlothi300;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MainActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{
    private TextView location;
    private TextView temperature;
    private TextView windDirection;
    private TextView windSpeed;
    private TextView humidity;
    private TextView pressure;
    private TextView date;
    private TextView visibility;
    private ImageView weatherImg;
    private ImageView windIcon;
    private GoogleMap map;
    private MapView mapView;

    private Spinner spinner;
    private final ArrayList<WeatherInfo> currentWeatherInfoList = new ArrayList<>();
    private final ArrayList<WeatherInfo> weatherInfoList1 = new ArrayList<>();
    private final ArrayList<WeatherInfo> weatherInfoList2 = new ArrayList<>();
    private final ArrayList<WeatherInfo> weatherInfoList3 = new ArrayList<>();
    private static final String[] paths = {"Glasgow", "London", "New York", "Oman", "Mauritius", "Bangladesh"}; // this displays names in spinner
    public String locationID = "2648579"; //sets Glasgow as default

    private double latitude;
    private double longitude;

    private DrawerLayout drawer;

    Handler handler = new Handler();
    Runnable refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextView elements
        date = findViewById(R.id.date);
        location = findViewById(R.id.location);
        temperature = findViewById(R.id.temperature);
        windDirection = findViewById(R.id.windDirection);
        windSpeed = findViewById(R.id.windSpeed);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        visibility = findViewById(R.id.visibility);

        // Initialize ImageViews
        weatherImg =findViewById(R.id.weatherImg);
        windIcon = findViewById(R.id.windDirectionIcon);

        // Initialize Spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Initialize Buttons
        Button startButton = findViewById(R.id.startButton);
        startButton.setVisibility(View.GONE); // Hide the button initially
        startButton.setOnClickListener(this);

        Button threeDayForecast = findViewById(R.id.threeDayButton);
        threeDayForecast.setOnClickListener(this);

        // Set default Lat and Long
        latitude = 55.8617;
        longitude = -4.2583;


        // Initialize Map
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize toolbar
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Start progress to fetch and populate weather data
        //Auto refresh both the feeds every 15 minutes
        refresh = () -> {
            startProgress();
            handler.postDelayed(refresh, 900000);
        };
        handler.post(refresh);
    }

    // Map View functions
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

    // When an option on the spinner is picked it changes the locationID and acts as an onClick
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        String selectedLocation = paths[position]; // Get the selected location from the spinner
        location.setText(selectedLocation);
        double latitude = 0;
        double longitude = 0;
        switch (position) {
            case 0:
                //Glasgow
                locationID = "2648579";
                latitude = 55.8617;
                longitude = -4.2583;
                break;
            case 1:
                //London
                locationID = "2651500";
                latitude = 51.5072 ;
                longitude = 0.1276;
                break;
            case 2:
                //New York
                locationID = "5128581";
                latitude = 40.7128;
                longitude = -74.0060;
                break;
            case 3:
                //Oman
                locationID = "287286";
                latitude = 23.6032;
                longitude = 58.4471;
                break;
            case 4:
                //Mauritius
                locationID = "934154";
                latitude = -20.1587;
                longitude = 57.5033;
                break;
            case 5:
                //Bangladesh
                locationID = "1185241";
                latitude = 24.1190;
                longitude = 90.2524;
                break;
        }

        // This sets latitude and longitude and zooms the map
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Default map
        LatLng location = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(location).title("You are here"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Nothing happens
    }

    public void onClick(View aview) {
        if (aview.getId() == R.id.startButton) {
            // Fetch current weather, this is outdated, should remove
            String urlWeatherNow = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + locationID + "/";
            Task task = new Task(urlWeatherNow, "weatherNow"); // Pass currentWeatherInfoList
            new Thread(task).start();
        } else if (aview.getId() == R.id.threeDayButton) {
            // Start ThreeDayForecastActivity when the Three Day Forecast button is clicked
            Intent intent = new Intent(MainActivity.this, ThreeDayForecastActivity.class);
            // Pass the location ID and weather info to the ThreeDayForecastActivity
            intent.putExtra("locationID", locationID);
            intent.putExtra("weatherInfo1", weatherInfoList1.toArray(new WeatherInfo[0]));
            intent.putExtra("weatherInfo2", weatherInfoList2.toArray(new WeatherInfo[0]));
            intent.putExtra("weatherInfo3", weatherInfoList3.toArray(new WeatherInfo[0]));
            String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationID + "/";
            // Create a new Task object with the appropriate feedType
            Task task = new Task(urlSource, "threeDayForecast"); // Pass currentWeatherInfoList
            // Start the thread
            new Thread(task).start();
            startActivity(intent);
        }
    }

    public void startProgress() {
        // Defines the URL to parse depending on location, starts a thread depending if it is weather now or 3 day
        String urlWeatherNow = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/"+ locationID;
        String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationID;
        new Thread(new Task(urlWeatherNow, "weatherNow")).start();
        new Thread(new Task(urlSource, "threeDayForecast")).start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    //Nav bar logic
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view items
        int id = item.getItemId();

        if (id == R.id.nav_item1) {
            // Perform the same action as the Three Day Button click
            Intent intent = new Intent(MainActivity.this, ThreeDayForecastActivity.class);
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

        if (id == R.id.nav_item2) {
            // Cycle through the spinner options
            int currentPosition = spinner.getSelectedItemPosition();
            int newPosition = (currentPosition + 1) % paths.length;
            spinner.setSelection(newPosition);
        }

        else if (id == R.id.nav_item3){
            // This refreshes the feed.
            startProgress();
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

    // This task determines what feed type to parse and runs parsing logic
    private class Task implements Runnable {
        private final String url;
        private final String feedType;

        // Constructor accepting feed type
        public Task(String aurl, String feedType) {
            url = aurl;
            this.feedType = feedType; // Assign feed type
        }

        // Getter for feed type
        public String getFeedType() {
            return feedType;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in;
            String inputLine;
            StringBuilder result = new StringBuilder(); // Initialize result
            Task task = new Task(url, feedType);

            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                    Log.e("MyTag", inputLine);
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            Log.e("MyTag", "Xml Data" + result);

            parseData(result.toString(), task);
        }
    }

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
                                    parseTitleForWeatherNow(text, weatherInfo);
                                    break;
                                case "description":
                                    parseDescriptionWeatherNow(text, weatherInfo);
                                    break;
                                case "pubDate":
                                    // Parse the pubDate to get the current day and date
                                    parsePubDate(text, weatherInfo);
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("item")) {
                            currentWeatherInfoList.add(weatherInfo); // Add the parsed weather info to the list
                            updateWeatherViews(weatherInfo); // Update UI with the latest weather info
                            // Reset weatherInfo after processing
                            weatherInfo = new WeatherInfo(); // Create a new WeatherInfo object for the next weather info
                        }
                        break;
                }
                eventType = parser.next();

                setImageForWindIcon(weatherInfo);
                setImageForWeatherCondition(weatherInfo);
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to parse the pubDate and extract current day and date
    private void parsePubDate(String pubDate, WeatherInfo weatherInfo) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            Date date = inputFormat.parse(pubDate);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            assert date != null;
            String day = dayFormat.format(date); // Get the current day

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            String dateString = dateFormat.format(date); // Get the current date

            // Set the current day and date in the WeatherInfo object
            weatherInfo.setDay();
            weatherInfo.setDate(dateString);

            // Return the formatted date
        } catch (ParseException e) {
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
            int dayCount = 0;

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
                                    parseTitleForThreeDayForecast(text, currentWeatherInfo);

                                    break;
                                case "description":
                                    parseDescriptionThreeDayForecast(text, currentWeatherInfo);
                                    break;
                                case "pubDate":
                                    // Parse the pubDate to get the current day and date
                                    parsePubDate(text, currentWeatherInfo);
                                    // Increment the pub date for the next day in the forecast
                                    incrementPubDate(currentWeatherInfo, dayCount);
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equalsIgnoreCase("item")) {
                            if (currentWeatherInfo != null) {

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

    private void incrementPubDate(WeatherInfo currentWeatherInfo, int dayCount) {
        // Increment the date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, dayCount);

        // Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH);
        String forecastDate = sdf.format(calendar.getTime());

        // Set the forecasted date
        currentWeatherInfo.setDate(forecastDate);
    }

    private void parseTitleForWeatherNow(String title, WeatherInfo weatherInfo) {
        if (title != null && !title.isEmpty()) {

            String[] parts = title.split(": ");
            if (parts.length > 1){
                String titleAfterTime = parts[1].trim();
                Log.d("CONDITION","CONDITION AFTER TIME IS "+ titleAfterTime);

                String[] currentConditionPart = titleAfterTime.split(",");
                String currentCondition = currentConditionPart[0].trim();
                Log.d("CONDITION","CONDITION IS "+ currentCondition);

                weatherInfo.setCurrentCondition(currentCondition);
            }

            else {
                Log.e("Parse Title Current","Could not get the current weather");
            }



        } else {
            Log.d("ParseTitleCurrent", "Empty or null title string provided.");
        }
    }

    private void parseTitleForThreeDayForecast(String title, WeatherInfo weatherInfo) {
        if (title != null) {
            // Split the title based on ":"
            String[] parts = title.split(":");

            // Ensure there is at least one part
            if (parts.length > 1) {
                String day = parts[0].trim();

                // Set the day in the WeatherInfo object
                weatherInfo.setDay();
                Log.d("ParseTitle3Day", "Day extracted: " + day);

                // Extract the weather condition
                String weatherAndTemp = parts[1].trim();
                String[] weatherParts = weatherAndTemp.split(",");
                if (weatherParts.length > 0) {
                    String weatherCondition = weatherParts[0].trim(); // Extract the weather condition
                    weatherInfo.setWeatherCondition(weatherCondition);
                    Log.d("ParseTitle3Day", "Weather Condition extracted: " + weatherCondition);
                } else {
                    Log.d("ParseTitle3Day", "Invalid weather format: " + weatherAndTemp);
                }
            } else {
                Log.d("ParseTitle3Day", "Invalid title format: " + title);
            }
        } else {
            Log.d("ParseTitle3Day", "Empty or null title string provided.");
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
                // Parse each key-value pair and set
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
                    case "Condition":
                        weatherInfo.setCurrentCondition(value);
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
        // Method body
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

    private void setImageForWindIcon(WeatherInfo weatherInfo) {
        String direction = weatherInfo.getWindDirection();
        String windSpeedString = weatherInfo.getWindSpeed();
        double windSpeed;

        if (direction != null) {
            windSpeedString = windSpeedString.replaceAll("[^\\d.]", "");
            windSpeed = Double.parseDouble(windSpeedString); // Parse wind speed
        } else {
            // Handle the case where either weatherInfo or direction is null
            Log.e("setImageForWindIcon", "weatherInfo or direction is null");
            return; // Exit the method early
        }

        runOnUiThread(() -> {
            Log.d("set wind direction", "Clearing wind image");
            if (windIcon != null) { // Check if windIcon and direction are not null
                windIcon.setImageDrawable(null);

                int rotation = 0; // Default rotation value

                switch (direction) {
                    case "Northerly":
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

                //set icon for wind dependant on wind strength
                if (windSpeed >= 30){
                    windIcon.setImageResource(R.drawable.wind_icon_red);
                }
                else if (windSpeed >=20){
                    windIcon.setImageResource(R.drawable.wind_icon_orange);
                }
                else if (windSpeed >=10){
                    windIcon.setImageResource(R.drawable.wind_icon_yellow);
                }
                else {
                    windIcon.setImageResource(R.drawable.wind_icon);
                }
                // Apply rotation to the wind icon
                windIcon.setRotation(rotation);

            } else {
                Log.e("setImageForWindIcon", "windIcon is null or direction is null");
            }
        });
    }

    private void setImageForWeatherCondition(WeatherInfo weatherInfo) {
        String condition = weatherInfo.getCurrentCondition();
        Log.d("CONDITION", "the condition for current weather is set to " + condition);

        runOnUiThread(() -> {
            if (condition != null) {
                // Set the GIF based on the weather condition using Glide
                String finalCondition = condition; // Make a final copy of condition
                switch (finalCondition) {
                    case "Sunny":
                    case "Clear Sky":
                        Glide.with(this).load(R.drawable.sunny_gif).into(weatherImg);
                        break;
                    case "Partly Cloudy":
                    case "Sunny Intervals":
                        Glide.with(this).load(R.drawable.sunny_intervals_gif).into(weatherImg);
                        break;
                    case "Light Cloud":
                    case "Thick Cloud":
                        Glide.with(this).load(R.drawable.cloud).into(weatherImg);
                        break;
                    case "Drizzle":
                    case "Light Rain":
                        Glide.with(this).load(R.drawable.light_rain_gif).into(weatherImg);
                        break;
                    case "Light Rain Showers":
                        Glide.with(this).load(R.drawable.light_rain_showers_gif).into(weatherImg);
                        break;
                    case "Rain":
                        Glide.with(this).load(R.drawable.rain_gif).into(weatherImg);
                        break;
                    case "Heavy Rain":
                        Glide.with(this).load(R.drawable.heavy_rain_gif).into(weatherImg);
                        break;
                    case "Thundery Showers":
                        Glide.with(this).load(R.drawable.lightning_gif).into(weatherImg);
                        break;
                    case "Snow":
                        Glide.with(this).load(R.drawable.snow_gif).into(weatherImg);
                        break;
                    case "Not available":
                    case "not available":
                        // If current condition is not available, check the three-day forecast
                        if (!weatherInfoList1.isEmpty()) {
                            WeatherInfo threeDayWeather = weatherInfoList1.get(0); // Get the first weather info from the three-day forecast
                            finalCondition = threeDayWeather.getWeatherCondition(); // Update the condition from the three-day forecast
                        }
                        // Now, check the condition again
                        switch (finalCondition) {
                            case "Sunny":
                            case "Clear Sky":
                                Glide.with(this).load(R.drawable.sunny_gif).into(weatherImg);
                                break;
                            case "Partly Cloudy":
                            case "Sunny Intervals":
                                Glide.with(this).load(R.drawable.sunny_intervals_gif).into(weatherImg);
                                break;
                            case "Light Cloud":
                            case "Thick Cloud":
                                Glide.with(this).load(R.drawable.cloud).into(weatherImg);
                                break;
                            case "Drizzle":
                            case "Light Rain":
                                Glide.with(this).load(R.drawable.light_rain_gif).into(weatherImg);
                                break;
                            case "Light Rain Showers":
                                Glide.with(this).load(R.drawable.light_rain_showers_gif).into(weatherImg);
                                break;
                            case "Rain":
                                Glide.with(this).load(R.drawable.rain_gif).into(weatherImg);
                                break;
                            case "Heavy Rain":
                                Glide.with(this).load(R.drawable.heavy_rain_gif).into(weatherImg);
                                break;
                            case "Thundery Showers":
                                Glide.with(this).load(R.drawable.lightning_gif).into(weatherImg);
                                break;
                            case "Snow":
                                Glide.with(this).load(R.drawable.snow_gif).into(weatherImg);
                                break;
                        }
                        break; // Break from the outer switch statement
                    default:
                        // If the condition is not "Not available", use the specified condition
                        //Glide.with(this).load(R.drawable.default_image).into(weatherImg);
                        break;
                }
            }
        });
    }



    // Updates UI with current Weather Info
    private void updateWeatherViews(WeatherInfo weatherInfo) {
        if (weatherInfo != null) {
            runOnUiThread(() -> {

                date.setText((weatherInfo.getDate()));
                temperature.setText(weatherInfo.getCurrentTemperature());
                windDirection.setText(String.format("Wind Direction: %s", weatherInfo.getWindDirection()));
                windSpeed.setText(String.format("Wind Speed: %s", weatherInfo.getWindSpeed()));
                humidity.setText(String.format("Humidity: %s", weatherInfo.getHumidity()));
                pressure.setText(String.format("Pressure: %s", weatherInfo.getPressure()));
                visibility.setText(String.format("Visibility: %s", weatherInfo.getVisibility()));

            });

        } else {
            Log.e("updateWeatherViews", "WeatherInfo object is null");
        }
    }
}