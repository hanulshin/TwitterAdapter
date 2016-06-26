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
 * Created by Hanulshin on 2016-06-25.
 */
public class LikeTweetAsync  extends AsyncTask <String,Void,Void> {

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
     * gets user timeline
     * @param params
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        Model model = Model.getInstance();
        String id_str = params[0];
       // OAuth1AccessToken accessToken = new OAuth1AccessToken(params[1], params[2]);
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance

        final OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/favorites/create.json"+"?id="+id_str, authService);
        authService.signRequest(model.getAccessToken(), request);

        final Response response = request.send();
        System.out.println("Like mark has been created, contents of the tweet - "+response.getBody());

        return null;
    }
}
