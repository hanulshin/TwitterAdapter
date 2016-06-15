package nl.saxion.cage.twitteradapter;

import android.os.AsyncTask;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

/**
 * Created by Hanulshin on 2016-06-15.
 */
public class AuthorizeUserAsync extends AsyncTask{
    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }
    private void login(){
                        final OAuth1AccessToken accessToken = authService.getAccessToken(requestToken, verifier);// "verifier you got frAom the user/callback"
                final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", authService);
                authService.signRequest(accessToken, request); // the access token from step 4
                final Response response = request.send();
                System.out.println(response.getBody());
    }
}
