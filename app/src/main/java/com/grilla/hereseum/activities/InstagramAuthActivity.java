package com.grilla.hereseum.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grilla.hereseum.R;
import com.grilla.hereseum.helper.SharedPrefsHelper;
import com.grilla.hereseum.helper.TaskCreator;

import bolts.Continuation;
import bolts.Task;

public class InstagramAuthActivity extends AppCompatActivity {

    public static final String INSTA_CLIENT_ID = "4235678fb514466d806fcae131fd9af4";
    public static final String INSTA_CLIENT_SECRET = "a235c681c9e04fe781fcc03ffda7c7a4";
    public static final String INSTA_REDIRECT_URI = "http://hereseum/redirect";
    private static final String INSTA_AUTH_URL = "https://api.instagram.com/oauth/authorize/?client_id=" +
            INSTA_CLIENT_ID + "&redirect_uri=" +
            INSTA_REDIRECT_URI + "&response_type=code" +
            "&scope=basic";
    private static final String PARAM_CODE = "code";
    private static final String PARAM_ERROR = "error";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_auth);

        String accessToken = SharedPrefsHelper.getString(this, SharedPrefsHelper.KEY_ACCESS_TOKEN);
        if (accessToken != null) {
            // Access token already saved, skip auth
            loadMainScreen(accessToken);
            return;
        }

        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.loadUrl(INSTA_AUTH_URL);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Uri uri = Uri.parse(url);
                String authCode = uri.getQueryParameter(PARAM_CODE);
                if (!TextUtils.isEmpty(authCode)) {
                    TaskCreator.getInstance(InstagramAuthActivity.this).getAccessTokenTask(authCode)
                        .continueWith(new Continuation<String, Void>() {
                            @Override
                            public Void then(Task<String> task) throws Exception {
                                if (task.isFaulted()) {
                                    authCodeError();
                                    return null;
                                }

                                String accessToken = task.getResult();
                                SharedPrefsHelper.putString(InstagramAuthActivity.this, SharedPrefsHelper.KEY_ACCESS_TOKEN, accessToken);
                                loadMainScreen(accessToken);
                                return null;
                            }
                        });
                    authCodeReceived();
                } else if (!TextUtils.isEmpty(uri.getQueryParameter(PARAM_ERROR))) {
                    authCodeError();
                }
            }
        });
    }

    private void loadMainScreen(String accessToken) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.EXTRA_ACCESS_TOKEN, accessToken);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    // Replace web view with loading screen
    void authCodeReceived() {
        mWebView.setVisibility(View.GONE);
        TextView uberLoadingText = (TextView)findViewById(R.id.insta_loading_text);
        ProgressBar spinner = (ProgressBar)findViewById(R.id.insta_loading_spinner);

        uberLoadingText.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);

        // wait for access code to be received
    }

    // Return to previous screen
    void authCodeError() {
        Toast.makeText(getApplicationContext(), getString(R.string.auth_error), Toast.LENGTH_SHORT).show();
        finish();
    }
}
