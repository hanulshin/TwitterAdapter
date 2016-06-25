package nl.saxion.cage.twitteradapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Entities.Hashtags;
import nl.saxion.cage.twitteradapter.Entities.Media;
import nl.saxion.cage.twitteradapter.Entities.URL;
import nl.saxion.cage.twitteradapter.Entities.User_Mention;

public class UserProfileActivity extends AppCompatActivity {
    private com.github.scribejava.core.oauth.OAuth10aService authService;
    //key & secret for getting accessToken
    private static final String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private static final String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";

    protected TextView nameText;
    protected TextView screenNameText;
    protected ImageView profileImage;
    protected TextView descriptionText;
    protected TextView followers_countText;
    protected TextView friends_countText;
    protected TextView statuses_countText;

    //adapter for cardView
    private CardAdapter adapter;

    //access and bearer token
    private OAuth1AccessToken accessToken;
    private String bearerToken = null;

    //current json file of tweets
    private String searchJSON;

    //list of tweets
    List<Tweets> tweets = new ArrayList<>();

    Users user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //card view adapter
        adapter = new CardAdapter(tweets, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.profile_list);
        recyclerView.setHasFixedSize(true);

        //linear layout manager used for recyclerView (cardView)
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //set recyclerView layout manager & adapter
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        //create intent
        Intent intent = getIntent();

        //get extras from intent
        accessToken = (OAuth1AccessToken) intent.getExtras().getSerializable("accessToken");

        getUserTimeline();

        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance

        //pending for json file which will contain user information
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", authService);
        authService.signRequest(accessToken, request); // the access token from step 4
        final Response response = request.send();

        //print json file to logcat
        Log.d("resp", response.getBody());
        try {
            readJsonToObjects(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button post = (Button) findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostTweets post = new PostTweets();
                EditText tweet = (EditText) findViewById(R.id.tweet);
                post.execute(tweet.getText().toString(), accessToken.getToken(), accessToken.getTokenSecret());
                tweet.setText("");
                getUserTimeline();

//
//                Toast toast = new Toast(context);
//                toast.setText("Posted to your timeline");
//                toast.show();
            }
        });

        nameText = (TextView) findViewById(R.id.name);
        screenNameText = (TextView) findViewById(R.id.screen_name);
        profileImage = (ImageView) findViewById(R.id.profile_image_url);
        descriptionText = (TextView) findViewById(R.id.description);
        followers_countText = (TextView) findViewById(R.id.followers_count);
        friends_countText = (TextView) findViewById(R.id.friends_count);
        statuses_countText = (TextView) findViewById(R.id.statuses_count);

        nameText.setText(user.getName());
        screenNameText.setText("@" + user.getScreen_name());
        //load profile image
        Picasso.with(this).load(user.getProfile_image_url()).into(profileImage);
        descriptionText.setText(user.getDescription());
        followers_countText.setText("followers: " + Integer.toString(user.getFollowers_count()));
        friends_countText.setText("following: " + Integer.toString(user.getFriends_count()));
        statuses_countText.setText("tweets posted: " + Integer.toString(user.getStatuses_count()));
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
        System.out.println("here is follower" + jsonObject.getInt("followers_count"));
        int friends_count = jsonObject.getInt("friends_count");
        int statuses_count = jsonObject.getInt("statuses_count");

        //create new user object with extracted json data
        user = new Users(screen_name, name, profile_image_url, description, followers_count, friends_count, statuses_count);
    }

    private void getUserTimeline() {
        System.out.println("getting user timeline");
        if (accessToken != null) {
            GetUserTimelineAsync getTimeline = new GetUserTimelineAsync();
            getTimeline.execute(accessToken.getToken(), accessToken.getTokenSecret());
            try {

                //update tweet list and cardView
                searchJSON = getTimeline.get();

                System.out.println("searchJson: " + searchJSON);

                updateCardView();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else System.out.println("accesstoken null");
    }

    private void updateCardView() {
        System.out.println("updating cardView");
        if (searchJSON != null) {
            //get start time
            long startTime = System.currentTimeMillis();

            //read objects
            try {
                //readJsonTest(searchJSON);
                readJsonStatusesToObjects(searchJSON);
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                System.out.println("wrdffeasvr");
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("help me");
            }

            //get end time
            long endTime = System.currentTimeMillis();

            //calculate total time
            long totalTime = endTime - startTime;

            //print total time
            Log.d("object loading time", Long.toString(totalTime) + "ms");

            //update the card view
            adapter.notifyDataSetChanged();
            System.out.println("tweets" + tweets);

            System.out.println("data set changed");
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
        //System.out.println(jsonObject);
        System.out.println("WTF");

        //get statuses
        //JSONArray jTweetArray = null;// = jsonObject.optJSONArray("");

        System.out.println(jTweetArray);

//        if (jTweetArray == null) {
//            jTweetArray = jsonObject.optJSONArray("");
//        }

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

    private void readJsonTest(String file) throws IOException, JSONException{
        System.out.println("yay");
        //JSONObject jsonObject = new JSONObject(file);
        JSONArray jsonArray = new JSONArray();
        //JSONArray jsonArray = jsonObject.getJSONArray("");
        Log.d("what","what");
        System.out.println(jsonArray);
        System.out.println("poop");
    }
}


