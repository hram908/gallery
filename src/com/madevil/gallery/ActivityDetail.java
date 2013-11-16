package com.madevil.gallery;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public class ActivityDetail extends BasicActivity {
    FragmentPicture mFragment = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    NavUtils.navigateUpFromSameTask(this);
	    return true;
	default:
	    return mFragment.onOptionsItemSelected(item);
	}
    }

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	DataShare share = DataShare.Ins(getApplicationContext());
	
	int index = getIntent().getIntExtra(DataPicture.intentIndex, 0);
	ArrayList<DataPicture> pictures = getIntent().getParcelableArrayListExtra(DataPicture.intentPictures);	
	Log.d("ActivityDetail", "pictures.size=" + pictures.size() + ", index=" + index);
	
        mFragment = FragmentPicture.Ins(pictures, index);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, mFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.activity_detail, menu);
	return true;
    }

}
