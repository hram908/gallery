package com.madevil.gallery;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMain extends ActionBarActivity {
    public final String PREFS_NAME = "cache";
    private ViewAdapter mViewAdapter = null;
    private ViewPager mPager = null;
    private DataShare share = null;
    private ActionBar mBar = null;
    private Context mContext = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	Log.d("ActivityMain", "onOptionsItemSelected()");
	// Handle item selection
	switch (item.getItemId()) {
	case R.id.menu_logout:
	    share.is_login = false;
	    share.login_info = "";
	    mViewAdapter.notifyDataSetChanged();
	    return true;
	default:
	    Fragment f = mViewAdapter.getItem(mPager.getCurrentItem());
	    Log.d("MainActivity", "calling fragment=" + f.getClass().getName());
	    return f.onOptionsItemSelected(item);
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }

    protected void onResume() {
	super.onResume();
	UpdateTabs();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onStop() {
	super.onStop();
	Log.d("ActivityMain", "onStop(), dump DataShare.");
	share.dump(getSharedPreferences(PREFS_NAME, 0));

	// flush http cache
	HttpResponseCache cache = HttpResponseCache.getInstalled();
	if (cache != null) {
	    cache.flush();
	}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	Log.d("MainActivity", "onActivityResult()");
	super.onActivityResult(requestCode, resultCode, data);
	Fragment f = mViewAdapter.getItem(mPager.getCurrentItem());
	Log.d("MainActivity", "calling fragment=" + f.getClass().getName());
	f.onActivityResult(requestCode, resultCode, data);
	// share.tencent.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	mBar = getSupportActionBar();
	mContext = getApplicationContext();
	share = DataShare.Ins(mContext);

	// Restore preferences
	Log.d("ActivityMain", "onCreate(), load DataShare");
	share.load(getSharedPreferences(PREFS_NAME, 0));

	// settings http cache
	try {
	    File httpCacheDir = new File(mContext.getCacheDir(), "http");
	    long httpCacheSize = 100 * 1024 * 1024; // 10 MiB
	    HttpResponseCache.install(httpCacheDir, httpCacheSize);
	} catch (Exception e) {
	    e.printStackTrace();
	    Log.i("ActivityMain", "HTTP cache installation failed:" + e);
	}

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
		.setTabListener(new PageListener(ViewAdapter.ITEM_LOGIN)));
	mBar.selectTab(mBar.getTabAt(ViewAdapter.ITEM_INDEX));
    }

    void UpdateTabs() {
	int index = mPager.getCurrentItem();
	mBar.selectTab(mBar.getTabAt(index));
	switch (index) {
	case ViewAdapter.ITEM_FEEDS:
	    mBar.setTitle(R.string.title_fragment_feeds);
	    break;
	case ViewAdapter.ITEM_INDEX:
	    mBar.setTitle(R.string.title_fragment_index);
	    break;
	case ViewAdapter.ITEM_LOGIN:
	    if (share.is_login) {
		mBar.setTitle(R.string.title_fragment_user);
	    } else {
		mBar.setTitle(R.string.title_fragment_login);
	    }
	    break;
	}
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

    public interface Callback {
	public void call();
    }

    class ViewAdapter extends FragmentStatePagerAdapter {
	public static final int ITEM_FEEDS = 0;
	public static final int ITEM_INDEX = 1;
	public static final int ITEM_LOGIN = 2;
	public static final int ITEM_COUNT = 3;
	private FragmentUser mFragmentUser = null;
	private FragmentIndex mFragmentIndex = null;
	private FragmentFeeds mFragmentFeeds = null;
	private FragmentLogin mFragmentLogin = null;

	public ViewAdapter(FragmentManager fm) {
	    super(fm);
	    mFragmentUser = new FragmentUser();
	    mFragmentIndex = new FragmentIndex();
	    mFragmentFeeds = new FragmentFeeds();
	    mFragmentLogin = FragmentLogin.Instance(new Callback() {
		public void call() {
		    notifyDataSetChanged();
		}
	    });
	}

	@Override
	public Fragment getItem(int index) {
	    switch (index) {
	    default:
	    case ITEM_INDEX:
		return mFragmentIndex;
	    case ITEM_FEEDS:
		return mFragmentFeeds;
	    case ITEM_LOGIN:
		if (share.is_login) {
		    return mFragmentUser;
		} else {
		    return mFragmentLogin;
		}
	    }
	}

	@Override
	public int getItemPosition(Object object) {
	    if (share.is_login && object instanceof FragmentLogin) {
		return POSITION_NONE;
	    }
	    if (!share.is_login && object instanceof FragmentUser) {
		return POSITION_NONE;
	    }
	    return POSITION_UNCHANGED;
	}

	@Override
	public int getCount() {
	    return ITEM_COUNT;
	}
    }

}
