package com.madevil.gallery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.madevil.gallery.R;
import com.madevil.gallery.UserPictureAdapter.ViewHolder;
import com.madevil.gallery.model.DataPicture;
import com.madevil.gallery.model.DataUser;
import com.madevil.gallery.model.G;
import com.origamilabs.library.views.StaggeredGridView;
import com.squareup.picasso.Picasso;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

class UserPictureAdapter extends BaseAdapter {
    private LinkedList<DataPicture> mPictures;
    private Context mContext;
    private int mUpload = 0;
    public static final int TYPE_UPLOAD = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_COUNT = 2;

    public UserPictureAdapter(Context context) {
	mPictures = new LinkedList<DataPicture>();
	mContext = context;
	mUpload = 1;
    }

    @Override
    public int getItemViewType(int position) {
	return (position < mUpload) ? TYPE_UPLOAD : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
	return TYPE_COUNT;
    }

    @Override
    public int getCount() {
	return mPictures.size() + mUpload;
    }

    @Override
    public Object getItem(int position) {
	int index = position - mUpload;
	if (index < 0) {
	    index = 0;
	}
	return mPictures.get(index);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    class ViewHolder {
	ImageView imageView;
	int index;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
	Log.d("PictureAdapter", "pos=" + position + "view=" + view);
	int type = getItemViewType(position);
	if (type == TYPE_UPLOAD) {
	    ViewHolder holder = null;
	    if (view == null) {
		LayoutInflater inflater = LayoutInflater.from(parent
			.getContext());
		view = inflater.inflate(R.layout.component_upload, null);
		view.setTag(holder);
	    }
	    return view;
	}

	// / normal pictures
	int index = position - mUpload;
	if (view == null) {
	    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	    view = inflater.inflate(R.layout.component_picture_fixed, null);
	    ViewHolder holder = new ViewHolder();
	    holder.index = index;
	    holder.imageView = (ImageView) view
		    .findViewById(R.id.component_picture_fixed_image);
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
	ViewHolder holder = (ViewHolder) view.getTag();
	Picasso.with(mContext).load(picture.getUrl()).into(holder.imageView);
	return view;
    }

    public void addItems(List<DataPicture> datas) {
	mPictures.addAll(datas);
    }
}

public class UserActivity extends Activity {
    private DataUser mUser = new DataUser();
    private UserPictureAdapter mUserPictureAdapter = null;
    private GridView mGridView = null;
    private TextView mTextNick = null;
    private TextView mTextIntro = null;
    private Button mButtonMoney = null;
    private Button mButtonPicture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_user);

	// 读取用户信息
	Intent intent = getIntent();
	mUser.setId(intent.getStringExtra(DataUser.intentTag));
	Log.d("UserActivity", "user: " + mUser.getId());

	// 构造图片转换器
	mUserPictureAdapter = new UserPictureAdapter(this);
	Log.d("UserActivity", "mAdapter=" + mUserPictureAdapter);

	mGridView = (GridView) this.findViewById(R.id.user_view_grid);
	mGridView.setAdapter(mUserPictureAdapter);

	// 添加一个默认的按钮
	mUserPictureAdapter.notifyDataSetChanged();

	mTextNick = (TextView) this.findViewById(R.id.user_text_nick);
	mTextIntro = (TextView) this.findViewById(R.id.user_text_intro);
	mButtonMoney = (Button) this.findViewById(R.id.user_btn_money);
	mButtonPicture = (Button) this.findViewById(R.id.user_btn_picture);

	// 异步发起读取用户信息的操作
	// do request
	String url = G.Url.getUser(mUser.getId(), 0);
	Log.d("UserActivity.http", "url=" + url);
	G.http.get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		List<DataPicture> mPictures = new ArrayList<DataPicture>();

		Log.d("MainActivity.http", "json:" + json_root);
		try {
		    int ecode = json_root.getInt("ecode");
		    if (ecode != 0 || json_root.isNull("data")) {
			String msg = "" + ecode + "."
				+ json_root.optString("msg", "系统繁忙，请休息一下再来～");
			Toast.makeText(getApplication(), msg,
				Toast.LENGTH_SHORT).show();
			return;
		    }
		    JSONObject json_data = json_root.getJSONObject("data");
		    mUser.setNick(json_data.optString("nick")
			    .replace("rexliao", "tyler")
			    .replace("rex", "tyler")
			    .replace("talebook", "tyler"));
		    mUser.setIntro(json_data.optString("intro", ""));
		    mUser.setMoneyNumber(json_data.optInt("money", 0));
		    mUser.setPictureNumber(json_data.optInt("pic_num", 0));

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
			mPictures.add(picture);
		    }
		} catch (Exception e) {
		    Log.e("MainActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
			    .show();
		}
		mUserPictureAdapter.addItems(mPictures);
		mUserPictureAdapter.notifyDataSetChanged();
		mTextNick.setText(mUser.getNick());
		mTextIntro.setText(mUser.getIntro());
		mButtonMoney.setText("" + mUser.getMoneyNumber());
		mButtonPicture.setText("" + mUser.getPictureNumber());

		mUserPictureAdapter.addItems(mPictures);
		mUserPictureAdapter.notifyDataSetChanged();
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		Log.e("MianActivity.http", "Exception: " + e.toString());
		e.printStackTrace();
		String msg = "服务器出错";
		Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
			.show();
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.activity_user, menu);
	return true;
    }
}
