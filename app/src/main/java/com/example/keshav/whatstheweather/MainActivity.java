package com.example.keshav.whatstheweather;

import android.content.Context;
import android.content.RestrictionEntry;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    Button buttonWeather;
    EditText editText;
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonWeather = findViewById(R.id.buttonWeather);
        editText = findViewById(R.id.editText2);
        resultTextView = findViewById(R.id.resultTextView);

    }
    public void getWeather(View view)
    {
        try {
            DownloadTask task = new DownloadTask();

            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=cc652841b0776d9fce5b9b623e88f277");
            editText.setText("");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObject = new JSONObject(s);

                String message = "";
                String main="";
                String description="";
                String temp="";
                String minTemp="";
                String maxTemp="";
                String convertedTemp="";
                String convertedMinTemp="";
                String convertedMaxTemp="";

                String weatherInfo = jsonObject.getString("weather");
                String tempInfo = jsonObject.getString("main");


                //JSONObject jsonObject2 = new JSONObject("main");

                Log.i("Weather content", weatherInfo);
                Log.i("Temp Info", tempInfo);

                JSONArray arr = new JSONArray(weatherInfo);
                JSONObject jsTempInfo = new JSONObject(tempInfo);
                //JSONArray arr2 = new JSONArray(tempInfo);

                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    //JSONObject jsonPart2 = arr2.getJSONObject(i);


                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                    temp = jsTempInfo.getString("temp");
                    minTemp = jsTempInfo.getString("temp_min");
                    maxTemp = jsTempInfo.getString("temp_max");

                    convertedTemp = convertToCelsius(temp);
                    convertedMinTemp = convertToCelsius(minTemp);
                    convertedMaxTemp = convertToCelsius(maxTemp);

                    Log.i("Min Temp", minTemp+" F°");
                    Log.i("Temp", temp+" F°");
                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));
                    //Log.i("temp", jsonObject2.getString("temp"));
                }
                if(!main.equals("") && !description.equals("") &&!temp.equals(""))
                {
                    message += main+" : "+description+"\r\n"+"Current Temperature: "+convertedTemp+"° C"+"\r\n"+
                            "Minimum Temperature: "+convertedMinTemp+"° C"+"\r\n"+ "Maximum Temperature: "
                        +convertedMaxTemp +"° C";
                    //temp+= temp +"\r\n";
                }
                if(!message.equals(""))
                {
                    resultTextView.setText(message);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Unable to find weather", Toast.LENGTH_SHORT).show();
            }
        }
        public String convertToCelsius(String tempInFahrenheit)
        {
            Double tempInCelsius = Math.floor((Double.parseDouble(tempInFahrenheit) - 273.15));
            String newTemp = Double.toString(tempInCelsius);
            return newTemp;
        }
    }
}