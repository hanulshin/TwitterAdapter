package nl.saxion.cage.twitteradapter;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class Connection extends AsyncTask<String, Void, Void>{
    private static final String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private static final String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";
    private static final String CHARSET_UTF_8 = "UTF-8";

    Connection() {
//        URL url = null;
//        try {
//            url = new URL("http://<url>");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection conn = null;
//
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        conn.setReadTimeout(10000);
//        conn.setConnectTimeout(15000);
//        conn.setDoInput(true);
//        int response = 0;
//
//        try {
//            response = conn.getResponseCode();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (response == 200) {
//            //everything is okay :D
//        } else {
//            //we fucked up
//        }
//
//        try {
//            conn.setRequestMethod("GET");
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        }
//
//        //tells client we want to get input from it
////        conn.setDoInput(true);
//
//        try {
//            InputStream is = conn.getInputStream();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
    }

    @Override
    protected Void doInBackground(String... params) {
        retrieveToken();
        return null;
    }

    private void retrieveToken() {
        // Prepare request
        try {

            URL url = new URL("https://api.twitter.com/oauth2/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            // Encode API key and secret
            String authString = URLEncoder.encode(API_KEY, CHARSET_UTF_8) + ":" + URLEncoder.encode(API_SECRET, CHARSET_UTF_8);

            // Apply Base64 encoding on the encoded string
            String authStringBase64 = Base64.encodeToString(authString.getBytes(CHARSET_UTF_8), Base64.NO_WRAP);

            // Set headers
            conn.setRequestProperty("Authorization", "Basic " + authStringBase64);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            // Set body
            conn.setDoOutput(true);
            byte[] body = "grant_type=client_credentials".getBytes("UTF-8");
            conn.setFixedLengthStreamingMode(body.length);

//            PrintWriter outWriter = new PrintWriter(conn.getOutputStream());

            BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(body);
            os.close();
            int response = conn.getResponseCode();
            if (response == 200) {
                InputStream is = conn.getInputStream();
                String connResponse = IOUtils.toString(is, "UTF-8");

                String bearerToken = parseJsonToken(connResponse);
                Log.d("token",bearerToken);
                IOUtils.closeQuietly(is);
            } else {
                Log.d("(((", "fail");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String parseJsonToken(String file) throws JSONException {
        JSONObject jObj = new JSONObject(file);
        String token = jObj.getString("access_token");
        return token;
    }
}
