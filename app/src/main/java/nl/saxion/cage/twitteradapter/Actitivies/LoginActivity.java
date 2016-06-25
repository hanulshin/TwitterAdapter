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
    //define views
    private WebView webView = null;
    private Activity myActivity = null;
    private ProgressDialog mDialog = null;

    static Model model = Model.getInstance();

    //key & secret for getting accessToken
    private static final String API_KEY = model.getApiKey();
    private static final String API_SECRET = model.getApiSecret();

    //for oAuth authorization
    private com.github.scribejava.core.oauth.OAuth10aService authService;
    private OAuth1RequestToken requestToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myActivity = this;

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Button button = (Button) findViewById(R.id.twitter_login_btn);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.web_view_login);
                //didn't create OAUTH10aService class because there is a default one
                authService =
                        new ServiceBuilder()
                                .apiKey(API_KEY)
                                .apiSecret(API_SECRET)
                                .callback("http://www.cagitter.com"/*OAUTH_CALLBACK_URL*/)// not used in git, but said to use in slides
                                .build(TwitterApi.instance());//changed from API to api, getInstance to instance
                requestToken = authService.getRequestToken();
                String authUrl = authService.getAuthorizationUrl(requestToken);

                webView = (WebView) findViewById(R.id.webView);
                webView.setWebViewClient(new MyWebViewClient());
                webView.loadUrl(authUrl);

//                final OAuth1AccessToken accessToken = authService.getAccessToken(requestToken, verifier);// "verifier you got frAom the user/callback"
//                final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json", authService);
//                authService.signRequest(accessToken, request); // the access token from step 4
//                final Response response = request.send();
//                System.out.println(response.getBody());
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mDialog == null)
                mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Loading..");

            if (!(myActivity.isFinishing())) {
                mDialog.show();
            }
        }

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