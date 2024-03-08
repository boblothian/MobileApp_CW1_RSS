package com.example.lothian_robert_rlothi300;

import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView weatherDataDisplay;
    private Button startButton;

    private Spinner spinner;
    private static String[] paths = {"Glasgow", "London", "New York", "Oman", "Mauritius", "Bangladesh"}; // this displays names in spinner
    public WeatherInfo weatherDay1, weatherDay2, weatherDay3;
    public String locationID = "2648579";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherDataDisplay = findViewById(R.id.weatherData);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item,paths);

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
                startButton.callOnClick();
                break;
            case 1:
                //London
                locationID = "2643743";
                startButton.callOnClick();
                break;
            case 2:
                //New York
                locationID = "5128581";
                startButton.callOnClick();
                break;
            case 3:
                //Oman
                locationID = "287286";
                startButton.callOnClick();
                break;
            case 4:
                //Mauritius
                locationID = "934154";
                startButton.callOnClick();
                break;
            case 5:
                //Bangladesh
                locationID = "1185241";
                startButton.callOnClick();
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //nothing happens
    }

    public void onClick(View aview) {
        startProgress();
    }

    public void startProgress() {
        String urlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/"+ locationID+"/"; //calls the RSS feed with location modifier chosen by spinner
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
    //this is where the parser starts and finds the tag <item> where it creates a new WeatherInfo object
    // and then the tag <description> where it sends that data to another parser in the parseDescription method
    // Update the weatherDataDisplay TextView with the formatted weather data
    private void parseData(String dataToParse) {
        List<WeatherInfo> weatherInfoList1 = new ArrayList<>();
        List<WeatherInfo> weatherInfoList2 = new ArrayList<>();
        List<WeatherInfo> weatherInfoList3 = new ArrayList<>();


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
                            weatherDay1 = new WeatherInfo();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (tagName != null && weatherDay1 != null) {
                            switch (tagName) {
                                case "description":
                                    parseDescription(text, weatherDay1);
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();

                //Day2
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("item")) {
                            weatherDay2 = new WeatherInfo();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (tagName != null && weatherDay2 != null) {
                            switch (tagName) {
                                case "description":
                                    parseDescription(text, weatherDay2);
                                    break;
                            }
                        }
                        break;
                }

                //Day3
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("item")) {
                            weatherDay3 = new WeatherInfo();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (tagName != null && weatherDay3 != null) {
                            switch (tagName) {
                                case "description":
                                    parseDescription(text, weatherDay3);
                                    break;
                            }
                        }
                        break;
                }

            }
            //TODO Need to make seperate lists currently it prints day 1 data 3 times :/
            //if the data is not null add it to a list
            if (weatherDay1 != null) {
                weatherInfoList1.add(weatherDay1);
            }

            if (weatherDay2 != null) {
                weatherInfoList2.add(weatherDay2);
            }

            if (weatherDay3 != null) {
                weatherInfoList3.add(weatherDay3);
            }


            // Updates the interface with parsed data
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This displays the information just parsed and formatted in the list
                    displayWeatherInfo(weatherInfoList1, weatherInfoList2, weatherInfoList3);
                }
            });

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    //this method parses through the Description tag of the RSS feed, it splits the data by category
    //by looking for a comma space ", " and then into key values by identifying when a colon space ": " occurs
    private void parseDescription(String description, WeatherInfo weatherInfo){
        Log.e("Description", description);
        String[] parts = description.split(", ");

        //This identifies the Key Values and splits them
        for (String part : parts) {
            String[] keyValue = part.split(": ");
            // this checks to ensure only the keypair only has 2 parts (to match how the RSS feed is formatted)
            if (keyValue.length ==2) {
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
    //method to display the information gathered in the RSS feed. This will change as for the project I will use fragments to contain this information
    private void displayWeatherInfo(List<WeatherInfo> weatherInfoList1, List<WeatherInfo> weatherInfoList2, List<WeatherInfo> weatherInfoList3) {
        StringBuilder weatherData = new StringBuilder();

        // This appends weather info with data contained in the weatherInfo object and prints the category
        //TODO look at ways to display this information in more appealing ways, use images (eg wind direction N S E W, could scale this image depending on speed? If <10mph size small <30 >10 medium, >30 large

        for (WeatherInfo weatherInfo : weatherInfoList1) {
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
            weatherData.append("Sunset: ").append(weatherInfo.getSunset()).append("\n\n");
            // Append other weather information as needed
        }

        for (WeatherInfo weatherInfo : weatherInfoList2) {
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
            weatherData.append("Sunset: ").append(weatherInfo.getSunset()).append("\n\n");
            // Append other weather information as needed
        }

        for (WeatherInfo weatherInfo : weatherInfoList3) {
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
            weatherData.append("Sunset: ").append(weatherInfo.getSunset()).append("\n\n");
            // Append other weather information as needed
        }



        //this displays the above information in a text box.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weatherDataDisplay.setText(weatherData.toString());
            }
        });
    }


}
