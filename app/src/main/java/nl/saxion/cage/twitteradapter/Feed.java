package nl.saxion.cage.twitteradapter;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Feed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_tweet);

        //create list for tweets
        List<Tweets> tweets = new ArrayList<>();

        //create CardView adapter
        CardAdapter adapter = new CardAdapter( this,R.layout.card_item,tweets);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);

        //set fixed size
        try {
            //make sure recList isn't null
            assert recList != null;
            recList.setHasFixedSize(true);
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        try{
            recList.setLayoutManager(llm);
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
        recList.setAdapter( adapter );


        String file = null;
        try {
            file = readAssetIntoString("tweets.json");
            JSONObject stateObj	=	new JSONObject(file);
            JSONArray tweetArray = stateObj.getJSONArray("statuses");
            for (int i = 0; i < tweetArray.length(); i++) {

                JSONObject tweetObj = tweetArray.getJSONObject(i);

                String text = tweetObj.getString("text");
                String createdAt = tweetObj.getString("created_at");
                int retweets = tweetObj.getInt("retweet_count");
                int favourites = tweetObj.getInt("favorite_count");

                JSONObject userObj = tweetObj.getJSONObject("user");
                String name = userObj.getString("name");
                String screen_name = userObj.getString("screen_name");
                Users user = new Users(screen_name,name);

                tweets.add( new Tweets( user, text, retweets, createdAt,favourites));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
     * Reads an asset file and returns a string with the full contents.
     *
     * @param filename  The filename of the file to read.
     * @return          The contents of the file.
     * @throws IOException  If file could not be found or not read.
     */
    private String readAssetIntoString(String filename) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            InputStream is = getAssets().open(filename, AssetManager.ACCESS_BUFFER);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
