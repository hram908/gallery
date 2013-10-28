package com.madevil.gallery;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.madevil.gallery.ActivityMain.ViewAdapter;
import com.madevil.gallery.PictureAdapter.ViewHolder;
import com.squareup.picasso.Picasso;

class PicturePagerAdapter extends FragmentPagerAdapter {
    private DataShare share = null;

    public PicturePagerAdapter(FragmentManager fm, Context c) {
	super(fm);
	share = DataShare.Ins(c);
    }

    @Override
    public Fragment getItem(int index) {
	DataPicture picture = share.pictures.get(index);
	Fragment f = FragmentPicture.Ins(picture);
	return f;
    }

    @Override
    public int getCount() {
	return share.pictures.size();
    }
}


public class ActivityDetail extends ActionBarActivity {
    private Context mContext = null;    
    private ActionBar mBar = null;
    private DataShare share = null;
    private PicturePagerAdapter mAdapter = null;
    private ViewPager mPager = null;
    
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
	setContentView(R.layout.activity_detail);
	this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	mBar = getSupportActionBar();
	mContext = getApplicationContext();
	share = DataShare.Ins(mContext);
	
	Intent intent = getIntent();
	int index = intent.getIntExtra("index", 0);

	mAdapter = new PicturePagerAdapter(getSupportFragmentManager(), mContext);
	mPager = (ViewPager) findViewById(R.id.detail_layout_pager);
	mPager.setAdapter(mAdapter);
	mPager.setCurrentItem(index);
	mPager.setOnPageChangeListener(new OnPageChangeListener() {
	    @Override
	    public void onPageScrollStateChanged(int arg0) {
	    }

	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2) {
	    }

	    @Override
	    public void onPageSelected(int index) {
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.activity_detail, menu);
	return true;
    }
    
    
}
