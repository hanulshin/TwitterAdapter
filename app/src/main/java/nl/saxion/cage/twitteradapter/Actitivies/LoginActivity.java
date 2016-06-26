package nl.saxion.cage.twitteradapter.Actitivies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

import nl.saxion.cage.twitteradapter.Model;
import nl.saxion.cage.twitteradapter.R;

public class LoginActivity extends AppCompatActivity {

    private WebView webView = null;

    /**
     * instance of model for getting api key and secret
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
     * request token for signing requests
     */
    private OAuth1RequestToken requestToken = null;

    /**
     * set auth service, get request token,
     * start webView for logging in
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_login);

        //strict thread policy
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //set authService
        authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance

        //set the request Token
        requestToken = authService.getRequestToken();

        //set the authorization url
        String authUrl = authService.getAuthorizationUrl(requestToken);

        //set the webView and load url
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(authUrl);

    }

    /**
     * override of pressing back button
     */
    @Override
    public void onBackPressed() {
        //do not let the user back out
    }

    /**
     * inner WebView class for handling logging in
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            String verifier = uri.getQueryParameter("oauth_verifier");
            Intent resultIntent = new Intent();

            //getting the verifier if user login and password are correct
            resultIntent.putExtra("oauth_verifier", verifier);
            setResult(RESULT_OK, resultIntent);

            //retrieving access token, which will allow us to permanently access user account
            final OAuth1AccessToken accessToken = authService.getAccessToken(requestToken, verifier);

            //pending for json file which will contain user information
            final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", authService);
            authService.signRequest(accessToken, request); // the access token from step 4
            final Response response = request.send();

            //new intent for sending info back to feed class
            Intent intent = new Intent();

            //add access token
            intent.putExtra("accessToken", accessToken);
            setResult(RESULT_OK, intent);

            //finish current activity
            finish();

            return false;
        }
    }
}