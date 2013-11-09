package com.madevil.gallery;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.StaggeredGridView.OnLoadmoreListener;
import com.squareup.picasso.Picasso;

class PictureAdapter extends BaseAdapter {
    private DataShare share = null;

    public PictureAdapter(Context c) {
	share = DataShare.Ins(c);
    }

    class ViewHolder {
	ImageView imageView;
	int index;
    }

    @Override
    public int getCount() {
	return share.pictures.size();
    }

    @Override
    public Object getItem(int position) {
	return share.pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
	if (view == null) {
	    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	    view = inflater.inflate(R.layout.component_picture, null);
	    ViewHolder holder = new ViewHolder();
	    holder.index = index;
	    holder.imageView = (ImageView) view.findViewById(R.id.news_pic);
	    // holder.contentView = (TextView)
	    // view.findViewById(R.id.news_title);
	    view.setTag(holder);
	    view.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    v.getContext();
		    ViewHolder holder = (ViewHolder) v.getTag();
		    DataPicture picture = share.pictures.get(holder.index);
		    Intent intent = new Intent(v.getContext(),
			    ActivityDetail.class);
		    intent.putExtra(DataPicture.intentTag, picture);
		    intent.putExtra("index", holder.index);
		    share.picture_index = holder.index;
		    v.getContext().startActivity(intent);
		    Log.d("PictureAdapter", "onClick() index=" + holder.index
			    + ", id=" + picture.getId());
		}
	    });
	}

	DataPicture picture = share.pictures.get(index);
	int height = picture.getHeight();
	int width = LinearLayout.LayoutParams.MATCH_PARENT;
	ViewHolder holder = (ViewHolder) view.getTag();
	holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(width,
		height));
	// holder.contentView.setText(picture.getTitle());
	Context c = parent.getContext();
	Picasso.with(c).load(picture.getSmallUrl()).into(holder.imageView);
	return view;
    }

    public void addItems(List<DataPicture> datas) {
	share.pictures.addAll(datas);
    }

    public void clear() {
	share.pictures.clear();
    }
}

public class FragmentIndex extends Fragment {
    private PictureAdapter mAdapter;
    private StaggeredGridView mGridView;
    private Context mContext;
    private DataShare share = null;
    private int mPage = 0;
    private boolean mReload = false;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    public static FragmentIndex Instance(PullToRefreshAttacher a) {
	FragmentIndex f = new FragmentIndex();
	f.mPullToRefreshAttacher = a;
	return f;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// TODO Auto-generated method stub
	super.onCreateOptionsMenu(menu, inflater);
	menu.add(getString(R.string.menu_refresh)).setShowAsAction(
		MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle item selection
	switch (item.getItemId()) {
	case R.id.menu_refresh:
	    refresh();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_index, container, false);

	// Retrieve the PullToRefreshLayout from the content view
	PullToRefreshLayout ptrLayout = (PullToRefreshLayout) view
		.findViewById(R.id.ptr_layout);

	// Give the PullToRefreshAttacher to the PullToRefreshLayout, along with
	// a refresh listener.
	ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher,
		new OnRefreshListener() {
		    @Override
		    public void onRefreshStarted(View view) {
			// TODO Auto-generated method stub
			Log.e("Refersh", "loading");
			refresh();
		    }
		});

	mContext = this.getActivity().getApplication();
	share = DataShare.Ins(mContext);
	mGridView = (StaggeredGridView) view
		.findViewById(R.id.fragment_index_grid);
	mGridView.setItemMargin(1); // set the GridView margin

	View footerView = inflater.inflate(R.layout.component_loading, null);
	mGridView.setFooterView(footerView);
	mGridView.setOnLoadmoreListener(new OnLoadmoreListener(){
	    @Override
	    public void onLoadmore() {
		loadmore();
	    }
	});

	mAdapter = new PictureAdapter(mContext);
	mGridView.setAdapter(mAdapter);

	if (share.pictures_json.length() == 0) {
	    loadmore();
	} else {
	    try {
		JSONObject json_root = new JSONObject(share.pictures_json);
		LoadJson(json_root);
	    } catch (Exception e) {
		e.printStackTrace();
		Log.e("FragmentIndex", "Load index picture from cache fail");
	    }
	}

	return view;
    }

    private void LoadJson(JSONObject json_root) {
	List<DataPicture> items = new ArrayList<DataPicture>();

	Log.d("MainActivity.http", "json:" + json_root);
	try {
	    JSONObject json_data = json_root.getJSONObject("data");
	    JSONArray json_pics = json_data.getJSONArray("pics");
	    for (int i = 0; i < json_pics.length(); i++) {
		JSONObject obj = json_pics.getJSONObject(i);
		DataPicture picture = new DataPicture();
		picture.setId(obj.optString("pid", ""));
		picture.setUrl(obj.optString("url", ""));
		picture.setTitle(obj.optString("title", ""));
		picture.setContent(obj.optString("content", ""));
		picture.setCommentNumber(obj.optInt("comment_num", 0));
		picture.setLikeNumber(obj.optInt("like", 0));
		picture.setDownloadNumber(obj.optInt("download", 0));
		picture.setHeight(obj.optInt("height"));
		picture.setWidth(obj.optInt("width"));
		items.add(picture);
	    }
	} catch (Exception e) {
	    Log.e("MainActivity.http", "exception:" + e.toString());
	    String msg = "服务器返回的数据无效";
	    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	if (mReload) {
	    mAdapter.clear();
	    mReload = false;
	}
	mPage += 1;
	mAdapter.addItems(items);
	mAdapter.notifyDataSetChanged();
    }
    
    private void refresh() {
	mPage = 0;
	mReload = true;
	loadmore();	
    }

    private void loadmore() {
	String url = G.Url.index(mPage);
	Log.d("MainActivity.http", "current url:" + url);

	// do request
	G.http.setCookieStore(share.http_cookies);
	G.http.get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		mPullToRefreshAttacher.setRefreshComplete();
		if (mPage == 0) {
		    share.pictures_json = json_root.toString();
		}
		LoadJson(json_root);
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		Log.e("MianActivity.http", "Exception: " + e.toString());
		e.printStackTrace();
		String msg = "服务器出错";
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		mPullToRefreshAttacher.setRefreshComplete();
	    }
	});
    }

}
