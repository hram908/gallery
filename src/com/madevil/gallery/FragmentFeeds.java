package com.madevil.gallery;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

public class FragmentFeeds extends Fragment {
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_feeds, container, false);
	mContext = this.getActivity().getApplication();
	return view;
    }

    private void AddItemToContainer(int pageindex) {
	String url = G.Url.index(pageindex);
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
		    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
		//mAdapter.addItems(items);
		//mAdapter.notifyDataSetChanged();
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		Log.e("MianActivity.http", "Exception: " + e.toString());
		e.printStackTrace();
		String msg = "服务器出错";
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	    }
	});
    }

}
