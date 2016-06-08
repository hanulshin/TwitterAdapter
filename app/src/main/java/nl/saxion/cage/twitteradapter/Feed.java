package nl.saxion.cage.twitteradapter;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
import nl.saxion.cage.twitteradapter.Entities.URL;
import nl.saxion.cage.twitteradapter.Entities.User_Mention;

public class Feed extends AppCompatActivity implements AsyncResponse {

    List<Tweets> tweets = new ArrayList<>();
    String searchJSON;
    CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_tweet);
//        Button buttonBG = (Button) findViewById(R.id.search);
//        buttonBG.setBackgroundResource(R.drawable.searchicon);
        Button searchButton = (Button) findViewById(R.id.search);

        assert searchButton != null;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mEdit = (EditText) findViewById(R.id.searchField);
                Connection conn = new Connection();
                String poop = mEdit.getText().toString();
                conn.execute(poop);
            }
        });


        try {
            String tweetsFile = readAssetIntoString("tweets.json");
            readJsonToObjects(tweetsFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new CardAdapter(this, R.layout.card_item_alt, tweets, this);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recList.setLayoutManager(llm);
        recList.setAdapter(adapter);

        //create connection and query the twitter api
        Connection conn = new Connection();
        conn.delegate = this;
        conn.execute("poop");
        if (searchJSON != null) {
            System.out.println(searchJSON);
        } else {
            System.out.println("searchJson is null");
        }

        try {
            conn.get();
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

            JSONObject userObj = tweetObj.getJSONObject("user");
            String name = userObj.getString("name");
            String screen_name = userObj.getString("screen_name");

            String profile_image_url = userObj.getString("profile_image_url");


//            image.setTag(profile_image_url);


            //LoadProfileAsync loadProfileAsync = new LoadProfileAsync();
            //loadProfileAsync.execute(image);

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
//////
            JSONArray JURLArray = JEntities.getJSONArray("urls");
            for (int p = 0; p < JURLArray.length(); p++) {
                JSONObject Jurl = JURLArray.getJSONObject(p);
//                String urlText = Jurl.getString("text");
                JSONArray JIndices = Jurl.getJSONArray("indices");
                int indices[] = new int[2];
                indices[0] = JIndices.getInt(0);
                indices[1] = JIndices.getInt(1);
                URL url = new URL(indices, "");
                urlList.add(url);
            }

            ArrayList<User_Mention> mentionList = new ArrayList<>();
            JSONArray JMentionArray = JEntities.getJSONArray("user_mentions");
            for (int p = 0; p < JMentionArray.length(); p++) {
                JSONObject Jmention = JMentionArray.getJSONObject(p);
                JSONArray JIndices = Jmention.getJSONArray("indices");
                int indices[] = new int[2];
                indices[0] = JIndices.getInt(0);
                indices[1] = JIndices.getInt(1);
                User_Mention mention = new User_Mention(indices);
                mentionList.add(mention);
            }


            Entities entities = new Entities(hashtagList, urlList, mentionList);

//        List<Media> media = (List<Media>) JEntities.getJSONArray("media");
//        List<URL> urls = (List<URL>) JEntities.getJSONArray("urls");
//        List<User_Mention> user_mentions = (List<User_Mention>) JEntities.getJSONArray("user_mentions");
//        Entities entities = new Entities(hashtags, media, urls, user_mentions);

            Users user = new Users(screen_name, name, profile_image_url);
            tweets.add(new Tweets(user, text, retweets, createdAt, favourites, entities));
        }
    }

    @Override
    public void processFinish(String output) {
        //recieve output
        searchJSON = output;
        if (searchJSON != null) {
            try {
                readJsonToObjects(output);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("OK" + output);
            adapter.notifyDataSetChanged();
        } else {
            System.out.println("LOL");
        }

    }
}
