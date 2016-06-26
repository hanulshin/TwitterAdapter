package nl.saxion.cage.twitteradapter.AsyncTasks;

import android.os.AsyncTask;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.*;
import com.github.scribejava.core.oauth.OAuth10aService;

import nl.saxion.cage.twitteradapter.Model;

/**
 * Created by Hanulshin on 2016-06-19.
 */
public class PostTweetsAsync extends AsyncTask<String, Void, Boolean> {
    private com.github.scribejava.core.oauth.OAuth10aService authService;

    /**
     * accessToken for signing requests
     */
    OAuth1AccessToken accessToken = model.getAccessToken();

    String tweet;

    /**
     * static model instance for getting keys
     */
    static Model model = Model.getInstance();

    //key & secret for getting accessToken
    private static final String API_KEY = model.getApiKey();
    private static final String API_SECRET = model.getApiSecret();

    @Override
    protected Boolean doInBackground(String... params) {
        accessToken = new OAuth1AccessToken(params[1], params[2]);
        tweet = params[0];
        postTweet();
        return true;
    }

    private void postTweet() {

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
