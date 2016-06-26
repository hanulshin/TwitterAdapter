package nl.saxion.cage.twitteradapter.Actitivies;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.saxion.cage.twitteradapter.AsyncTasks.GetUserTimelineAsync;
import nl.saxion.cage.twitteradapter.Adapters.CardTweetAdapter;
import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Entities.Hashtags;
import nl.saxion.cage.twitteradapter.Entities.Media;
import nl.saxion.cage.twitteradapter.Entities.URL;
import nl.saxion.cage.twitteradapter.Entities.User_Mention;
import nl.saxion.cage.twitteradapter.AsyncTasks.PostTweetsAsync;
import nl.saxion.cage.twitteradapter.Model;
import nl.saxion.cage.twitteradapter.R;
import nl.saxion.cage.twitteradapter.Tweets.Tweets;
import nl.saxion.cage.twitteradapter.Users;

public class UserProfileActivity extends AppCompatActivity {
    private com.github.scribejava.core.oauth.OAuth10aService authService;

    /**
     * static model instance for getting keys
     */
    static Model model = Model.getInstance();

    /**
     * api key for signing requests
     */
    private static final String API_KEY = model.getApiKey();

    /**
     * api secret for signing requests
     */
    private static final String API_SECRET = model.getApiSecret();

    /**
     * cardView adapter for tweets
     */
    private CardTweetAdapter adapter;

    /**
     * accessToken for signing requests
     */
    private OAuth1AccessToken accessToken = model.getAccessToken();

    /**
     * String json file containing list of tweets
     */
    private String jsonTweets;

    /**
     * list of tweets
     */
    List<Tweets> tweets = new ArrayList<>();

    /**
     * current user object, containing user information
     */
    Users user;

    /**
     *
     */
    private EditText tweetField;

    /**
     * button for logging out
     */
    private Button logoutButton;

    /**
     * set cardView adapter, get intent extras, load user timeline,
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //set views
        Button postButton = (Button) findViewById(R.id.post);
        Button friends_button = (Button) findViewById(R.id.button_friends);
        TextView nameText = (TextView) findViewById(R.id.name);
        TextView screenNameText = (TextView) findViewById(R.id.screen_name);
        TextView descriptionText = (TextView) findViewById(R.id.description);
        TextView followers_countText = (TextView) findViewById(R.id.followers_count);
        TextView friends_countText = (TextView) findViewById(R.id.friends_count);
        TextView statuses_countText = (TextView) findViewById(R.id.statuses_count);
        ImageView profileImage = (ImageView) findViewById(R.id.profile_image_url);
        tweetField = (EditText) findViewById(R.id.tweet_edittext);
        logoutButton = (Button) findViewById(R.id.b_logout);

        //cardView adapter
        adapter = new CardTweetAdapter(tweets, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.profile_list);
        recyclerView.setHasFixedSize(true);

        //linear layout manager used for recyclerView (cardView)
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //set recyclerView layout manager & adapter
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        //get tweets user has made
        getUserTimeline();

        //set authService
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance

        //query twitter for user information
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", authService);
        authService.signRequest(accessToken, request);
        final Response response = request.send();

        //read response of query to objects
        try {
            readJsonToObjects(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assert postButton != null;
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new post tweets asyncTask
                PostTweetsAsync post = new PostTweetsAsync();
                post.execute(tweetField.getText().toString(), accessToken.getToken(), accessToken.getTokenSecret());

                //clear editText
                tweetField.setText("");

                //refresh user timeline
                getUserTimeline();
            }
        });

        assert logoutButton != null;
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log out
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.removeAllCookies(null);
                    model.setAccessToken(null);
                    model.setLoggedIn(false);
                    System.out.println("logged out");
                    finish();
                }
            }
        });

        //setViews
        nameText.setText(user.getName());
        screenNameText.setText("@" + user.getScreen_name());
        descriptionText.setText(user.getDescription());
        followers_countText.setText("followers: " + Integer.toString(user.getFollowers_count()));
        friends_countText.setText("following: " + Integer.toString(user.getFriends_count()));
        statuses_countText.setText("tweets posted: " + Integer.toString(user.getStatuses_count()));

        //load profile image
        Picasso.with(this).load(user.getProfile_image_url()).into(profileImage);

        friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load friendsList activity
                Intent friendsListIntent = new Intent(getApplicationContext(), FriendsListActivity.class);
                friendsListIntent.putExtra("accessToken", accessToken);
                startActivity(friendsListIntent);
            }
        });
    }

    /**
     * Parses JSON file into objects
     *
     * @param file name of the JSON file to be parsed.
     */
    private void readJsonToObjects(String file) throws IOException, JSONException {
        //create new jsonObject for reading the file
        JSONObject jsonObject = new JSONObject(file);
        String name = jsonObject.getString("name");
        String screen_name = jsonObject.getString("screen_name");
        String profile_image_url = jsonObject.getString("profile_image_url");
        String description = jsonObject.getString("description");
        int followers_count = jsonObject.getInt("followers_count");
        int friends_count = jsonObject.getInt("friends_count");
        int statuses_count = jsonObject.getInt("statuses_count");

        //create new user object with extracted json data
        user = new Users(screen_name, name, profile_image_url, description, followers_count, friends_count, statuses_count);
    }

    /**
     * loads user timeline (tweets the user has posted)
     */
    private void getUserTimeline() {
        if (accessToken != null) {
            GetUserTimelineAsync getTimeline = new GetUserTimelineAsync();
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
    }

    /**
     * reads json file to objects, logs time it took
     * updates list of objects
     */
    private void updateCardView() {
        if (jsonTweets != null) {
            //get start time
            long startTime = System.currentTimeMillis();

            //read objects
            try {
                readJsonStatusesToObjects(jsonTweets);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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
        //create new jsonObject for reading the file
        JSONArray jTweetArray = new JSONArray(file);
        System.out.println(jTweetArray);
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
            tweets.add(new Tweets(user, text, retweets, createdAt, favourites, entities,id_str));
        }
    }
}

