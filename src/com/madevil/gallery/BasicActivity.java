package com.madevil.gallery;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.google.analytics.tracking.android.EasyTracker;

class BasicActivity extends ActionBarActivity {

    protected void onStart() {
	super.onStart();
	EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    protected void onResume() {
	super.onResume();
	MobclickAgent.onResume(this);
	Log.d("BasicActivity", "OnResume(), " + this.getClass().getName());
    }

    @Override
    protected void onPause() {
	super.onPause();
	MobclickAgent.onPause(this);
	Log.d("BasicActivity", "OnPause(), " + this.getClass().getName());
    }
    
    protected void onStop() {
	super.onStop();
	EasyTracker.getInstance().activityStop(this);
    }
}
