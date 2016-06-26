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

    @Override
    protected Boolean doInBackground(String... params) {
        postTweet(params[0]);
        return true;
    }

    /**
     * posts a tweet to twitter
     * @param tweetText
     */
    private void postTweet(String tweetText) {
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance
        OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/statuses/update.json", authService);
        request.addParameter("status", tweetText);
        authService.signRequest(accessToken, request);
        request.send();
    }
}
