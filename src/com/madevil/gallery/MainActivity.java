package com.madevil.gallery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;
import com.madevil.gallery.R;

class ViewAdapter extends FragmentPagerAdapter {
    public static final int TAB_INDEX = 0;
    public static final int TAB_FEEDS = 1;
    public static final int TAB_MYSELF = 2;
    public static final int TAB_COUNT = 3;

    public ViewAdapter(FragmentManager fm) {
	super(fm);
	// TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int index) {
	switch (index) {
	case TAB_INDEX:
	    return new FragmentIndex();
	case TAB_FEEDS:
	    return new FragmentFeeds();
	case TAB_MYSELF:
	    return new FragmentMyself();
	}
	return null;
    }

    @Override
    public int getCount() {
	return TAB_COUNT;
    }

}

public class MainActivity extends FragmentActivity {
    private ViewAdapter mViewAdapter;
    private ViewPager mPager;
    private Button mButtonIndex, mButtonFeeds, mButtonMyself;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	mButtonIndex = (Button) this.findViewById(R.id.main_btn_index);
	mButtonFeeds = (Button) this.findViewById(R.id.main_btn_feeds);
	mButtonMyself = (Button) this.findViewById(R.id.main_btn_myself);
	mButtonIndex.setPressed(true);

	mViewAdapter = new ViewAdapter(getSupportFragmentManager());
	mPager = (ViewPager) findViewById(R.id.main_layout_pager);
	mPager.setAdapter(mViewAdapter);
	mPager.setOnPageChangeListener(new OnPageChangeListener() {
	    @Override
	    public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplication(), "onPageScrollStateChanged",
			Toast.LENGTH_SHORT).show();
	    }

	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplication(), "onPageScrolled",
			Toast.LENGTH_SHORT).show();
	    }

	    @Override
	    public void onPageSelected(int type) {
		Boolean index = false, feeds = false, myself = false;
		switch (type) {
		case ViewAdapter.TAB_INDEX:
		    index = true;
		    break;
		case ViewAdapter.TAB_FEEDS:
		    feeds = true;
		    break;
		case ViewAdapter.TAB_MYSELF:
		    myself = true;
		    break;
		}
		mButtonIndex.setPressed(index);
		mButtonFeeds.setPressed(feeds);
		mButtonMyself.setPressed(myself);
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }

}
