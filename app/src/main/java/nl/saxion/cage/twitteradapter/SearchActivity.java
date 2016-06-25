package nl.saxion.cage.twitteradapter;

//imports
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.saxion.cage.twitteradapter.AsyncTasks.RetrieveBearerAsync;
import nl.saxion.cage.twitteradapter.AsyncTasks.SearchTwitterAsync;
import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Entities.Hashtags;
import nl.saxion.cage.twitteradapter.Entities.Media;
import nl.saxion.cage.twitteradapter.Entities.URL;
import nl.saxion.cage.twitteradapter.Entities.User_Mention;

public class SearchActivity extends AppCompatActivity {

    //list of tweets
    List<Tweets> tweets = new ArrayList<>();
    private EditText editSearch;
    private String bearerToken;
    private String searchJSON;
    private CardTweetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //define editText view for search bar
        editSearch = (EditText) findViewById(R.id.edit_search);

        //listens to key presses on keyboard
        TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    //search twitter and clear search bar
                    search(editSearch.getText().toString());
                    editSearch.setText("");

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    //the key has been pressed
                    return true;
                }
                return false;
            }
        };
        editSearch.setOnEditorActionListener(actionListener);

        //card view adapter
        adapter = new CardTweetAdapter(tweets, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_card);
        recyclerView.setHasFixedSize(true);

        //linear layout manager used for recyclerView (cardView)
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //set recyclerView layout manager & adapter
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    private void search(String searchTerm) {
        //check if we have bearerToken
        if (bearerToken == null) {

            //retrieve new bearerToken
            RetrieveBearerAsync retrieveToken = new RetrieveBearerAsync();
            retrieveToken.execute();

            //try to get the token
            try {
                System.out.println("getting token");
                bearerToken = retrieveToken.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //new searchTwitter async task
        SearchTwitterAsync searchTwitter = new SearchTwitterAsync();

        //execute task
        searchTwitter.execute(searchTerm, bearerToken);

        try {

            //update tweet list and cardView
            searchJSON = searchTwitter.get();
            System.out.println(searchJSON);
            updateCardView();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void updateCardView() {
        if (searchJSON != null) {
            //get start time
            long startTime = System.currentTimeMillis();

            //read objects
            try {
                readJsonStatusesToObjects(searchJSON);
            } catch (IOException ioe) {
            } catch (JSONException e) {
                try {
                    readJsonStatusesToObjects(searchJSON);
                } catch (JSONException e1) {
                } catch (IOException e1) {
                }
            }

            //get end time
            long endTime = System.currentTimeMillis();

            //calculate total time
            long totalTime = endTime - startTime;

            //print total time
            Log.d("object loading time", Long.toString(totalTime) + "ms");

            //update the card view
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Parses JSON file into objects
     *
     * @param file name of the JSON file to be parsed.
     */
    private void readJsonStatusesToObjects(String file) throws IOException, JSONException {
        //clear the current list of tweets
        tweets.clear();

        //create new jObject for reading the file
        JSONObject jObject = new JSONObject(file);
        System.out.println(jObject);

        //get statuses
        JSONArray jTweetArray = jObject.getJSONArray("statuses");

        System.out.println(jTweetArray);

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
