package com.madevil.gallery;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;


public class FragmentPicture extends Fragment {
    private DataUser mUser = new DataUser();
    private Boolean mUserLiked = false;
    private Boolean mUserCommented = false;
    private Boolean mUserDownloaded = false;
    private DataPicture mPicture = null;
    private Button mButtonLike, mButtonComment, mButtonDownload;
    private ImageView mButtonAvatar = null;
    private Context mContext = null;
    
    public static FragmentPicture Ins(DataPicture picture) {
	FragmentPicture f = new FragmentPicture();
	f.mPicture = picture;
	return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_picture, container, false);
	mContext = this.getActivity().getApplication();

	Picasso.with(mContext)
		.load(mPicture.getUrl())
		.into((ImageView) view.findViewById(R.id.detail_image));

	Log.d("PictureAdapter", "onCreate() id=" + mPicture.getId());

	mButtonLike = (Button) view.findViewById(R.id.detail_btn_like);
	mButtonComment = (Button) view.findViewById(R.id.detail_btn_comment);
	mButtonDownload = (Button) view.findViewById(R.id.detail_btn_download);
	mButtonLike.setText(String.format("%s", mPicture.getLikeNumber()));
	mButtonComment
		.setText(String.format("%s", mPicture.getCommentNumber()));
	mButtonDownload.setText(String.format("%s",
		mPicture.getDownloadNumber()));
	
	mButtonLike.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		onClick_detail_btn_like(arg0);
	    }	    
	});
	mButtonComment.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		onClick_detail_btn_comment(arg0);
	    }	    
	});
	mButtonDownload.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		onClick_detail_btn_download(arg0);
	    }	    
	});
	
	mButtonAvatar = (ImageView)view.findViewById(R.id.detail_btn_avatar);
	mButtonAvatar.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onClick_detail_btn_avatar(v);
	    }
	});

	String url = G.Url.pictureInfo(mPicture.getId());
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
			Toast.makeText(mContext, msg,
				Toast.LENGTH_SHORT).show();
			return;
		    }
		    JSONObject obj = json_root.getJSONObject("data");
		    mUserLiked = obj.optBoolean("liked", false);
		    mUserCommented = obj.optBoolean("commented", false);
		    mUserDownloaded = obj.optBoolean("downloaded", false);
		    mUser.id = obj.getString("owner");
		    Log.d("DetailActivity", "uesr_id=" + mUser.id);
		} catch (Exception e) {
		    Log.e("DetailActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
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
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
			.show();
	    }

	});
	return view;

    }

    public void onClick_detail_btn_like(View v) {
	mButtonLike.setEnabled(false);
	String url = G.Url.doPictureLike(mPicture.getId());
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
		mButtonLike.setEnabled(true);
		Log.d("MainActivity.http", "json:" + json_root);
		try {
		    int ecode = json_root.getInt("ecode");
		    String msg = "" + ecode + "."
			    + json_root.optString("msg", "系统繁忙");
		    if (ecode == 0) {
			mUserLiked = !mUserLiked;
		    } else {
			Toast.makeText(mContext, msg,
				Toast.LENGTH_SHORT).show();
		    }
		} catch (Exception e) {
		    Log.e("MainActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
			    .show();
		}
		mButtonLike.setPressed(mUserLiked);
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		mButtonLike.setEnabled(true);
		Log.e("MianActivity.http", "Exception: " + e.toString());
		e.printStackTrace();
		String msg = "服务器出错";
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
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
	Intent intent = new Intent(mContext, ActivityComment.class);
	intent.putExtra(DataPicture.intentTag, mPicture);
	this.startActivityForResult(intent, 0);
    }

    public void onClick_detail_btn_avatar(View v) {
	Intent intent = new Intent(mContext, ActivityUser.class);
	intent.putExtra(DataUser.intentTag, mUser.id);
	this.startActivityForResult(intent, 0);
    }
}
