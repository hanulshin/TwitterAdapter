package nl.saxion.cage.twitteradapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

/**
 * Created by Hanulshin on 2016-06-15.
 */
public class AuthorizeUserAsync extends AsyncTask <WebView, String, String> {
    private WebView webView = null;
    private Activity mActivity = null;
    private ProgressDialog mDialog = null;
    private static final String API_KEY = "BABNgm313dL2rRXf3iRM11lL8";
    private static final String API_SECRET = "WR2VFNTaJBRGmDCUettxUGPss50ZPOQaVlO8wsUYoHPMKlQkrG";
    private static final String TAG = LoginActivity.class
            .getSimpleName();
    static String verifier = null;

    @Override
    protected String doInBackground(WebView... params) {
        login(params);
        return null;
    }

    private String login(WebView[] webViews) {
        com.github.scribejava.core.oauth.OAuth10aService authService =
                new ServiceBuilder()
                        .apiKey(API_KEY)
                        .apiSecret(API_SECRET)
                        .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                        .build(TwitterApi.instance());//changed from API to api, getInstance to instance
        final OAuth1RequestToken requestToken = authService.getRequestToken();
        final String authUrl = authService.getAuthorizationUrl(requestToken);

        webView = webViews[0];

        webView.setWebViewClient(new MyWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                verifier = uri.getQueryParameter("oauth_verifier");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("oauth_verifier", verifier);
                Log.d("msg", verifier);
                return false;
            }
        });

        webView.loadUrl(authUrl);

        final OAuth1AccessToken accessToken = authService.getAccessToken(requestToken, verifier);// "verifier you got frAom the user/callback"
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", authService);
        authService.signRequest(accessToken, request); // the access token from step 4
        final Response response = request.send();
        System.out.println(response.getBody());
        return response.getBody();
    }

    @Override
    protected void onPostExecute(String body) {
        super.onPostExecute(body);
    }
}
