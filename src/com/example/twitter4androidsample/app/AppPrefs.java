package com.example.twitter4androidsample.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPrefs {
    private static final String SHARED_PREFS_NAME = "t_prefs";
    
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String ACCESS_TOKEN_SECRET = "access_token_secret";

    private static AppPrefs sInstance;

    private SharedPreferences mPrefs;

    private AppPrefs(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static AppPrefs get(Context context) {
        if (sInstance == null) {
            synchronized (AppPrefs.class) {
                if (sInstance == null) {
                    sInstance = new AppPrefs(context);
                }
            }
        }
        return sInstance;
    }

    public String getTwitterAccessTokenKey() {
        return mPrefs.getString(ACCESS_TOKEN_KEY, null);
    }

    public String getTwitterAccessTokenSecret() {
        return mPrefs.getString(ACCESS_TOKEN_SECRET, null);
    }
    
    public void setTwitterAccessToken(String accessTokenKey, String accessTokenSecret) {
        Editor editor = mPrefs.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessTokenKey);
        editor.putString(ACCESS_TOKEN_SECRET, accessTokenSecret);
        editor.commit();
    }
}
