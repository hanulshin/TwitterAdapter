package nl.saxion.cage.twitteradapter.AsyncTasks;

import android.os.AsyncTask;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

import nl.saxion.cage.twitteradapter.Model;

public class GetUserTimelineAsync extends AsyncTask<String, Void, String> {

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
     * accessToken for signing requests
     */
    private OAuth1AccessToken accessToken = model.getAccessToken();

    /**
     * authService for authenticating requests
     */
    private com.github.scribejava.core.oauth.OAuth10aService authService;

    /**
     * gets user timeline
     * @param params api key
     * @return json file which contains user recent posts
     */
    @Override
    protected String doInBackground(String... params) {
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/statuses/user_timeline.json", authService);
        authService.signRequest(accessToken, request);
        final Response response = request.send();
        return response.getBody();
    }
}
