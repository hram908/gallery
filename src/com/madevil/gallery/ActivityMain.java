package com.madevil.gallery;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;
import com.madevil.gallery.R;
import com.tencent.tauth.Tencent;

class ViewAdapter extends FragmentPagerAdapter {
    public static final int ITEM_FEEDS = 0;
    public static final int ITEM_INDEX = 1;
    public static final int ITEM_MYSELF = 2;
    public static final int ITEM_COUNT = 3;

    public ViewAdapter(FragmentManager fm) {
	super(fm);
	// TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int index) {
	switch (index) {
	case ITEM_INDEX:
	    return new FragmentIndex();
	case ITEM_FEEDS:
	    return new FragmentFeeds();
	case ITEM_MYSELF:
	    return new FragmentMyself();
	}
	return null;
    }

    @Override
    public int getCount() {
	return ITEM_COUNT;
    }

}

public class ActivityMain extends FragmentActivity {
    private ViewAdapter mViewAdapter;
    private ViewPager mPager;
    private Button mButtonIndex, mButtonFeeds, mButtonMyself;
    private DataShare share = null;

    /*
    protected void onSaveInstanceState (Bundle outState) {
	outState.putSerializable("DataShare", DataShare.Ins(getApplicationContext()));
    }
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
	DataShare share = DataShare.Ins(getApplicationContext());
	share = (DataShare) savedInstanceState.getSerializable("DataShare");
    }
    */

    void UpdateNavigate() {
	Boolean[] values = new Boolean[3];
	Arrays.fill(values, false);
	values[mPager.getCurrentItem()] = true;
	mButtonIndex.setPressed(values[ViewAdapter.ITEM_INDEX]);
	mButtonFeeds.setPressed(values[ViewAdapter.ITEM_FEEDS]);
	mButtonMyself.setPressed(values[ViewAdapter.ITEM_MYSELF]);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	Log.d("MainActivity", "onActivityResult()");
	super.onActivityResult(requestCode, resultCode, data);
	share.tencent.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	share = DataShare.Ins(this.getApplicationContext());
	mButtonIndex = (Button) this.findViewById(R.id.main_btn_index);
	mButtonFeeds = (Button) this.findViewById(R.id.main_btn_feeds);
	mButtonMyself = (Button) this.findViewById(R.id.main_btn_myself);

	mViewAdapter = new ViewAdapter(getSupportFragmentManager());
	mPager = (ViewPager) findViewById(R.id.main_layout_pager);
	mPager.setAdapter(mViewAdapter);
	mPager.setCurrentItem(ViewAdapter.ITEM_INDEX);
	mPager.setOnPageChangeListener(new OnPageChangeListener() {
	    @Override
	    public void onPageScrollStateChanged(int arg0) {
	    }

	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2) {
	    }

	    @Override
	    public void onPageSelected(int type) {
		UpdateNavigate();
	    }
	});
	mButtonIndex.setPressed(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }

    protected void onResume() {
	super.onResume();
	UpdateNavigate();
    }

}
