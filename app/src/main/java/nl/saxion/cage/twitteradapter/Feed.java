package nl.saxion.cage.twitteradapter;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Entities.Hashtags;
import nl.saxion.cage.twitteradapter.Entities.Media;
import nl.saxion.cage.twitteradapter.Entities.URL;
import nl.saxion.cage.twitteradapter.Entities.User_Mention;
import static android.widget.TextView.*;

public class Feed extends AppCompatActivity {

    //list of tweets
    List<Tweets> tweets = new ArrayList<>();

    //current json file of tweets
    String searchJSON;

    //adapter for cardView
    CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tweet);

        final EditText editSearch = (EditText) findViewById(R.id.editSearch);

        OnEditorActionListener actionListener = new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 5) {
                    queryTwitter(editSearch.getText().toString());
                    editSearch.setText("");
                    return true;
                }
                return false;
            }
        };

        assert editSearch != null;
        editSearch.setOnEditorActionListener(actionListener);

        adapter = new CardAdapter(this, R.layout.card_item_alt, tweets, this);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recList.setLayoutManager(llm);
        recList.setAdapter(adapter);
    }

    private void queryTwitter(String searchTerm){
        //create connection
        Connection conn = new Connection();

        //set delegate for parsing response and updating the tweet list
        //conn.delegate = this;

        //query twitter
        conn.execute(searchTerm);

        try {
            searchJSON = conn.get();
            if (searchJSON != null) {
                try {
                    readJsonToObjects(searchJSON);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("OK" + searchJSON);
                adapter.notifyDataSetChanged();
            } else {
                System.out.println("LOL");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads an asset file and returns a string with the full contents.
     *
     * @param filename The filename of the file to read.
     * @return The contents of the file.
     * @throws IOException If file could not be found or not read.
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


    /**
     * Parses JSON file into objects
     *
     * @param file name of the JSON file to be parsed.
     */
    private void readJsonToObjects(String file) throws IOException, JSONException {

        tweets.clear();
        JSONObject jObj = new JSONObject(file);
        JSONArray tweetArray = jObj.getJSONArray("statuses");

        for (int i = 0; i < tweetArray.length(); i++) {
            JSONObject tweetObj = tweetArray.getJSONObject(i);

            String text = tweetObj.getString("text");
            String createdAt = tweetObj.getString("created_at");
            int retweets = tweetObj.getInt("retweet_count");
            int favourites = tweetObj.getInt("favorite_count");

            //get user from json file
            JSONObject userObj = tweetObj.getJSONObject("user");
            String name = userObj.getString("name");
            String screen_name = userObj.getString("screen_name");
            String profile_image_url = userObj.getString("profile_image_url");

            //get entities from json file
            JSONObject JEntities = tweetObj.getJSONObject("entities");

            ArrayList<Hashtags> hashtagList = new ArrayList<>();
            JSONArray JHashtagArray = JEntities.getJSONArray("hashtags");
            for (int j = 0; j < JHashtagArray.length(); j++) {
                JSONObject JHashtags = JHashtagArray.getJSONObject(j);
                String hashText = JHashtags.getString("text");
                JSONArray JIndices = JHashtags.getJSONArray("indices");
                int indices[] = new int[2];
                indices[0] = JIndices.getInt(0);
                indices[1] = JIndices.getInt(1);
                Hashtags hashtag = new Hashtags(indices, hashText);
                hashtagList.add(hashtag);
            }


            ArrayList<URL> urlList = new ArrayList<>();
            JSONArray JURLArray = JEntities.getJSONArray("urls");
            for (int p = 0; p < JURLArray.length(); p++) {
                JSONObject Jurl = JURLArray.getJSONObject(p);
                JSONArray JIndices = Jurl.getJSONArray("indices");
                int indices[] = {JIndices.getInt(0),JIndices.getInt(1)};
                URL url = new URL(indices, Jurl.getString("url"));
                urlList.add(url);
            }

            ArrayList<User_Mention> mentionList = new ArrayList<>();
            JSONArray JMentionArray = JEntities.getJSONArray("user_mentions");
            for (int p = 0; p < JMentionArray.length(); p++) {
                JSONObject Jmention = JMentionArray.getJSONObject(p);
                JSONArray JIndices = Jmention.getJSONArray("indices");
                int indices[] = {JIndices.getInt(0),JIndices.getInt(1)};
                User_Mention mention = new User_Mention(indices);
                mentionList.add(mention);
            }

//            ArrayList<Media> mediaList = new ArrayList<>();
//            JSONArray jsonMedia = JEntities.getJSONArray("media");
//            for (int p = 0; p < JMentionArray.length(); p++) {
//                JSONObject Jmention = JMentionArray.getJSONObject(p);
//                JSONArray JIndices = Jmention.getJSONArray("indices");
//                int indices[] = {JIndices.getInt(0),JIndices.getInt(1)};
//                User_Mention mention = new User_Mention(indices);
//                mentionList.add(mention);
//            }

            Entities entities = new Entities( hashtagList, urlList, mentionList );

            Users user = new Users(screen_name, name, profile_image_url);
            tweets.add(new Tweets(user, text, retweets, createdAt, favourites, entities));
        }
    }
}
