package com.app.myapplication.rest;

import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dinesh on 3/23/2015.
 *
 */
public class RESTForLiveData {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JSONObject getLiveData(String city, String appid) {

        String url = "http://api.openweathermap.org/data/2.5/find?q=";
        HttpURLConnection con = null ;
        InputStream is = null;
        try
        {
            /*HttpClient httpclient;
            HttpPost httppost;
            ArrayList<NameValuePair> postParameters;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(url);
            postParameters = new ArrayList<NameValuePair>();
            if((city != null && !city.isEmpty() )&& (appid != null && !appid.isEmpty())) {
                postParameters.add(new BasicNameValuePair("q", "pune"));
                postParameters.add(new BasicNameValuePair("appid", "52ee4deba4a01b2306b0219a1a3a860d"));
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                is = response.getEntity().getContent();
            }*/

                con = (HttpURLConnection) ( new URL(url + city+"&appid="+appid)).openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.connect();

                // Let's read the response
                StringBuffer buffer = new StringBuffer();
                is = con.getInputStream();

        }catch (ClientProtocolException e) {
    
            e.printStackTrace();
    
        } catch (IOException e) {

            e.printStackTrace();

        } catch (Exception e) {

           e.printStackTrace();
        }
    
    
        try {
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
    
            json = sb.toString();
            Log.e("JSONStr", json);
    
        } catch (Exception e) {
    
            e.printStackTrace();
        }
        // Parse the String to a JSON Object
        try {
            jObj = new JSONObject(json);
        } catch (Exception e) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // Return JSON String
        return jObj;

    }
}
