package com.madevil.gallery;

import java.util.List;

import org.json.JSONObject;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class FragmentPicture extends Fragment {
    private DataUser mUser = new DataUser();
    private Boolean mUserLiked = false;
    private Boolean mUserCommented = false;
    private Boolean mUserDownloaded = false;

    private List<DataPicture> mPictures;
    private DataPicture mPicture;

    private Button mButtonLike, mButtonComment, mButtonDownload;
    private ImageView mButtonAvatar;
    private Context mContext;
    private ViewPager mViewPager;
    private RelativeLayout mLayout;
    
    private int mIndex;

    public static FragmentPicture Ins(List<DataPicture> pictures, int index) {
	FragmentPicture f = new FragmentPicture();
	f.mPictures = pictures;
	f.mIndex = index;
	return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_picture, container,
		false);
	mContext = this.getActivity().getApplication();
	mLayout = (RelativeLayout) view.findViewById(R.id.detail_bar_info);

	mButtonLike = (Button) view.findViewById(R.id.detail_btn_like);
	mButtonComment = (Button) view.findViewById(R.id.detail_btn_comment);
	mButtonDownload = (Button) view.findViewById(R.id.detail_btn_download);

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

	mButtonAvatar = (ImageView) view.findViewById(R.id.detail_btn_avatar);
	mButtonAvatar.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		onClick_detail_btn_avatar(v);
	    }
	});

	// bind photo view
	mViewPager = (ViewPager) view.findViewById(R.id.detail_frame);
	mViewPager.setAdapter(new PicturePagerAdapter());
	mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
	    @Override
	    public void onPageScrollStateChanged(int arg0) {
	    }

	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2) {
	    }

	    @Override
	    public void onPageSelected(int position) {
		Log.d("ActivityDetail", "onPageSelected");
		mPicture = mPictures.get(position);
		mButtonLike.setText(String.format("%s",
			mPicture.getLikeNumber()));
		mButtonComment.setText(String.format("%s",
			mPicture.getCommentNumber()));
		mButtonDownload.setText(String.format("%s",
			mPicture.getDownloadNumber()));
		loadPictureInfo(G.Url.pictureInfo(mPicture.getId()));
	    }
	});
	
	mViewPager.setCurrentItem(mIndex);
	hide();
	return view;
    }

    public void loadPictureInfo(String url) {
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
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
				.show();
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
		    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
		mButtonLike.setPressed(mUserLiked);
		mButtonComment.setPressed(mUserCommented);
		mButtonDownload.setPressed(mUserDownloaded);
		String url = G.Url.userAvatar(mUser.id);
		Log.d("ActivityDetail", "avatar url=" + url);
		Picasso.with(mContext).load(url).into(mButtonAvatar);
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
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
				.show();
		    }
		} catch (Exception e) {
		    Log.e("MainActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
		mButtonLike.setPressed(mUserLiked);
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		mButtonLike.setEnabled(true);
		Log.e("MianActivity.http", "Exception: " + e.toString());
		e.printStackTrace();
		String msg = "服务器出错";
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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

    public void show() {
	((ActivityDetail) getActivity()).getSupportActionBar().show();
	mLayout.setVisibility(View.VISIBLE);

    }

    public void hide() {
	((ActivityDetail) getActivity()).getSupportActionBar().hide();
	mLayout.setVisibility(View.GONE);

    }

    class PicturePagerAdapter extends PagerAdapter {
	@Override
	public int getCount() {
	    return mPictures.size();
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
	    PhotoView photoView = new PhotoView(container.getContext());
	    photoView.setPadding(2, 2, 2, 2);
	    DataPicture picture = mPictures.get(position);
	    Picasso.with(mContext).load(picture.getMiddleUrl()).into(photoView);
	    container.addView(photoView, LayoutParams.MATCH_PARENT,
		    LayoutParams.MATCH_PARENT);

	    photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
		@Override
		public void onPhotoTap(View arg0, float arg1, float arg2) {
		    if (mLayout.getVisibility() != View.GONE) {
			hide();
		    } else {
			show();
		    }
		}
	    });
	    return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	    container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
	    return view == object;
	}

    }

}
