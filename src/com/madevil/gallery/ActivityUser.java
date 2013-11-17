package com.madevil.gallery;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityUser extends BasicActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    NavUtils.navigateUpFromSameTask(this);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	
	DataUser user = getIntent().getParcelableExtra(DataUser.intent);

	FragmentManager fm = this.getSupportFragmentManager();
	FragmentTransaction ft = fm.beginTransaction();
	FragmentUser f = FragmentUser.Ins(user);
	ft.add(android.R.id.content, f);
	ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_user, menu);
	return true;
    }

}
