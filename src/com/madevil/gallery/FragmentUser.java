package com.madevil.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapterWrapper;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

class UserPictureAdapter implements StickyGridHeadersSimpleAdapter {
    public static final int TYPE_UPLOAD = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_COUNT = 2;

    private ArrayList<DataPicture> mPictures;
    private Context mContext;
    private int mUpload = 0;
    private OnClickListener mClick = null;
    private View mHeadView = null;
    private DataSetObserver ob = null;

    public UserPictureAdapter(Context context, View h) {
	mPictures = new ArrayList<DataPicture>();
	mContext = context;
	mHeadView = h;
    }

    public UserPictureAdapter(Context context, View h, int upload,
	    OnClickListener l) {
	mPictures = new ArrayList<DataPicture>();
	mContext = context;
	mHeadView = h;
	mUpload = upload;
	mClick = l;
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
	ImageView image;
	int index;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
	int type = getItemViewType(position);
	if (type == TYPE_UPLOAD) {
	    if (view == null) {
		LayoutInflater inflater = LayoutInflater.from(parent
			.getContext());
		view = inflater.inflate(R.layout.component_upload, null);
		view.setTag((Integer) position);
		ImageButton btn = (ImageButton) view
			.findViewById(R.id.user_btn_upload);
		btn.setOnClickListener(mClick);
		Log.d("UserPictureAdapter", "binding button");
	    }
	    return view;
	}

	// / normal pictures
	int index = position - mUpload;
	if (view == null) {
	    //Log.i("adapter.user", "build view index=" + index);
	    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	    view = inflater.inflate(R.layout.component_picture_fixed, null);
	    view.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
		    ImageView image = (ImageView) v
			    .findViewById(R.id.component_picture_fixed_image);
		    int index = (Integer) image.getTag();
		    Intent intent = new Intent(mContext, ActivityDetail.class);
		    intent.putExtra(DataPicture.intentPictures, mPictures);
		    intent.putExtra(DataPicture.intentIndex, index);
		    mContext.startActivity(intent);
		}
	    });
	}

	DataPicture picture = mPictures.get(index);
	ImageView image = (ImageView) view
		.findViewById(R.id.component_picture_fixed_image);
	image.setTag((Integer) index);
	Picasso.with(mContext).load(picture.getSmallUrl()).into(image);
	return view;
    }

    public void addItems(List<DataPicture> datas) {
	Log.i("data.pictures", "addItems(), n=" + datas.size());
	for (int i = 0; i < datas.size(); ++i) {
	    mPictures.add(datas.get(i));
	}
    }

    public void addFirst(DataPicture picture) {
	mPictures.add(0, picture);
    }

    public void addLast(DataPicture picture) {
	mPictures.add(picture);
    }

    @Override
    public boolean areAllItemsEnabled() {
	return true;
    }

    @Override
    public boolean isEnabled(int position) {
	return true;
    }

    @Override
    public boolean hasStableIds() {
	return false;
    }

    @Override
    public boolean isEmpty() {
	return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
	ob = observer;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
	if (ob == observer)
	    ob = null;
    }

    @Override
    public long getHeaderId(int position) {
	return 0;
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup parent) {
	return mHeadView;
    }

    public void notifyDataSetChanged() {
	ob.onChanged();
    }

}

public class FragmentUser extends Fragment {
    public static final int REQCODE_UPLOAD_IMAGE = 2;
    private DataShare share = null;
    private DataUser mUser = new DataUser();
    private UserPictureAdapter mUserPictureAdapter = null;
    private StickyGridHeadersGridView mGridView = null;
    private TextView mTextNick = null;
    private TextView mTextIntro = null;
    private Button mButtonMoney = null;
    private Button mButtonPicture = null;
    private Context mContext = null;
    private ProgressDialog mDialog = null;
    private ImageView mUserAvatar = null;
    private View mHeaderView = null;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch (requestCode) {
	case REQCODE_UPLOAD_IMAGE:
	    if (resultCode == Activity.RESULT_OK) {
		Uri uri = data.getData();
		doUpload(uri);
	    }
	    break;
	default:
	}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle item selection
	switch (item.getItemId()) {
	case R.id.menu_upload:
	    doSelectImage();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    public static FragmentUser Ins(DataUser user) {
	FragmentUser f = new FragmentUser();
	f.mUser = user;
	return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	Log.d("FragmentUser", "onCreateView");

	View view = inflater.inflate(R.layout.fragment_user, container, false);
	mContext = view.getContext();
	share = DataShare.Ins(mContext);

	mHeaderView = inflater.inflate(R.layout.component_user, container,
		false);
	View h = mHeaderView;

	// 读取用户信息
	Log.i("UserActivity", "user_id=" + mUser.id + ", nick=" + mUser.nick);
	if (mUser.id == share.user.id) {
	    // 构造图片转换器
	    mUserPictureAdapter = new UserPictureAdapter(mContext, h, 1,
		    new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    Log.e("upload", "start to upload image");
			    doSelectImage();
			}
		    });
	} else {
	    mUserPictureAdapter = new UserPictureAdapter(mContext, h);
	}
	Log.d("UserActivity", "mAdapter=" + mUserPictureAdapter);

	mGridView = (StickyGridHeadersGridView) view
		.findViewById(R.id.user_view_grid);
	mGridView.setAdapter(mUserPictureAdapter);
	mGridView.setAreHeadersSticky(false);
	mUserPictureAdapter.notifyDataSetChanged();

	mTextNick = (TextView) h.findViewById(R.id.user_text_nick);
	mTextIntro = (TextView) h.findViewById(R.id.user_text_intro);
	mButtonMoney = (Button) h.findViewById(R.id.user_btn_money);
	mButtonPicture = (Button) h.findViewById(R.id.user_btn_picture);
	mUserAvatar = (ImageView) h.findViewById(R.id.user_image_avatar);
	Picasso.with(mContext).load(G.Url.userAvatar(mUser.id))
		.into(mUserAvatar);

	// 异步发起读取用户信息的操作
	updateScreenInfo();
	getUserInfo();
	return view;
    }

    private int mPage = 0;

    public void getUserInfo() {
	// do request
	String url = G.Url.user(mUser.id, mPage);
	Log.d("UserActivity.http", "url=" + url);
	G.http.setCookieStore(share.http_cookies);
	G.http.get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		if (onSuccessGetUser(json_root) > 0) {
		    mPage += 1;
		    getUserInfo();
		}
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

    public int onSuccessGetUser(JSONObject json_root) {
	List<DataPicture> mPictures = new ArrayList<DataPicture>();

	Log.d("http.user", "json:" + json_root);
	try {
	    int ecode = M.ecode(json_root);
	    if (ecode != 0) {
		Toast.makeText(mContext, M.emsg(json_root), Toast.LENGTH_SHORT)
			.show();
		return -1;
	    }
	    JSONObject json_data = json_root.getJSONObject("data");
	    mUser.nick = json_data.optString("nick");
	    mUser.intro = json_data.optString("intro", "");
	    mUser.moneyNumber = json_data.optInt("money", 0);
	    mUser.pictureNumber = json_data.optInt("pic_num", 0);

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
	    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	Toast.makeText(mContext, "loaded:" + mPictures.size(),
		Toast.LENGTH_SHORT).show();
	updateScreenInfo();
	mUserPictureAdapter.addItems(mPictures);
	mUserPictureAdapter.notifyDataSetChanged();
	return mPictures.size();
    }

    public void updateScreenInfo() {
	mTextNick.setText(mUser.nick);
	mTextIntro.setText(mUser.intro);
	mButtonMoney.setText("" + mUser.moneyNumber);
	mButtonPicture.setText("" + mUser.pictureNumber);
    }

    public void onSuccessUpload(JSONObject json_root) {
	Log.d("http.upload", "json:" + json_root);
	DataPicture picture = new DataPicture();
	try {
	    int ecode = M.ecode(json_root);
	    if (ecode != 0) {
		Toast.makeText(mContext, M.emsg(json_root), Toast.LENGTH_SHORT)
			.show();
		return;
	    }
	    JSONObject json_data = json_root.getJSONObject("data");
	    picture.setId(json_data.getString("pid"));
	    picture.setUrl(json_data.getString("url"));
	} catch (Exception e) {
	    Log.e("MainActivity.http", "exception:" + e.toString());
	    String msg = "服务器返回的数据无效";
	    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	mUserPictureAdapter.addFirst(picture);
	mUserPictureAdapter.notifyDataSetChanged();
	Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
    }

    public void doSelectImage() {
	Intent intent = new Intent();
	intent.setType("image/*");
	intent.setAction(Intent.ACTION_GET_CONTENT);
	startActivityForResult(Intent.createChooser(intent, "Select Picture"),
		REQCODE_UPLOAD_IMAGE);
    }

    public void doUpload(Uri selectedImageUri) {
	mDialog = ProgressDialog.show(mContext, "正在上传", "图片正在上传中……", true,
		false);
	String filePath = null;
	try {
	    // OI FILE Manager
	    String filemanagerstring = selectedImageUri.getPath();

	    // MEDIA GALLERY
	    String gallery_media = getGalleryPath(selectedImageUri);
	    if (gallery_media != null) {
		filePath = gallery_media;
	    } else if (filemanagerstring != null) {
		filePath = filemanagerstring;
	    } else {
		Toast.makeText(mContext, "Unknown path", Toast.LENGTH_LONG)
			.show();
		Log.e("Bitmap", "Unknown path");
	    }
	    Log.d("image", "filepath=" + filePath);

	    RequestParams params = new RequestParams();
	    params.put("title", "no title");
	    params.put("width", "7000");
	    params.put("height", "7000");
	    params.put("file", new File(filePath));
	    String url = G.Url.doUpload();
	    G.http.setCookieStore(share.http_cookies);
	    G.http.post(url, params, new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(JSONObject json_root) {
		    mDialog.dismiss();
		    onSuccessUpload(json_root);
		}

		@Override
		public void onFailure(Throwable e, String response) {
		    mDialog.dismiss();
		    Log.e("MianActivity.http", "Exception: " + e.toString());
		    e.printStackTrace();
		    new AlertDialog.Builder(mContext).setTitle("很抱歉")
			    .setMessage("很抱歉，图片上传失败了～\n请稍后再次尝试").create()
			    .show();
		}

	    });
	} catch (Exception e) {
	    Toast.makeText(mContext, "Internal error", Toast.LENGTH_LONG)
		    .show();
	    Log.e(e.getClass().getName(), e.getMessage(), e);
	}

    }

}
