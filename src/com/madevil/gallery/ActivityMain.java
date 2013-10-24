package com.madevil.gallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

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

public class ActivityMain extends ActionBarActivity {
    private ViewAdapter mViewAdapter = null;
    private ViewPager mPager = null;
    private DataShare share = null;
    private ActionBar mBar = null;

    /*
     * protected void onSaveInstanceState (Bundle outState) {
     * outState.putSerializable("DataShare",
     * DataShare.Ins(getApplicationContext())); } protected void
     * onRestoreInstanceState (Bundle savedInstanceState) { DataShare share =
     * DataShare.Ins(getApplicationContext()); share = (DataShare)
     * savedInstanceState.getSerializable("DataShare"); }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }

    protected void onResume() {
	super.onResume();
	UpdateTabs();
    }

    void UpdateTabs() {
	int index = mPager.getCurrentItem();
	mBar.selectTab(mBar.getTabAt(index));
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

	mBar = this.getSupportActionBar();
	share = DataShare.Ins(this.getApplicationContext());

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
	    public void onPageSelected(int index) {
		UpdateTabs();
	    }
	});

	mBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	mBar.setDisplayShowTitleEnabled(false);
	mBar.addTab(mBar.newTab().setText("动态")
		.setTabListener(new PageListener(ViewAdapter.ITEM_FEEDS)));
	mBar.addTab(mBar.newTab().setText("浏览")
		.setTabListener(new PageListener(ViewAdapter.ITEM_INDEX)));
	mBar.addTab(mBar.newTab().setText("我")
		.setTabListener(new PageListener(ViewAdapter.ITEM_MYSELF)));
	mBar.selectTab(mBar.getTabAt(ViewAdapter.ITEM_INDEX));
    }

    class PageListener implements ActionBar.TabListener {
	private int mIndex;

	public PageListener(int index) {
	    mIndex = index;
	}

	@Override
	public void onTabReselected(Tab arg0,
		android.support.v4.app.FragmentTransaction arg1) {
	    mPager.setCurrentItem(mIndex);
	}

	@Override
	public void onTabSelected(Tab arg0,
		android.support.v4.app.FragmentTransaction arg1) {
	    mPager.setCurrentItem(mIndex);
	}

	@Override
	public void onTabUnselected(Tab arg0,
		android.support.v4.app.FragmentTransaction arg1) {
	    // TODO Auto-generated method stub
	}
    }

}
