package com.example.twitter4androidsample.util;

import java.util.WeakHashMap;

import android.app.Activity;
import android.app.ProgressDialog;

public class ProgressHelper {

	private static ProgressHelper mInstance;
	
	private ProgressDialog progressDialog;
	
	private WeakHashMap<Activity, ProgressDialog> map = new WeakHashMap<Activity, ProgressDialog>();
	
	public static ProgressHelper getInstance() {
		if (mInstance == null) {
			synchronized (ProgressHelper.class) {
				if (mInstance == null) {
					mInstance = new ProgressHelper();
				}
			}
		}
		return mInstance;
	}
	
	public void show(Activity activity, String text) {
		if (activity != null) {
			synchronized (activity) {
				if (map.containsKey(activity)) {
					progressDialog = map.get(activity);
					progressDialog.setMessage(text);
					progressDialog.show();
				} else {
					progressDialog = ProgressDialog.show(activity, "", text, true, false);
					map.put(activity, progressDialog);
				}
			}
		}
	}
	
	public void cancel(Activity activity) {
		progressDialog = map.get(activity);
		if (progressDialog != null) {
			progressDialog.dismiss();
			map.remove(activity);
		}
	}
}