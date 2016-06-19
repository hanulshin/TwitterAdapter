package nl.saxion.cage.twitteradapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.scribejava.core.model.OAuth1AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
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

    Context context = this;
    //access token
    private OAuth1AccessToken accessToken = null;

    //list of tweets
    List<Tweets> tweets = new ArrayList<>();

    //current json file of tweets
    private String searchJSON;

    //adapter for cardView
    private CardAdapter adapter;

    //search bar and profile button
    private EditText editSearch;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_screen);

        //login screen
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent,1);



        //define editText view for search bar, and profile button
        editSearch = (EditText) findViewById(R.id.editSearch);
        profileButton = (Button) findViewById(R.id.profileButton);

        //listens to key presses on keyboard
        OnEditorActionListener actionListener = new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    //query twitter api and clear search bar
                    queryTwitter(editSearch.getText().toString());
                    editSearch.setText("");

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    //the key has been pressed
                    return true;
                }
                return false;
            }

        };
        editSearch.setOnEditorActionListener(actionListener);

        //card view adapter
        adapter = new CardAdapter(this, R.layout.card_item, tweets, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);

        //linear layout manager used for recyclerView (cardView)
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //set recyclerView layout manager & adapter
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);


        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               if (accessToken != null){
                   Log.d("Buttton bprees",accessToken.toString());
                   //user profile screen
                   Intent intent = new Intent(context, UserProfileActivity.class);
                   intent.putExtra("accessToken", accessToken);
                   startActivity(intent);

               }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}

        //get access token from data intent
        accessToken = (OAuth1AccessToken) data.getExtras().getSerializable("accessToken");
        Log.d("yomama",accessToken.toString());

    }

    private void queryTwitter(String searchTerm){
        //create connection
        Connection conn = new Connection();

        //query twitter
        conn.execute(searchTerm);

        //get response of twitter query
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

                //update the card view
                adapter.notifyDataSetChanged();

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

        //clear the current list of tweets
        tweets.clear();

        //create new jsonObject for reading the file
        JSONObject jsonObject = new JSONObject(file);

        //get statuses
        JSONArray jTweetArray = jsonObject.getJSONArray("statuses");

        //loop through statuses
        for (int i = 0; i < jTweetArray.length(); i++) {
            JSONObject jTweetObj = jTweetArray.getJSONObject(i);

            //get basic status properties
            String text = jTweetObj.getString("text");
            String createdAt = jTweetObj.getString("created_at");
            int retweets = jTweetObj.getInt("retweet_count");
            int favourites = jTweetObj.getInt("favorite_count");

            //get user
            JSONObject jUserObject = jTweetObj.getJSONObject("user");
            String name = jUserObject.getString("name");
            String screen_name = jUserObject.getString("screen_name");
            String profile_image_url = jUserObject.getString("profile_image_url");

            //get entities
            JSONObject jEntitiesObject = jTweetObj.optJSONObject("entities");

            //get hashtags
            ArrayList<Hashtags> hashtagList = new ArrayList<>();
            JSONArray jHashtagArray = jEntitiesObject.optJSONArray("hashtags");

            //loop through all hashtags
            for (int j = 0; j < jHashtagArray.length(); j++) {
                JSONObject JHashtags = jHashtagArray.optJSONObject(j);
                String hashText = JHashtags.getString("text");
                JSONArray jIndices = JHashtags.getJSONArray("indices");
                int indices[] = new int[2];
                indices[0] = jIndices.getInt(0);
                indices[1] = jIndices.getInt(1);
                Hashtags hashtag = new Hashtags(indices, hashText);
                hashtagList.add(hashtag);
            }

            //get urls
            ArrayList<URL> urlArray = new ArrayList<>();
            JSONArray jUrlArray = jEntitiesObject.getJSONArray("urls");

            //loop through all urls
            for (int p = 0; p < jUrlArray.length(); p++) {
                JSONObject jUrl = jUrlArray.optJSONObject(p);
                JSONArray jIndices = jUrl.getJSONArray("indices");
                int indices[] = {jIndices.getInt(0), jIndices.getInt(1)};
                URL url = new URL(indices, jUrl.getString("url"));
                urlArray.add(url);
            }

            //get user mentions
            ArrayList<User_Mention> userMentionArray = new ArrayList<>();
            JSONArray jUserMentionArray = jEntitiesObject.getJSONArray("user_mentions");

            //loop through all user mentions
            for (int p = 0; p < jUserMentionArray.length(); p++) {
                JSONObject jUserMention = jUserMentionArray.getJSONObject(p);
                JSONArray JIndices = jUserMention.getJSONArray("indices");
                int indices[] = {JIndices.getInt(0), JIndices.getInt(1)};
                User_Mention mention = new User_Mention(indices);
                userMentionArray.add(mention);
            }

            //get media
            ArrayList<Media> mediaArray = new ArrayList<>();
            JSONArray jMediaArray = jEntitiesObject.optJSONArray("media");

            //loop through all media
            if (jMediaArray != null) {
                for (int p = 0; p < jUserMentionArray.length(); p++) {
                    JSONObject jMediaObject = jMediaArray.optJSONObject(p);
                    if (jMediaObject != null) {
                        JSONArray JIndices = jMediaObject.optJSONArray("indices");
                        int indices[] = {JIndices.getInt(0), JIndices.getInt(1)};
                        String url = jMediaObject.getString("media_url");
                        Media media = new Media(indices, url);
                        mediaArray.add(media);
                    }
                }
            }

            //create new entities object with extracted json data
            Entities entities = new Entities(hashtagList, mediaArray, urlArray, userMentionArray);

            //create new user object with extracted json data
            Users user = new Users(screen_name, name, profile_image_url);

            //create and add new Tweet to list of tweets with json data, and user & entities object
            tweets.add(new Tweets(user, text, retweets, createdAt, favourites, entities));
        }
    }
}
