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

public class Connection extends AsyncTask<String, Void, String> {
    private static final String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private static final String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";
    private static final String CHARSET_UTF_8 = "UTF-8";

    Connection() {

    }

    @Override
    protected String doInBackground(String... params) {
        requestData(params[0], retrieveToken());
        return null;
    }

    private String retrieveToken() {
        String bearerToken = "";
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

            BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(body);
            os.close();
            int response = conn.getResponseCode();
            if (response == 200) {
                InputStream is = conn.getInputStream();
                String connResponse = IOUtils.toString(is, "UTF-8");

                bearerToken = parseJsonToken(connResponse);
                Log.d("token", bearerToken);
                IOUtils.closeQuietly(is);
            } else {
                Log.d("Connection", String.valueOf(conn.getResponseCode()));
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
        return bearerToken;
    }

    public String parseJsonToken(String file) throws JSONException {
        JSONObject jObj = new JSONObject(file);
        String token = jObj.getString("access_token");
        return token;
    }

    public String requestData(String searchTerm, String bearerToken) {

        //the url we need to query
        //https://api.twitter.com/1.1/search/tweets.json?q=%40twitterapi
        try {

            //search api url
            String searchUrl = "https://api.twitter.com/1.1/search/tweets.json?q=";
            String encodedTerm = URLEncoder.encode(searchTerm, CHARSET_UTF_8);

            URL url = new URL(searchUrl+encodedTerm);

            //create connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //connection headers
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Authorization", "Bearer " + bearerToken);
            conn.addRequestProperty("Content-Type", "application/json");

            conn.setConnectTimeout (5000) ;
            conn.setDoInput(true);

            int response = conn.getResponseCode();
            if (response == 200) {
                InputStream is = conn.getInputStream();
                String connResponse = IOUtils.toString(is, "UTF-8");

                String tweets = connResponse;
                Log.d("Tweets", connResponse);
                IOUtils.closeQuietly(is);
            } else {
                Log.d("(((",conn.getResponseMessage());
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}