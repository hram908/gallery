package com.madevil.gallery;

import java.net.UnknownHostException;
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
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.StaggeredGridView.OnLoadmoreListener;
import com.squareup.picasso.Picasso;

class PictureAdapter extends BaseAdapter {
    private DataShare share = null;
    private int mItemWidth = 0;

    public PictureAdapter(Context c, int itemWidth) {
	share = DataShare.Ins(c);
	mItemWidth = itemWidth;
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
		    intent.putExtra(DataPicture.intentPictures, share.pictures);
		    intent.putExtra(DataPicture.intentIndex, holder.index);
		    v.getContext().startActivity(intent);
		    Log.d("PictureAdapter", "onClick() index=" + holder.index
			    + ", id=" + picture.id);
		}
	    });
	}

	DataPicture picture = share.pictures.get(index);
	int height = picture.height * mItemWidth / picture.width;
	int width = LayoutParams.MATCH_PARENT;
	Log.d("FragmentIndex", "height=" + height + ", orig height="
		+ picture.height + ", item width=" + mItemWidth);
	ViewHolder holder = (ViewHolder) view.getTag();
	holder.imageView.setLayoutParams(new LayoutParams(width, height));
	Context c = parent.getContext();
	Picasso.with(c).load(picture.url_s).into(holder.imageView);
	return view;
    }

    public void addItems(List<DataPicture> datas) {
	share.pictures.addAll(datas);
    }

    public void clear() {
	share.pictures.clear();
    }
}

public class FragmentIndex extends TrackedFragment {
    private PictureAdapter mAdapter;
    private StaggeredGridView mGridView;
    private Context mContext;
    private DataShare share = null;
    private int mPage = 0;
    private boolean mReload = false;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private View mFooter = null;
    private int mItemWidth = 0;

    public static FragmentIndex Instance(PullToRefreshAttacher a) {
	FragmentIndex f = new FragmentIndex();
	f.mPullToRefreshAttacher = a;
	return f;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	menu.add(getString(R.string.menu_refresh)).setShowAsAction(
		MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

	// 下拉刷新
	PullToRefreshLayout ptrLayout = (PullToRefreshLayout) view
		.findViewById(R.id.ptr_layout);
	ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher,
		new OnRefreshListener() {
		    @Override
		    public void onRefreshStarted(View view) {
			// TODO Auto-generated method stub
			Log.e("Refersh", "loading");
			refresh();
		    }
		});

	// 全局参数
	mContext = this.getActivity().getApplication();
	share = DataShare.Ins(mContext);

	// 瀑布流
	mGridView = (StaggeredGridView) view
		.findViewById(R.id.fragment_index_grid);
	mGridView.setColumnCount(3);
	mGridView.setOnLoadmoreListener(new OnLoadmoreListener() {
	    @Override
	    public void onLoadmore() {
		loadmore();
	    }
	});

	// 加载更多
	mFooter = inflater.inflate(R.layout.component_loading, null);
	mGridView.setFooterView(mFooter);

	// 数据类
	mItemWidth = getActivity().getWindowManager().getDefaultDisplay()
		.getWidth();
	mItemWidth = mItemWidth / mGridView.getColumnCount() + 2;
	mAdapter = new PictureAdapter(mContext, mItemWidth);
	mGridView.setAdapter(mAdapter);
	mAdapter.clear();
	mAdapter.notifyDataSetChanged();

	try {
	    JSONObject json_data = new JSONObject(share.pictures_json);
	    LoadJson(json_data);
	} catch (Exception e) {
	    e.printStackTrace();
	    Log.e("FragmentIndex", "Load index picture from cache fail");
	}
	loadmore();

	return view;
    }

    private void LoadJson(JSONObject json_data) {
	Log.d("data", "loadJson()");
	List<DataPicture> items = new ArrayList<DataPicture>();
	items.clear();

	try {
	    JSONArray json_pics = json_data.getJSONArray("pics");
	    for (int i = 0; i < json_pics.length(); i++) {
		JSONObject obj = json_pics.getJSONObject(i);
		DataPicture picture = new DataPicture();
		picture.id = obj.optString("pid", "");
		picture.title = obj.optString("title", "");
		picture.content = obj.optString("content", "");
		picture.commentNumber = obj.optInt("comment_num", 0);
		picture.likeNumber = obj.optInt("like", 0);
		picture.downloadNumber = obj.optInt("download", 0);
		picture.height = obj.optInt("height", 0);
		picture.width = obj.optInt("width");
		picture.user.id = obj.optString("user");
		picture.setUrl(obj.optString("url", ""));
		items.add(picture);
	    }
	} catch (Exception e) {
	    Log.e("MainActivity.http", "exception:" + e.toString());
	    String msg = "服务器返回的数据无效";
	    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	int last_page = json_data.optInt("last_page", 0);
	Log.i("data", "loadJson items="+items.size());
	Log.i("data", "last_page=" + last_page);
	if (items.size() == 0 || last_page > 0) {
	    // all finish
	    mFooter.findViewById(R.id.component_loading_progress)
		    .setVisibility(View.INVISIBLE);
	    TextView t = (TextView) mFooter
		    .findViewById(R.id.component_loading_text);
	    t.setText(R.string.t_load_done);
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
	Log.d("data", "loadmore()");
	String url = G.Url.index(mPage);
	Http.With(mContext).get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_data) {
		if (mPullToRefreshAttacher != null)
		    mPullToRefreshAttacher.setRefreshComplete();
		if (mPage == 0) {
		    share.pictures_json = json_data.toString();
		}
		LoadJson(json_data);
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		if (mPullToRefreshAttacher != null)
		    mPullToRefreshAttacher.setRefreshComplete();
	    }
	});
    }

}
