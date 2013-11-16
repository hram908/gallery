package com.madevil.gallery;

import java.io.File;
import java.io.FileNotFoundException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class FragmentUpload extends TrackedFragment {
    public static final int REQCODE_UPLOAD_IMAGE = 2;
    private DataShare share = null;
    private TextView mText = null;
    private Button mButtonSend, mButtonCancel;
    private Context mContext = null;
    private ProgressDialog mDialog = null;
    private ImageView mImage = null;
    private Uri mUri;
    private String mTitle = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater
		.inflate(R.layout.fragment_upload, container, false);
	mContext = view.getContext();
	share = DataShare.Ins(mContext);

	mText = (TextView) view.findViewById(R.id.upload_text);
	mImage = (ImageView) view.findViewById(R.id.upload_image);
	mButtonSend = (Button) view.findViewById(R.id.upload_send);
	mButtonSend.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		doUpload(mUri);
	    }
	});

	mButtonCancel = (Button) view.findViewById(R.id.upload_cancel);
	mButtonCancel.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		getActivity().setResult(Activity.RESULT_CANCELED);
		getActivity().finish();
	    }
	});

	mUri = this.getActivity().getIntent().getData();
	Picasso.with(getActivity()).load(mUri).into(mImage);
	return view;
    }

    public String getGalleryPath(Uri uri) {
	String[] projection = { MediaStore.Images.Media.DATA };
	Cursor cursor = mContext.getContentResolver().query(uri, projection,
		null, null, null);
	if (cursor == null)
	    return null;
	int column_index = cursor
		.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	cursor.moveToFirst();
	return cursor.getString(column_index);
    }

    public void onSuccessUpload(JSONObject json_data) {
	DataPicture picture = new DataPicture();
	try {
	    picture.id = json_data.getString("pid");
	    picture.title = mTitle;
	    picture.setUrl(json_data.getString("url"));
	} catch (Exception e) {
	    Log.e("MainActivity.http", "exception:" + e.toString());
	    String msg = "服务器返回的数据无效";
	    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	Intent intent = new Intent();
	intent.putExtra("picture", picture);
	getActivity().setResult(Activity.RESULT_OK, intent);
	Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
	getActivity().finish();
    }

    public void doSelectImage() {
	Intent intent = new Intent();
	intent.setType("image/*");
	intent.setAction(Intent.ACTION_GET_CONTENT);
	startActivityForResult(Intent.createChooser(intent, "Select Picture"),
		REQCODE_UPLOAD_IMAGE);
    }

    public void doUpload(Uri selectedImageUri) {
	mDialog = ProgressDialog.show(mContext, "", getString(R.string.d_upload_ing), true,
		false);
	String filePath = null;
	// OI FILE Manager
	String filemanagerstring = selectedImageUri.getPath();

	// MEDIA GALLERY
	String gallery_media = getGalleryPath(selectedImageUri);
	if (gallery_media != null) {
	    filePath = gallery_media;
	} else if (filemanagerstring != null) {
	    filePath = filemanagerstring;
	} else {
	    Toast.makeText(mContext, "Unknown path", Toast.LENGTH_LONG).show();
	    Log.e("Bitmap", "Unknown path");
	}
	Log.d("image", "filepath=" + filePath);

	mTitle = mText.getText().toString();
	RequestParams params = new RequestParams();
	params.put("title", mTitle);
	params.put("width", "7000");
	params.put("height", "7000");

	try {
	    params.put("file", new File(filePath));
	} catch (FileNotFoundException e) {
	    Toast.makeText(mContext, "找不到文件", Toast.LENGTH_LONG).show();
	    Log.e(e.getClass().getName(), e.getMessage(), e);
	}

	Http.With(mContext).post(G.Url.doUpload(), params,
		new JsonHttpResponseHandler() {
		    @Override
		    public void onSuccess(JSONObject json_data) {
			mDialog.dismiss();
			onSuccessUpload(json_data);
		    }

		    @Override
		    public void onFailure(Throwable e, String response) {
			mDialog.dismiss();
			new AlertDialog.Builder(mContext).setTitle("很抱歉")
				.setMessage("很抱歉，图片上传失败了～\n请稍后再次尝试").create()
				.show();
		    }

		});
    }

}
