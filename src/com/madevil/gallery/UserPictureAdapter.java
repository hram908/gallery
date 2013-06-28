package com.madevil.gallery;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.madevil.gallery.R;
import com.madevil.gallery.model.DataPicture;
import com.madevil.util.ImageFetcher;

public class UserPictureAdapter extends BaseAdapter {
	private LinkedList<DataPicture> mPictures;
	private ImageFetcher mImageFetcher;
	private Context mContext;
	private int mUpload = 0;
	public static final int TYPE_UPLOAD = 0;
	public static final int TYPE_ITEM = 1;
	public static final int TYPE_COUNT = 2;

	public UserPictureAdapter(Context context, ImageFetcher f) {
		mPictures = new LinkedList<DataPicture>();
		mImageFetcher = f;
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
		if (index < 0) { index = 0; }
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
		if ( type == TYPE_UPLOAD ) {
			ViewHolder holder = null;
			if ( view == null ) {
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.component_upload, null);
				view.setTag(holder);
			}
			return view;
		}


		/// normal pictures
		int index = position - mUpload;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.component_picture_fixed, null);
			ViewHolder holder = new ViewHolder();
			holder.index = index;
			holder.imageView = (ImageView) view.findViewById(R.id.component_picture_fixed_image);
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
		mImageFetcher.loadImage(picture.getUrl(), holder.imageView);
		return view;
	}

	public void addItems(List<DataPicture> datas) {
		mPictures.addAll(datas);
	}
}
