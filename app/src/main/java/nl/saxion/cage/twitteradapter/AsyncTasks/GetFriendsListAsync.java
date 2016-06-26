package nl.saxion.cage.twitteradapter.AsyncTasks;

import android.os.AsyncTask;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

import nl.saxion.cage.twitteradapter.Model;

/**
 * Created by Cage on 6/19/16.
 */
public class GetFriendsListAsync extends AsyncTask<String, Void, String> {

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
     * authService for authenticating requests
     */
    private com.github.scribejava.core.oauth.OAuth10aService authService;

    /**
     * get friends list
     * @param params
     * @return json file which contains list of user friends
     */
    @Override
    protected String doInBackground(String... params) {
        OAuth1AccessToken accessToken = new OAuth1AccessToken(params[0], params[1]);
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance

        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/friends/list.json", authService);
        authService.signRequest(accessToken, request);
        final Response response = request.send();
        return response.getBody();
    }
}
