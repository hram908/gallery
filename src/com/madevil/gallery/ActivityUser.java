package com.madevil.gallery;

import com.madevil.gallery.R;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

public class ActivityUser extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	FragmentManager fm = this.getSupportFragmentManager();
	FragmentTransaction ft = fm.beginTransaction();
	FragmentUser f = new FragmentUser();
	ft.add(android.R.id.content, f);
	ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_user, menu);
	return true;
    }

}
