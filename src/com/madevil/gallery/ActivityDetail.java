package com.madevil.gallery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.madevil.gallery.R;
import com.squareup.picasso.Picasso;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ActivityDetail extends Activity {
    private DataUser mUser = new DataUser();
    private Boolean mUserLiked = false;
    private Boolean mUserCommented = false;
    private Boolean mUserDownloaded = false;
    private DataPicture mPicture = null;
    private Button mButtonLike, mButtonComment, mButtonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_detail);

	Intent intent = getIntent();
	mPicture = (DataPicture) intent
		.getSerializableExtra(DataPicture.intentTag);
	Picasso.with(getApplication()).load(mPicture.getUrl())
		.into((ImageView) this.findViewById(R.id.detail_image));

	mButtonLike = (Button) this.findViewById(R.id.detail_btn_like);
	mButtonComment = (Button) this.findViewById(R.id.detail_btn_comment);
	mButtonDownload = (Button) this.findViewById(R.id.detail_btn_download);
	mButtonLike.setText(String.format("%s", mPicture.getLikeNumber()));
	mButtonComment
		.setText(String.format("%s", mPicture.getCommentNumber()));
	mButtonDownload.setText(String.format("%s",
		mPicture.getDownloadNumber()));

	String url = G.Url.getPictureDetail(mPicture.getId());
	Log.i("DetailActivity.http", "url: " + url);
	G.http.get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		Log.d("DetailActivity.http", "json:" + json_root);
		try {
		    int ecode = json_root.getInt("ecode");
		    if (ecode != 0) {
			String msg = "" + ecode + "."
				+ json_root.optString("msg", "系统繁忙，请休息一下再来～");
			Toast.makeText(getApplication(), msg,
				Toast.LENGTH_SHORT).show();
			return;
		    }
		    JSONObject obj = json_root; // FIXME .getJSONObject("data");
		    mUserLiked = obj.optBoolean("liked", false);
		    mUserCommented = obj.optBoolean("commented", false);
		    mUserDownloaded = obj.optBoolean("downloaded", false);
		    mUser.id = json_root.getString("owner");
		    Log.d("DetailActivity", "uesr_id=" + mUser.id);
		} catch (Exception e) {
		    Log.e("DetailActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
			    .show();
		}
		mButtonLike.setPressed(mUserLiked);
		mButtonComment.setPressed(mUserCommented);
		mButtonDownload.setPressed(mUserDownloaded);

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
	getMenuInflater().inflate(R.menu.activity_detail, menu);
	return true;
    }

    public void onClick_detail_btn_back(View v) {
	this.finish();
    }

    public void onClick_detail_btn_like(View v) {
	String url = G.Url.getPictureDetail(mPicture.getId());
	// do request
	RequestParams params = new RequestParams();
	if (mUserLiked) {
	    params.put("like", "0");
	} else {
	    params.put("like", "1");
	}
	G.http.post(url, params, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		Log.d("MainActivity.http", "json:" + json_root);
		try {
		    int ecode = json_root.getInt("ecode");
		    String msg = "" + ecode + "."
			    + json_root.optString("msg", "系统繁忙");
		    if (ecode == 0) {
			mUserLiked = !mUserLiked;
		    } else {
			Toast.makeText(getApplication(), msg,
				Toast.LENGTH_SHORT).show();
		    }
		} catch (Exception e) {
		    Log.e("MainActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
			    .show();
		}
		mButtonLike.setPressed(mUserLiked);
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
	return;
    }

    public void onClick_detail_btn_download(View v) {
	if (!mUserDownloaded) {
	    // alert the coin
	}
    }

    public void onClick_detail_btn_comment(View v) {
	Intent intent = new Intent(getApplication(), ActivityComment.class);
	this.startActivity(intent);
    }

    public void onClick_detail_btn_avatar(View v) {
	Intent intent = new Intent(getApplication(), ActivityUser.class);
	intent.putExtra(DataUser.intentTag, mUser.id);
	this.startActivity(intent);
    }
}
