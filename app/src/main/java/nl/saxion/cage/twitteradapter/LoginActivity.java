package nl.saxion.cage.twitteradapter;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.*;

public class LoginActivity extends AppCompatActivity {
    private static final String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private static final String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Button button= (Button) findViewById(R.id.twitter_login_btn);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//didn't create OAUTH10aService class because there is a default one
                com.github.scribejava.core.oauth.OAuth10aService authService =
                        new ServiceBuilder()
                                .apiKey(API_KEY)
                                .apiSecret(API_SECRET)
                                .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                                .build(TwitterApi.instance());//changed from API to api, getInstance to instance
                final OAuth1RequestToken requestToken = authService.getRequestToken();
                String authUrl = authService.getAuthorizationUrl(requestToken);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
                startActivity(browserIntent);
                Log.d("auth",authUrl);
                final OAuth1AccessToken accessToken = authService.getAccessToken(requestToken, "");// "verifier you got frAom the user/callback"
                final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", authService);
                authService.signRequest(accessToken, request); // the access token from step 4
                final Response response = request.send();
                System.out.println(response.getBody());
            }
        });

    }
}
