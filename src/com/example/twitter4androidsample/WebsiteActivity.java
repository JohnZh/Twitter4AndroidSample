package com.example.twitter4androidsample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebsiteActivity extends Activity {
    
    private WebView mWebview;
    private ProgressBar mProgressbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);
        initializeViews();
    }
    
    private void initializeViews() {
        mWebview = (WebView) findViewById(R.id.webview);
        mProgressbar = (ProgressBar) findViewById(R.id.progress);
        findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setWebViewClient(new OAuthWebViewClient());
        mWebview.loadUrl(getIntent().getExtras().getString(MainActivity.AUTHENTICATION_URL));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        return false;
    }

    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("John", "Url = " + url);
            mProgressbar.setVisibility(View.GONE);
            if (url != null && url.startsWith(MainActivity.CALLBACK)) {
                String oauthVerifer = Uri.parse(url).getQueryParameter(MainActivity.OAUTH_VERIFIER);
                Log.d("John", "OAauthVerifer= " + oauthVerifer);
                Intent intent = new Intent();
                intent.putExtra(MainActivity.OAUTH_VERIFIER, oauthVerifer);
                setResult(RESULT_OK, intent);
                Log.d("John", "Before finish");
                finish();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressbar.setVisibility(View.VISIBLE);
        }
    }
    
}
