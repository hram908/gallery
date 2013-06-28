package com.madevil.gallery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.madevil.gallery.R;
import com.madevil.gallery.model.DataPicture;
import com.madevil.gallery.model.DataUser;
import com.madevil.gallery.model.Globals;
import com.madevil.util.Helper;
import com.madevil.util.ImageFetcher;
import com.origamilabs.library.views.StaggeredGridView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity {
	private DataUser mUser = new DataUser();
	private UserPictureAdapter mUserPictureAdapter = null;
    private ImageFetcher mImageFetcher = null;
    private GridView mGridView = null;
    private TextView mTextNick = null;
    private TextView mTextIntro = null;
    private Button mButtonMoney = null;
    private Button mButtonPicture = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		// 读取用户信息
        Intent intent = getIntent();
        mUser.setId(intent.getStringExtra(DataUser.intentTag));
		Log.d("UserActivity", "user: " + mUser.getId());

		// 构造图片加载器
        mImageFetcher = new ImageFetcher(this, 240);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);

        // 构造图片转换器
		mUserPictureAdapter = new UserPictureAdapter(this, mImageFetcher);
		Log.d("UserActivity", "mAdapter=" + mUserPictureAdapter);

		mGridView = (GridView) this.findViewById(R.id.user_view_grid);
        mGridView.setAdapter(mUserPictureAdapter);

		// 添加一个默认的按钮
        mUserPictureAdapter.notifyDataSetChanged();

		mTextNick = (TextView) this.findViewById(R.id.user_text_nick);
		mTextIntro = (TextView) this.findViewById(R.id.user_text_intro);
		mButtonMoney = (Button) this.findViewById(R.id.user_btn_money);
		mButtonPicture = (Button) this.findViewById(R.id.user_btn_picture);


		// 异步发起读取用户信息的操作
		String url = Globals.Url.getUser(mUser.getId(), 0);
		TaskGetUser task = new TaskGetUser();
		task.execute(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_user, menu);
		return true;
	}

	private class TaskGetUser extends AsyncTask<String, Integer, Integer> {
		private int ecode = 0;
		private String msg = "";
		private List<DataPicture> mPictures = null;

		@Override
		protected void onPostExecute(Integer result) {
			if (ecode != 0) {
				Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT)
						.show();
			}
			mUserPictureAdapter.addItems(mPictures);
            mUserPictureAdapter.notifyDataSetChanged();
            mTextNick.setText(mUser.getNick());
            mTextIntro.setText(mUser.getIntro());
            mButtonMoney.setText(""+mUser.getMoneyNumber());
            mButtonPicture.setText(""+mUser.getPictureNumber());
		}

		@Override
		protected Integer doInBackground(String... params) {
            mPictures = new ArrayList<DataPicture>();

			String url = params[0];
			String json = "";
			if (!Helper.checkConnection(getApplication())) {
				ecode = 1;
				msg = "无网络连接";
				return ecode;
			}
			try {
				json = Helper.getStringFromUrl(url);
			} catch (IOException e) {
				Log.e("IOException is : ", e.toString());
				e.printStackTrace();
				ecode = 2;
				msg = "连接图片服务器失败！请休息一下再来～";
				return ecode;
			}
			Log.d("DetailActiivty", "picture info json:" + json);

			if (json == null) {
				ecode = 3;
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
				JSONObject json_data = json_root.getJSONObject("data");
				mUser.setNick(json_data.optString("nick").replace("rexliao", "tyler")
						.replace("rex", "tyler").replace("talebook", "tyler"));
				mUser.setIntro(json_data.optString("intro", ""));
				mUser.setMoneyNumber(json_data.optInt("money", 0));
				mUser.setPictureNumber(json_data.optInt("pic_num", 0));

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
                    picture.setHeight(obj.optInt("height",0));
                    picture.setWidth(obj.optInt("width"));
                    mPictures.add(picture);
                }

			} catch (Exception e) {
				e.printStackTrace();
				ecode = 4;
				msg = "图片服务器返回的数据出错！请休息一下再来～";
				return ecode;
			}
			return 0;
		}

	}

}
