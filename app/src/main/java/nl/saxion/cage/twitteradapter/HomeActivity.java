package nl.saxion.cage.twitteradapter;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.github.scribejava.core.model.OAuth1AccessToken;
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
import nl.saxion.cage.twitteradapter.Actitivies.LoginActivity;
import nl.saxion.cage.twitteradapter.Actitivies.SearchActivity;
import nl.saxion.cage.twitteradapter.Actitivies.UserProfileActivity;
import nl.saxion.cage.twitteradapter.Adapters.CardTweetAdapter;
import nl.saxion.cage.twitteradapter.AsyncTasks.GetHomeTimelineAsync;
import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Entities.Hashtags;
import nl.saxion.cage.twitteradapter.Entities.Media;
import nl.saxion.cage.twitteradapter.Entities.URL;
import nl.saxion.cage.twitteradapter.Entities.User_Mention;
import nl.saxion.cage.twitteradapter.Tweets.Tweets;

public class HomeActivity extends AppCompatActivity {

    /**
     * accessToken for signing requests
     */
    private OAuth1AccessToken accessToken = null;

    /**
     * list of tweets
     */
    List<Tweets> tweets = new ArrayList<>();

    /**
     * String json file containing list of tweets
     */
    private String jsonTweets;

    /**
     * to adapt list of tweets to cardView
     */
    private CardTweetAdapter cardTweetAdapter;

    /**
     * model class
     */
    private static Model model = Model.getInstance();

    /**
     * Sets up toolbar, starts login screen, initializes cardView adapter
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_screen);

        //set up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

//        //login screen
//        Intent loginIntent = new Intent(this, LoginActivity.class);
//        startActivityForResult(loginIntent, 1);

        //card view
        cardTweetAdapter = new CardTweetAdapter(tweets, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);

        //linear layout manager used for recyclerView (cardView)
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //set recyclerView layout manager &
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(cardTweetAdapter);
    }

    /**
     *
     * @param menu menu layout resource, defines what is in the toolbar (optionsMenu)
     * @return boolean from superClass
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     * @param item the item in toolbar (optionsMenu) that has been clicked
     * @return boolean from superclass
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getHomeTimeline();
                return true;
            case R.id.action_search:
                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.action_profile:
                if (model.isLoggedIn()) {
                    if (accessToken != null) {
                        Intent profileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        startActivity(profileIntent);
                    }
                } else {
                    //login screen
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivityForResult(loginIntent, 1);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * reload the home timeline after coming back to
     * this page from another (eg. search screen)
     */
    @Override
    protected void onResume() {
        super.onResume();
        //getHomeTimeline();
    }

    /**
     * Gives the result of the loginActivity
     *
     * @param requestCode
     * @param resultCode
     * @param data intent containing access token
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            finish();
            return;
        }
        //get Model instance and set accessToken
        model.setAccessToken((OAuth1AccessToken) data.getExtras().getSerializable("accessToken"));

        //tell the model that we have successfully logged in
        model.setLoggedIn(true);

        //get accessToken from model
        accessToken = model.getAccessToken();

        //load main timeline
        Intent profileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(profileIntent);
    }

    /**
     * Gets the home timeline of the logged in user and updates the cardView
     */
    private void getHomeTimeline() {
        if (model.isLoggedIn()) {
            if (accessToken != null) {
                //new getHomeTimeline async task
                GetHomeTimelineAsync getTimeline = new GetHomeTimelineAsync();
                getTimeline.execute();

                try {
                    //update tweet list and cardView
                    jsonTweets = getTimeline.get();
                    updateCardView();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else {
            tweets.clear();
            cardTweetAdapter.notifyDataSetChanged();
        }
    }

    /**
     * loads json file to objects and updates cardView
     */
    private void updateCardView() {
        if (jsonTweets != null) {

            long startTime = System.currentTimeMillis();
            //read objects
            try {
            readJsonToObjects(jsonTweets);
            } catch (JSONException jse) {
                jse.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            //calculate total time and print
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            Log.d("object loading time", Long.toString(totalTime) + "ms");

            //tell the adapter that the list of tweets has changed
            cardTweetAdapter.notifyDataSetChanged();
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
     * Parses JSON file into objects and adds them to the list of tweets
     *
     * @param file name of the JSON file to be parsed
     */
    private void readJsonToObjects(String file) throws IOException, JSONException {

        //clear the current list of tweets
        tweets.clear();

        //create new jsonArray for reading the file
        JSONArray jTweetArray = new JSONArray(file);

        //loop through statuses
        for (int i = 0; i < jTweetArray.length(); i++) {
            JSONObject jTweetObj = jTweetArray.getJSONObject(i);

            //get basic status properties
            String id_str = jTweetObj.getString("id_str");
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
            tweets.add(new Tweets(user, text, retweets, createdAt, favourites, entities, id_str));
        }
    }
}