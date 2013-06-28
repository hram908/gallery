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

public class PictureAdapter extends BaseAdapter {
	private LinkedList<DataPicture> mPictures;
	private ImageFetcher mImageFetcher;
	private Context mContext;

	public PictureAdapter(Context context, ImageFetcher f) {
		mPictures = new LinkedList<DataPicture>();
		mImageFetcher = f;
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
		Log.d("PictureAdapter", "pos=" + index + "view=" + view);
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
		holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(
				width, height) );
		holder.contentView.setText(picture.getTitle());
		mImageFetcher.loadImage(picture.getUrl(), holder.imageView);
		return view;
	}

	public void addItems(List<DataPicture> datas) {
		mPictures.addAll(datas);
	}
}
