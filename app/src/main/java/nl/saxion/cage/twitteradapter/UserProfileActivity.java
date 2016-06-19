package nl.saxion.cage.twitteradapter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    Users user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        OAuth1AccessToken accessToken=null;
        //create intent
        Intent intent = getIntent();




            //get extras from intent
           accessToken = (OAuth1AccessToken) intent.getExtras().getSerializable("accessToken");


        //didn't create OAUTH10aService class because there is a default one
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

        //printing json file to logcat
        Log.d("resp",response.getBody());
        try {
            readJsonToObjects(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTheView();
        nameText.setText(user.getName());
        screenNameText.setText("@"+user.getScreen_name());
        //load profile image
        Picasso.with(this).load(user.getProfile_image_url()).into(profileImage);

        descriptionText.setText(user.getDescription());

        followers_countText.setText("followers: "+Integer.toString(user.getFollowers_count()));
        friends_countText.setText("following: "+Integer.toString(user.getFriends_count()));
        statuses_countText.setText("tweets posted: "+Integer.toString(user.getStatuses_count()));


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
        System.out.println("here is follower"+jsonObject.getInt("followers_count"));
        int friends_count = jsonObject.getInt("friends_count");
        int statuses_count = jsonObject.getInt("statuses_count");


            //create new user object with extracted json data
            user = new Users(screen_name, name, profile_image_url,description,followers_count,friends_count,statuses_count);
        }


    private void setTheView(){

        nameText = (TextView) findViewById(R.id.name);
        screenNameText = (TextView) findViewById(R.id.screen_name);
        profileImage = (ImageView) findViewById(R.id.profile_image_url);
        descriptionText = (TextView) findViewById(R.id.description);
        followers_countText = (TextView) findViewById(R.id.followers_count);
        friends_countText = (TextView) findViewById(R.id.friends_count);
        statuses_countText = (TextView) findViewById(R.id.statuses_count);

    }

    }


