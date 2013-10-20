package com.madevil.gallery;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.madevil.gallery.R;
import com.origamilabs.library.views.StaggeredGridView;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class FragmentMyself extends Fragment {
    private Context mContext;
    private TextView mMsg;
    private Button mButtonLoginQQ = null;
    private DataShare share = null;

    /*
     * 把拿到的用户ID发送给后台进行登陆态校验
     */
    public void LoginUser() {
	RequestParams params = new RequestParams();
	params.put("login_msg", share.login_msg);

	BasicClientCookie cookie = new BasicClientCookie("user",
		share.user.id);
	share.http_cookies.addCookie(cookie);
	G.http.setCookieStore(share.http_cookies);

	G.http.post(G.Url.getLogin(), params, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		Log.d("FragmentMyself.http", "json:" + json_root);
		int ecode = M.ecode(json_root);
		if (ecode != 0) {
		    share.isLogin = false;
		    Toast.makeText(getActivity(), M.emsg(json_root),
			    Toast.LENGTH_SHORT).show();
		}
	    }

	    public void onFailure(Throwable e, String response) {
		Log.e("MianActivity.http", "Exception: " + e.toString());
		e.printStackTrace();
		share.isLogin = false;
		String msg = G.EMSG;
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	    }

	});
    }

    public void LoginByQQ() {
	share.tencent.login(this.getActivity(), G.TENCENT_APIS,
		new IUiListener() {
		    @Override
		    public void onCancel() {
			// TODO Auto-generated method stub
			Log.d("Login", "onCancel");
			mMsg.setText("cancel");
		    }

		    @Override
		    public void onComplete(JSONObject result) {
			// TODO Auto-generated method stub
			Log.d("Login", "onComplete");
			mMsg.setText(result.toString());
			try {
			    share.user.id = "qq_" + result.getString("openid");
			    share.login_msg = result.toString();
			    share.isLogin = true;
			    Log.d("Data", "tencent_login_msg="
				    + share.login_msg);
			} catch (Exception e) {
			    e.printStackTrace();
			    Log.e("http.qq", "qq login fail, result=" + result);
			    return;
			}
			LoginUser();
		    }

		    @Override
		    public void onError(UiError e) {
			// TODO Auto-generated method stub
			Log.d("Login", "onError");
			mMsg.setText(e.toString());
		    }

		});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	mContext = this.getActivity().getApplication();
	share = DataShare.Ins(mContext);
	Log.d("DataShare", "login_msg=" + share.login_msg);
	if (!share.isLogin) {
	    View view = inflater.inflate(R.layout.fragment_login, container,
		    false);
	    mMsg = (TextView) view.findViewById(R.id.login_text_msg);
	    mButtonLoginQQ = (Button) view.findViewById(R.id.login_btn_qq);
	    mButtonLoginQQ.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		    LoginByQQ();
		}
	    });
	    return view;
	}
	View view = inflater
		.inflate(R.layout.fragment_myself, container, false);
	TextView nick = (TextView)view.findViewById(R.id.myself_text_nick);
	nick.setText(share.user.id);
	return view;
    }

    private void AddItemToContainer(int pageindex) {
	String url = G.Url.getIndexByNew(pageindex);
	Log.d("MainActivity.http", "current url:" + url);

	// do request
	G.http.get(url, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_root) {
		List<DataPicture> items = new ArrayList<DataPicture>();

		Log.d("MainActivity.http", "json:" + json_root);
		try {
		    JSONObject json_data = json_root.getJSONObject("data");
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
			items.add(picture);
		    }
		} catch (Exception e) {
		    Log.e("MainActivity.http", "exception:" + e.toString());
		    String msg = "服务器返回的数据无效";
		    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
		// mAdapter.addItems(items);
		// mAdapter.notifyDataSetChanged();
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

}
