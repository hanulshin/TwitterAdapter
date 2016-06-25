package nl.saxion.cage.twitteradapter.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class SearchTwitterAsync extends AsyncTask<String, Void, String> {

    private static final String CHARSET_UTF_8 = "UTF-8";

    @Override
    protected String doInBackground(String... params) {
        return requestData(params[0], params[1]);
    }

     /**
     * Gets list of tweets from twitter based on searchTerm
     *
     * @param searchTerm the term that will be searched
     * @param bearerToken token retrieved from twitter with api key and secret
     * @return String json file of tweets list
     */
    public String requestData(String searchTerm, String bearerToken) {
        String tweets = null;

        try {
            //search api url
            String searchUrl = "https://api.twitter.com/1.1/search/tweets.json?q=";
            String encodedTerm = URLEncoder.encode(searchTerm, CHARSET_UTF_8);

            URL url = new URL(searchUrl + encodedTerm);

            //create connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //connection headers
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Authorization", "Bearer " + bearerToken);
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);

            int response = conn.getResponseCode();
            if (response == 200) {
                InputStream is = conn.getInputStream();
                String connResponse = IOUtils.toString(is, "UTF-8");

                tweets = connResponse;
                Log.d("Tweets", connResponse);
                IOUtils.closeQuietly(is);
            } else {
                Log.d("Response error", conn.getResponseMessage());
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweets;
    }
}