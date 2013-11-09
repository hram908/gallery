package com.madevil.gallery;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class ActivityMain extends BasicActivity {
    public final String PREFS_NAME = "cache";
    private ViewAdapter mViewAdapter = null;
    private ViewPager mPager = null;
    private DataShare share = null;
    private ActionBar mBar = null;
    private Context mContext = null;
    private PullToRefreshAttacher mPullToRefreshAttacher;


    private boolean onPageritemSelected(MenuItem item) {
	    Fragment f = mViewAdapter.getItem(mPager.getCurrentItem());
	    Log.d("MainActivity", "calling fragment=" + f.getClass().getName());
	    return f.onOptionsItemSelected(item);
	
    }
    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	Log.d("ActivityMain", "onOptionsItemSelected()");
	// Handle item selection
	switch (item.getItemId()) {
	case R.id.menu_login:
	    mPager.setCurrentItem(ViewAdapter.ITEM_LOGIN);
	    invalidateOptionsMenu();
	    return this.onPageritemSelected(item);
	case R.id.menu_logout:
	    share.is_login = false;
	    share.login_info = "";
	    mViewAdapter.notifyDataSetChanged();
	    mPager.setCurrentItem(ViewAdapter.ITEM_LOGIN);
	    invalidateOptionsMenu();
	    return true;
	case R.id.menu_feedback:
	    FeedbackAgent agent = new FeedbackAgent(this);
	    agent.startFeedbackActivity();
	    return true;
	case R.id.menu_refresh:
	    mPager.setCurrentItem(ViewAdapter.ITEM_INDEX);
	    return this.onPageritemSelected(item);
	default:
	    return this.onPageritemSelected(item);
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	if (share.is_login) {
	    menu.setGroupVisible(R.id.menu_group_is_not_login, false);
	} else {
	    menu.setGroupVisible(R.id.menu_group_is_login, false);
	}
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

	/*
	 * // flush http cache HttpResponseCache cache =
	 * HttpResponseCache.getInstalled(); if (cache != null) { cache.flush();
	 * }
	 */
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	Log.d("MainActivity", "onActivityResult()");
	super.onActivityResult(requestCode, resultCode, data);
	Fragment f = mViewAdapter.getItem(mPager.getCurrentItem());
	Log.d("MainActivity", "calling fragment=" + f.getClass().getName());
	f.onActivityResult(requestCode, resultCode, data);
	invalidateOptionsMenu();
	// share.tencent.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

	mBar = getSupportActionBar();
	mContext = getApplicationContext();
	share = DataShare.Ins(mContext);
    // Create a PullToRefreshAttacher instance
    mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

	// Restore preferences
	Log.d("ActivityMain", "onCreate(), load DataShare");
	share.load(getSharedPreferences(PREFS_NAME, 0));

	/*
	 * // settings http cache try { File httpCacheDir = new
	 * File(mContext.getCacheDir(), "http-cache"); long httpCacheSize = 50 *
	 * 1024 * 1024; // 50 MiB HttpResponseCache.install(httpCacheDir,
	 * httpCacheSize); } catch (Exception e) { e.printStackTrace();
	 * Log.i("ActivityMain", "HTTP cache installation failed:" + e); }
	 */
	Picasso.with(mContext).setDebugging(true);

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

	mBar.setTitle(R.string.msg_welcome);
	// Bind the title indicator to the adapter
	LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View v = inflator.inflate(R.layout.actionbar_tabs, null);
	TabPageIndicator indicator = (TabPageIndicator) v
		.findViewById(R.id.indicator);
	indicator.setViewPager(mPager);
	mBar.setDisplayShowCustomEnabled(true);
	mBar.setDisplayShowTitleEnabled(false);
	mBar.setCustomView(v);
	mPager.setCurrentItem(ViewAdapter.ITEM_INDEX);
    }

    void UpdateTabs() {
	int index = mPager.getCurrentItem();
	// mBar.selectTab(mBar.getTabAt(index));
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
	private FragmentIndex mFragmentIndex = null;
	private FragmentFeeds mFragmentFeeds = null;
	private FragmentLogin mFragmentLogin = null;

	public ViewAdapter(FragmentManager fm) {
	    super(fm);
	    mFragmentIndex = FragmentIndex.Instance(mPullToRefreshAttacher);
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
		    return FragmentUser.Ins(share.user);
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

	private final String[] CONTENT = new String[] { "动态", "浏览", "我" };

	@Override
	public CharSequence getPageTitle(int position) {
	    return CONTENT[position % CONTENT.length].toUpperCase();
	}

	@Override
	public int getCount() {
	    return ITEM_COUNT;
	}
    }

}
