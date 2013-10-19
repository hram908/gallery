package com.madevil.gallery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.madevil.gallery.R;
import com.madevil.gallery.PictureAdapter.ViewHolder;
import com.madevil.gallery.model.DataPicture;
import com.madevil.gallery.model.G;
import com.origamilabs.library.views.StaggeredGridView;
import com.squareup.picasso.Picasso;

class PictureAdapter extends BaseAdapter {
    private LinkedList<DataPicture> mPictures;
    private Context mContext;

    public PictureAdapter(Context context) {
	mPictures = new LinkedList<DataPicture>();
	mContext = context;
    }

    class ViewHolder {
	ImageView imageView;
	TextView contentView;
	TextView timeView;
	int index;
	int upload = 0;
    }

    @Override
    public int getCount() {
	return mPictures.size();
    }

    @Override
    public Object getItem(int position) {
	return mPictures.get(position);
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
	    holder.contentView = (TextView) view.findViewById(R.id.news_title);
	    view.setTag(holder);
	    view.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    ViewHolder holder = (ViewHolder) v.getTag();
		    DataPicture picture = mPictures.get(holder.index);
		    Intent intent = new Intent(mContext, DetailActivity.class);
		    intent.putExtra(DataPicture.intentTag, picture);
		    mContext.startActivity(intent);
		}
	    });
	}

	DataPicture picture = mPictures.get(index);
	int height = picture.getHeight();
	int width = LinearLayout.LayoutParams.MATCH_PARENT;
	ViewHolder holder = (ViewHolder) view.getTag();
	holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(width,
		height));
	holder.contentView.setText(picture.getTitle());
	Picasso.with(mContext).load(picture.getUrl()).into(holder.imageView);
	return view;
    }

    public void addItems(List<DataPicture> datas) {
	mPictures.addAll(datas);
    }
}

/**
 *
 * This will not work so great since the heights of the imageViews are
 * calculated on the iamgeLoader callback ruining the offsets. To fix this try
 * to get the (intrinsic) image width and height and set the views height
 * manually. I will look into a fix once I find extra time.
 *
 * @author Maurycy Wojtowicz
 *
 */
public class MainActivity extends Activity {
    private PictureAdapter mAdapter;
    private StaggeredGridView mGridView;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	mGridView = (StaggeredGridView) this
		.findViewById(R.id.main_view_staggered_grid);
	mGridView.setItemMargin(1, 1, 1, 1); // set the GridView margin

	mAdapter = new PictureAdapter(MainActivity.this);
	mGridView.setAdapter(mAdapter);
	mAdapter.notifyDataSetChanged();
	AddItemToContainer(1);
	// AddItemToContainer(2);
	// AddItemToContainer(3);
    }

    private void AddItemToContainer(int pageindex) {
	String url = G.Url.getIndexByNew(pageindex);
	Log.d("MainActivity.http", "current url:" + url);

	// do request
	G.http.get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
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
			picture.setHeight(obj.optInt("height", 0));
			picture.setWidth(obj.optInt("width"));
			items.add(picture);
		    }
		} catch (Exception e) {
		    Log.e("MainActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
			    .show();
		}
		mAdapter.addItems(items);
		mAdapter.notifyDataSetChanged();
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		Log.e("MianActivity.http", "Exception: "+ e.toString());
		e.printStackTrace();
		String msg = "服务器出错";
		Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
			.show();
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_main, menu);
	return true;
    }

}
