package com.madevil.gallery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.madevil.gallery.model.DataPicture;
import com.madevil.gallery.model.DataUser;
import com.madevil.gallery.model.Globals;
import com.madevil.gallery.R;
import com.madevil.util.Helper;
import com.madevil.util.ImageFetcher;
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

public class DetailActivity extends Activity {
    private DataUser mUser = new DataUser();
    private Boolean mUserLiked = false;
    private Boolean mUserCommented = false;
    private Boolean mUserDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_detail);

	Intent intent = getIntent();
	DataPicture picture = (DataPicture) intent
		.getSerializableExtra(DataPicture.intentTag);
	Picasso.with(getApplication()).load(picture.getUrl())
		.into((ImageView) this.findViewById(R.id.detail_image));

	Button buttonLike = (Button) this.findViewById(R.id.detail_btn_like);
	Button buttonComment = (Button) this
		.findViewById(R.id.detail_btn_comment);
	Button buttonDownload = (Button) this
		.findViewById(R.id.detail_btn_download);
	buttonLike.setText(String.format("%s", picture.getLikeNumber()));
	buttonComment.setText(String.format("%s", picture.getCommentNumber()));
	buttonDownload
		.setText(String.format("%s", picture.getDownloadNumber()));

	String url = Globals.Url.getPictureDetail(picture.getId());
	TaskGetDetail task = new TaskGetDetail();
	task.execute(url);
	Log.i("DetailActivity", "url: " + url);

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
	if (mUserLiked) {
	    // cancel like
	}
	return;
    }

    public void onClick_detail_btn_download(View v) {
	if (!mUserDownloaded) {
	    // alert the coin
	}
    }

    public void onClick_detail_btn_comment(View v) {
	Intent intent = new Intent(getApplication(), CommentActivity.class);
	this.startActivity(intent);
    }

    public void onClick_detail_btn_avatar(View v) {
	Intent intent = new Intent(getApplication(), UserActivity.class);
	intent.putExtra(DataUser.intentTag, mUser.getId());
	this.startActivity(intent);
    }

    private class TaskGetDetail extends AsyncTask<String, Integer, Integer> {
	private int ecode = 0;
	private String msg = "";

	@Override
	protected void onPostExecute(Integer result) {
	    if (ecode != 0) {
		Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
			.show();
	    }
	}

	@Override
	protected Integer doInBackground(String... params) {
	    String url = params[0];
	    String json = "";
	    if (Helper.checkConnection(DetailActivity.this)) {
		try {
		    json = Helper.getStringFromUrl(url);

		} catch (IOException e) {
		    Log.e("IOException is : ", e.toString());
		    e.printStackTrace();
		    ecode = 1;
		    msg = "连接图片服务器失败！请休息一下再来～";
		    return ecode;
		}
	    }
	    Log.d("DetailActiivty", "picture info json:" + json);

	    if (json == null) {
		ecode = 2;
		msg = "图片服务器返回的数据为空！请休息一下再来～";
		return ecode;
	    }

	    try {
		JSONObject json_root = new JSONObject(json);
		ecode = json_root.getInt("ecode");
		if (ecode != 0 || json_root.isNull("data")) {
		    msg = "" + ecode + "."
			    + json_root.optString("msg", "系统繁忙，请休息一下再来～");
		    return ecode;
		}
		JSONObject obj = json_root.getJSONObject("data");
		mUserLiked = obj.optBoolean("liked", false);
		mUserCommented = obj.optBoolean("commented", false);
		mUserDownloaded = obj.optBoolean("downloaded", false);
		mUser.setId(json_root.getString("owner"));
		Log.d("DetailActivity", "uesr_id=" + mUser.getId());
	    } catch (Exception e) {
		e.printStackTrace();
		ecode = 3;
		msg = "图片服务器返回的数据出错！请休息一下再来～";
		return ecode;
	    }
	    return 0;
	}
    }

    private void FailMsg(String msg) {
    }

}
