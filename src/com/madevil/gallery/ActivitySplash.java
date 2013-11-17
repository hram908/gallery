package com.madevil.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ActivitySplash extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟三秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
	super.onPostCreate(savedInstanceState);
	new Handler().postDelayed(new Runnable() {
	    @Override
	    public void run() {
		Intent mainIntent = new Intent(ActivitySplash.this,
			ActivityMain.class);
		startActivity(mainIntent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	    }
	}, SPLASH_DISPLAY_LENGHT);
    }

}
