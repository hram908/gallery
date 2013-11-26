package com.evilmadhacker.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import com.evilmadhacker.gallery.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ActivitySplash extends Activity {
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
	int timeout = 3000; // 3s
	if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	    timeout = 5000; // 0.5s
	}
	new Handler().postDelayed(new Runnable() {
	    @Override
	    public void run() {
		Intent mainIntent = new Intent(ActivitySplash.this,
			ActivityMain.class);
		startActivity(mainIntent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	    }
	}, timeout);
    }

}
