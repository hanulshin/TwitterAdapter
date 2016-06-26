package nl.saxion.cage.twitteradapter.AsyncTasks;

import android.os.AsyncTask;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

import nl.saxion.cage.twitteradapter.Model;

/**
 * Created by Hanulshin on 2016-06-25.
 */
public class UnlikeTweetAsync  extends AsyncTask<String,Void,Void> {

    //key & secret for getting accessToken
    private static final String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private static final String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";

    /**
     * authService for authenticating requests
     */
    private com.github.scribejava.core.oauth.OAuth10aService authService;


    /**
     * unlike tweet that has been chosen
     * @param params
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        Model model = Model.getInstance();

        //id of the tweet user choose to unlike
        String id_str = params[0];
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance

        final OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/favorites/destroy.json"+"?id="+id_str, authService);
        authService.signRequest(model.getAccessToken(), request); // the access token from step 4

        final Response response = request.send();//record the response from server
        System.out.println("Like mark has been deleted, contents of the tweet - "+response.getBody());


        return null;
    }
}
