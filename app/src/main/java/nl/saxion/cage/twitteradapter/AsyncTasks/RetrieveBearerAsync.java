package nl.saxion.cage.twitteradapter.AsyncTasks;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import nl.saxion.cage.twitteradapter.Model;

public class RetrieveBearerAsync extends AsyncTask<String, Void, String> {

    static Model model = Model.getInstance();

    //key & secret for getting accessToken
    private static final String API_KEY = model.getApiKey();
    private static final String API_SECRET = model.getApiSecret();

    //encoding charset
    private static final String CHARSET_UTF_8 = "UTF-8";

    @Override
    protected String doInBackground(String... params) {
        return retrieveToken();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    /**
     * retrieves bearer token from twitter with api key and secret
     *
     * @return bearerToken
     */
    private String retrieveToken() {
        // Set blank bearerToken
        String jsonToken = "";

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

            //set up output stream
            BufferedOutputStream os = new BufferedOutputStream(conn.getOutputStream());

            //write and close output stream
            os.write(body);
            os.close();

            //grab response code
            int response = conn.getResponseCode();

            //check response
            if (response == 200) {
                InputStream is = conn.getInputStream();
                String connResponse = IOUtils.toString(is, "UTF-8");
                jsonToken = parseJsonToken(connResponse);
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

        return jsonToken;
    }

    /**
     * Parses bearer token json file to string format
     *
     * @param jsonToken the json string we receive as a response of retrieveToken
     * @return parsed token
     * @throws JSONException
     */
    public String parseJsonToken(String jsonToken) throws JSONException {
        //create new JSONobject from the file
        JSONObject jObj = new JSONObject(jsonToken);

        //return token String
        return jObj.getString("access_token");
    }
}