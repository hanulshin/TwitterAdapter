package nl.saxion.cage.twitteradapter;

import android.os.AsyncTask;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.*;
import com.github.scribejava.core.oauth.OAuth10aService;

/**
 * Created by Hanulshin on 2016-06-19.
 */
public class PostTweets extends AsyncTask<String,Void,Boolean>{
    private com.github.scribejava.core.oauth.OAuth10aService authService;

    OAuth1AccessToken accessToken;

    String tweet;

    //key & secret for getting accessToken
    private static final String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private static final String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... params) {

        accessToken = new OAuth1AccessToken(params[1],params[2]);
        tweet=params[0];
        postTweet();
        return true;
    }


    private void postTweet(){

        System.out.println("works" + tweet + accessToken.getToken() + accessToken.getTokenSecret());
//didn't create OAUTH10aService class because there is a default one
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance
        OAuthRequest request =
                new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/statuses/update.json", authService);
        request.addParameter("status", tweet);
        authService.signRequest(accessToken, request);
        Response response = request.send();
        if (response.isSuccessful()) {
            System.out.println("I can");
            String res = response.getBody();
            // Do something with res...
        }
    }
}
