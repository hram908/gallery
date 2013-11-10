package com.madevil.gallery;

import java.util.ArrayList;

import org.json.JSONObject;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class FragmentPicture extends Fragment {
    private DataUser mUser = new DataUser();
    private Boolean mUserLiked = false;
    private Boolean mUserCommented = false;
    private Boolean mUserDownloaded = false;

    private int mIndex = 0;
    private DataPicture mPicture = null;
    private ArrayList<DataPicture> mPictures = null;

    private Button mButtonLike, mButtonComment, mButtonDownload;
    private ImageView mButtonAvatar;
    private TextView mTitle;
    private Context mContext;
    private ViewPager mViewPager;
    private LinearLayout mLayout;
    private PicturePagerAdapter mAdapter;

    public static FragmentPicture Ins(ArrayList<DataPicture> pictures, int index) {
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
	mLayout = (LinearLayout) view.findViewById(R.id.detail_bar_info);
	mTitle = (TextView) view.findViewById(R.id.fragment_picture_title);
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

	ProgressBar p = (ProgressBar) view
		.findViewById(R.id.fragment_picture_loading);
	p.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (mLayout.getVisibility() != View.GONE) {
		    hide();
		} else {
		    show();
		}
	    }
	});

	// bind photo view
	mViewPager = (ViewPager) view.findViewById(R.id.detail_frame);
	mAdapter = new PicturePagerAdapter();
	mViewPager.setAdapter(mAdapter);
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
		updateScreen();
	    }
	});

	mPicture = mPictures.get(mIndex);
	mViewPager.setCurrentItem(mIndex);
	updateScreen();
	hide();
	return view;
    }

    public void updateScreen() {
	int num = mPicture.likeNumber;
	if (num > 0) {
	    mButtonLike.setText(String.format("%s", num));
	} else {
	    mButtonLike.setText(R.string.t_like);
	}

	num = mPicture.commentNumber;
	if (num > 0) {
	    mButtonComment.setText(String.format("%s", num));
	} else {
	    mButtonComment.setText(R.string.t_comment);
	}

	mButtonDownload.setText(R.string.t_download);
	mTitle.setText(mPicture.getTitle());

	loadPictureInfo(G.Url.pictureInfo(mPicture.id));
	String url = G.Url.userAvatar(mPicture.user.id);
	Log.i("picture", "avatar: " + url);
	Picasso.with(getActivity()).load(url).into(mButtonAvatar);
    }

    public void loadPictureInfo(String url) {
	Http.With(mContext).get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject obj) {
		try {
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
	    }
	});
    }

    private boolean lock_btn_like = false;

    public void onClick_detail_btn_like(View v) {
	if (lock_btn_like)
	    return;
	lock_btn_like = true;
	String url = G.Url.doPictureLike(mPicture.id);
	Log.d("http", "like url=" + url);
	// do request
	RequestParams params = new RequestParams();
	if (mUserLiked) {
	    params.put("like", "0");
	} else {
	    params.put("like", "1");
	}
	Http.With(mContext).post(url, params, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject obj) {
		lock_btn_like = false;
		mUserLiked = !mUserLiked;
		mButtonLike.setPressed(mUserLiked);
	    }

	    @Override
	    public void onFailure(Throwable e, String response) {
		lock_btn_like = false;
	    }
	});
	return;
    }

    @SuppressLint("ResourceAsColor")
    public void onClick_detail_btn_download(View v) {
	if (!this.mUserDownloaded) {
	}

	PhotoView photo = (PhotoView) mViewPager
		.findViewById(R.id.fragment_picture);
	photo.setBackgroundColor(R.color.transparent);
	Picasso.with(mContext).load(mPicture.url).into(photo);
    }

    public void onClick_detail_btn_comment(View v) {
	Intent intent = new Intent(mContext, ActivityComment.class);
	intent.putExtra(DataPicture.intent, mPicture);
	this.startActivityForResult(intent, 0);
    }

    public void onClick_detail_btn_avatar(View v) {
	if (mUser.id.length() == 0) {
	    Toast.makeText(mContext, "抱歉，暂时无法查看该用户的写真册", Toast.LENGTH_LONG)
		    .show();
	    return;
	}
	Intent intent = new Intent(mContext, ActivityUser.class);
	intent.putExtra(DataUser.intent, mUser);
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View instantiateItem(ViewGroup container, int position) {
	    PhotoView photoView = new PhotoView(container.getContext());
	    photoView.setPadding(2, 2, 2, 2);
	    photoView.setBackgroundColor(R.color.transparent);
	    photoView.setId(R.id.fragment_picture);

	    DataPicture picture = mPictures.get(position);
	    Picasso.with(mContext).load(picture.url_m).into(photoView);
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
