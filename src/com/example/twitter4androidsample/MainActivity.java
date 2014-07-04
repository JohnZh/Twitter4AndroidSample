package com.example.twitter4androidsample;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    public static final String  CALLBACK           = "http://pix2paint.com";
    public static final String  OAUTH_VERIFIER     = "oauth_verifier";
    public static final String  AUTHENTICATION_URL = "authentication_url";
    
    private static final String CONSUMER_KEY       = "p8ouOBF1PijpT8z9BevbVoh4T";
    private static final String CONSUMER_SECRET    = "ssQyasHfyK3JN6olSWzeZ9NfzWTvIJMLP6102BOsg7owsC5v6i";
    
    private static final int REQ_TWITTER = 1;

    private AppPrefs            mPrefs;
    private Twitter             mTwitter;
    private RequestToken        mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = AppPrefs.get(this);
        initializeTwitter();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    private void initializeTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(CONSUMER_KEY);
        builder.setOAuthConsumerSecret(CONSUMER_SECRET);
        Configuration conf = builder.build();
        mTwitter = new TwitterFactory(conf).getInstance();
    }

    private void sendTwitterMessage() {
        String accessTokenKey = mPrefs.getTwitterAccessTokenKey();
        String accessTokenSecret = mPrefs.getTwitterAccessTokenSecret();
        if (accessTokenKey == null && accessTokenSecret == null) {
            reOAuth();
        } else {
            mTwitter.setOAuthAccessToken(new AccessToken(accessTokenKey, accessTokenSecret));
            new UpdateTwitterStatus().execute("Test from pix2paint.");
        }
    }

    private void reOAuth() {
        mTwitter.setOAuthAccessToken(null);
        new GetOAuthRequestTokenTask().execute();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.d("John", "onActivityResult data = " + data == null ? "null" : data.toString());
        
        if (resultCode == RESULT_OK && requestCode == REQ_TWITTER) {
            String oauthVerifier = data.getStringExtra(OAUTH_VERIFIER);
            new GetOAuthAccessTokenTask().execute(oauthVerifier);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class GetOAuthRequestTokenTask extends AsyncTask<Void, Void, RequestToken> {

        @Override
        protected RequestToken doInBackground(Void... arg0) {
            try {
                return mTwitter.getOAuthRequestToken(CALLBACK);
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e("John", e.getMessage());
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(RequestToken result) {
            super.onPostExecute(result);
            mRequestToken = result;
            if (mRequestToken != null) {
                Intent intent = new Intent(MainActivity.this, WebsiteActivity.class);
                intent.putExtra(AUTHENTICATION_URL, mRequestToken.getAuthenticationURL());
                startActivityForResult(intent, REQ_TWITTER);
            }
        }
    }
    
    private class GetOAuthAccessTokenTask extends AsyncTask<String, Void, AccessToken> {

        @Override
        protected AccessToken doInBackground(String... params) {
            try {
                return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e("John", e.getErrorMessage());
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(AccessToken accessToken) {
            super.onPostExecute(accessToken);
            if (accessToken != null) {
                mTwitter.setOAuthAccessToken(accessToken);
                mPrefs.setTwitterAccessToken(accessToken.getToken(), accessToken.getTokenSecret());
            }
        }
    }
    
    private class UpdateTwitterStatus extends AsyncTask<String, Void, Status> {

        @Override
        protected twitter4j.Status doInBackground(String... params) {
            try {
                return mTwitter.updateStatus(params[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.e("John", "TwitterException e");
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(twitter4j.Status result) {
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(MainActivity.this, "Status:" + result.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getView().findViewById(R.id.twitter).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.sendTwitterMessage();
                }
            });
        }
    }
}
