package com.app.myapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.myapplication.R;
import com.app.myapplication.database.DatabaseHandler;
import com.app.myapplication.model.WeatherDataDTO;
import com.app.myapplication.rest.RESTForLiveData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView temp;
    TextView pressure;
    TextView humidity;
    TextView cityName;
    EditText city;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = (EditText) findViewById(R.id.editText);
        cityName = (TextView) findViewById(R.id.cityName);
        temp =(TextView) findViewById(R.id.temp);
        pressure =(TextView) findViewById(R.id.pressure);
        humidity = (TextView) findViewById(R.id.humidity);
    }

    public void getWeatherDetails(View v)
    {
        if(city.getText().toString() != null && !city.getText().toString().isEmpty()) {

            db = DatabaseHandler.getInstance(getApplicationContext());
            ArrayList<WeatherDataDTO> list = new ArrayList<WeatherDataDTO>();
            Cursor l_managedCursor = db.getData(city.getText().toString().trim());
            try
            {
                if (l_managedCursor != null)
                {
                    if (l_managedCursor.moveToFirst())
                    {
                        String cityId = "";
                        String cityName = "";
                        String temp = "";
                        String pressure = "";
                        String humidity = "";
                        StringBuilder l_displayText = new StringBuilder();

                        do
                        {
                            WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
                            cityId = l_managedCursor.getString(0);
                            cityName = l_managedCursor.getString(1);
                            temp = l_managedCursor.getString(2);
                            pressure = l_managedCursor.getString(3);
                            humidity = l_managedCursor.getString(4);

                            weatherDataDTO.setCityId(cityId);
                            weatherDataDTO.setCity(cityName);
                            weatherDataDTO.setTemp(temp);
                            weatherDataDTO.setPressure(pressure);
                            weatherDataDTO.setHumidity(humidity);
                            list.add(weatherDataDTO);
                        }while (l_managedCursor.moveToNext());

                        l_managedCursor.close();

                    }
                }


                if(list != null)
                {
                    if(list.size() > 0) {

                        for (WeatherDataDTO weatherDataDTO : list) {
                            cityName.setText("CityName : "+weatherDataDTO.getCity());
                            temp.setText("Temperature : "+weatherDataDTO.getTemp());
                            pressure.setText("Pressure : "+weatherDataDTO.getPressure());
                            humidity.setText("Humidity : "+weatherDataDTO.getHumidity());
                        }

                    }
                    else
                    {
                        new GetWeatherData(this).execute(city.getText().toString().trim(), "52ee4deba4a01b2306b0219a1a3a860d");
                    }

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please enter city",Toast.LENGTH_SHORT).show();
        }
    }


    private class GetWeatherData extends AsyncTask<String, String, JSONObject>
    {
        private ProgressDialog pDialog;
        private String city;
        private String appid;
        private Activity activity;

        public GetWeatherData(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Please Wait..");
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args)
        {
            city = args[0];
            appid = args[1];

            RESTForLiveData restForLiveData = new RESTForLiveData();

            return restForLiveData.getLiveData(city, appid);
        }

        @Override
        protected void onPostExecute(JSONObject json)
        {
            try
            {
                if ((pDialog != null) && pDialog.isShowing()){
                    pDialog.dismiss();
                }

                if (json != null)
                {
                    JSONObject jsonObj= null;
                    JSONArray jsonArray = json.getJSONArray("list");
                    for(int i=0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        String value = jsonData.getString("main");
                        jsonObj = new JSONObject(value);
                        /*String [] values = value.split(",");
                        for(String str : values) {
                            parameter += str+"\n";
                        }*/
                        cityName.setText("CityName : "+city);
                        temp.setText("Temperature : "+jsonObj.getString("temp"));
                        pressure.setText("Pressure : "+jsonObj.getString("pressure"));
                        humidity.setText("Humidity : "+jsonObj.getString("humidity"));
                    }

                    saveData(jsonObj,city);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Log.d("Error", "error : "+e.toString());
            }
        }
    }


    public void saveData(JSONObject jsonObject,String city) {
        try {
            db = DatabaseHandler.getInstance(MainActivity.this);
            WeatherDataDTO weatherDataDTO = new WeatherDataDTO();
            weatherDataDTO.setCity(city);
            weatherDataDTO.setTemp(jsonObject.getString("temp"));
            weatherDataDTO.setPressure(jsonObject.getString("pressure"));
            weatherDataDTO.setHumidity(jsonObject.getString("humidity"));
            db.addData(weatherDataDTO);
            Log.e("saved data", jsonObject.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
