package com.madevil.gallery;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
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
	    // Log.i("adapter.user", "build view index=" + index);
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
	Picasso.with(parent.getContext()).load(picture.url_s).into(image);
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

public class FragmentUser extends TrackedFragment {
    public static final int REQCODE_SELECT_IMAGE = 2;
    public static final int REQCODE_UPLOAD_IMAGE = 3;
    private DataShare share = null;
    private DataUser mUser = new DataUser();
    private UserPictureAdapter mUserPictureAdapter = null;
    private StickyGridHeadersGridView mGridView = null;
    private TextView mTextNick = null;
    private TextView mTextIntro = null;
    private Button mButtonMoney = null;
    private Button mButtonPicture = null;
    private Context mContext = null;
    private ImageView mUserAvatar = null;
    private View mHeaderView = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch (requestCode) {
	case REQCODE_SELECT_IMAGE:
	    if (resultCode == Activity.RESULT_OK) {
		Intent intent = new Intent(data);
		intent.setClass(getActivity(), ActivityUpload.class);
		startActivityForResult(intent, REQCODE_UPLOAD_IMAGE);
	    }
	    break;
	case REQCODE_UPLOAD_IMAGE:
	    if (resultCode == Activity.RESULT_OK) {
		DataPicture picture = data.getParcelableExtra("picture");
		picture.user = mUser;
		mUserPictureAdapter.addFirst(picture);
		mUserPictureAdapter.notifyDataSetChanged();
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
	h.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		startActivity(new Intent(Intent.ACTION_VIEW, G.Url.helpMoney()));
	    }
	});

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

	mButtonMoney.setClickable(true);
	mButtonMoney.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		Log.e("widget", "click");
		startActivity(new Intent(Intent.ACTION_VIEW, G.Url.helpMoney()));
	    }
	});

	TextView help = (TextView) h.findViewById(R.id.user_help);
	help.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	help.setClickable(true);
	help.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		Log.e("widget", "click");
		startActivity(new Intent(Intent.ACTION_VIEW, G.Url.helpMoney()));
	    }
	});

	// 异步发起读取用户信息的操作
	updateScreenInfo();
	getUserInfo();
	return view;
    }

    private int mPage = 0;

    public void getUserInfo() {
	// do request
	String url = G.Url.user(mUser.id, mPage);
	Http.With(mContext).get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject obj) {
		if (onSuccessGetUser(obj) == 0) {
		    mPage += 1;
		    getUserInfo();
		}
	    }
	});
    }

    /* 返回值表示是否加载完最后一页 */
    public int onSuccessGetUser(JSONObject json_data) {
	List<DataPicture> mPictures = new ArrayList<DataPicture>();

	try {
	    mUser.nick = json_data.optString("nick");
	    mUser.intro = json_data.optString("intro", "");
	    mUser.moneyNumber = json_data.optInt("money", 0);

	    JSONArray json_pics = json_data.getJSONArray("pics");
	    for (int i = 0; i < json_pics.length(); i++) {
		JSONObject obj = json_pics.getJSONObject(i);
		DataPicture picture = new DataPicture();
		picture.id = obj.optString("pid", "");
		picture.title = obj.optString("title", "");
		picture.content = obj.optString("content", "");
		picture.commentNumber = obj.optInt("comment_num", 0);
		picture.likeNumber = obj.optInt("like", 0);
		picture.downloadNumber = obj.optInt("download", 0);
		picture.height = obj.optInt("height", 0);
		picture.width = obj.optInt("width");
		picture.user = mUser;
		picture.setUrl(obj.optString("url", ""));
		mPictures.add(picture);
	    }
	} catch (Exception e) {
	    Log.e("MainActivity.http", "exception:" + e.toString());
	    String msg = "服务器返回的数据无效";
	    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	// Toast.makeText(mContext, "loaded:" + mPictures.size(),
	// Toast.LENGTH_SHORT).show();
	mUser.pictureNumber += mPictures.size();
	if (mUser.id == share.user.id) {
	    share.user = mUser;
	}

	updateScreenInfo();
	mUserPictureAdapter.addItems(mPictures);
	mUserPictureAdapter.notifyDataSetChanged();

	return json_data.optInt("last_page", 0);
    }

    public void updateScreenInfo() {
	mTextNick.setText(mUser.nick);
	mTextIntro.setText(mUser.intro);
	mButtonMoney.setText("" + mUser.moneyNumber + "秀点");
	mButtonPicture.setText("" + mUser.pictureNumber + "图片");
    }

    public void doSelectImage() {
	Intent intent = new Intent();
	intent.setType("image/*");
	intent.setAction(Intent.ACTION_GET_CONTENT);
	startActivityForResult(Intent.createChooser(intent, "Select Picture"),
		REQCODE_SELECT_IMAGE);

    }

}
