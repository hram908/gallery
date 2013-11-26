package com.evilmadhacker.gallery;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.evilmadhacker.gallery.R;
import com.evilmadhacker.gallery.ActivityMain.Callback;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class FragmentLogin extends TrackedFragment {
    private Context mContext = null;
    private ImageView mButtonLoginQQ = null;
    private DataShare share = null;
    private Callback mCallback = null;
    private ProgressDialog mDialog = null;

    class DialogWindows extends AsyncTask<Object, Void, Object> {
	protected void onPreExecute() {
	}

	@Override
	protected Object doInBackground(Object... params) {
	    return null;
	}
    };

    /*
     * 把拿到的用户ID发送给后台进行登陆态校验
     */
    public void LoginUser() {
	Activity act = this.getActivity();
	mDialog = ProgressDialog.show(act, "", "登陆中，请稍后……", true, false);

	RequestParams params = new RequestParams();
	params.put("login_info", share.login_info);

	BasicClientCookie cookie = new BasicClientCookie("user", share.user.id);
	share.http_cookies.addCookie(cookie);
	Http.With(mContext).post(G.Url.doLogin(), params,
		new JsonHttpResponseHandler() {
		    @Override
		    public void onSuccess(JSONObject json_data) {
			mDialog.dismiss();
			try {
			    share.user.nick = json_data.getString("nick");
			} catch (Exception e) {
			    share.is_login = false;
			    String msg = "服务器数据错误，请重试登陆";
			    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
				    .show();
			    return;
			}
			share.user.avatar = "";
			share.dump(getActivity().getSharedPreferences(
				ActivityMain.PREFS_NAME, 0));
			Log.d("ActivityMain", "login success!");
			ChangeFragmentUser();

		    }

		    public void onFailure(Throwable e, String response) {
			mDialog.dismiss();
			share.is_login = false;
		    }
		});
    }

    public void LoginByQQ() {
	Activity act = this.getActivity();
	mContext = act.getApplicationContext();
	share.tencent.login(act, G.TENCENT_APIS, new IUiListener() {
	    @Override
	    public void onCancel() {
		Log.d("Login", "onCancel");
	    }

	    @Override
	    public void onComplete(JSONObject result) {
		Log.d("Login", "onComplete");
		try {
		    share.user.id = result.getString("openid");
		    share.login_info = result.toString();
		    share.is_login = true;
		    Log.d("Data", "tencent.login_info=" + share.login_info);
		} catch (Exception e) {
		    e.printStackTrace();
		    Log.e("http.qq", "qq login fail, result=" + result);
		    return;
		}
		LoginUser();
	    }

	    @Override
	    public void onError(UiError e) {
		Log.d("Login", "onError");
	    }
	});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	Log.d("FragmentLogin", "onActivityResult()");
	super.onActivityResult(requestCode, resultCode, data);
	share.tencent.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle item selection
	switch (item.getItemId()) {
	case R.id.menu_login:
	    LoginByQQ();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	Log.d("FragmentLogin", "onCreateView");
	mContext = this.getActivity().getApplication();
	share = DataShare.Ins(mContext);
	Log.d("DataShare", "login_msg=" + share.login_info);

	View view = inflater.inflate(R.layout.fragment_login, container, false);
	mButtonLoginQQ = (ImageView) view.findViewById(R.id.login_btn_qq);
	mButtonLoginQQ.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		LoginByQQ();
	    }
	});

	ImageView btn = (ImageView) view.findViewById(R.id.login_btn_weibo);
	btn.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		new AlertDialog.Builder(getActivity())
			.setMessage("暂不支持微博登陆，敬请期待")
			.setNegativeButton("好的",
				new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog,
					    int which) {
					dialog.dismiss();
				    }
				}).create().show();
	    }
	});
	return view;
    }

    public FragmentLogin() {
    }

    public static FragmentLogin Instance(Callback c) {
	FragmentLogin f = new FragmentLogin();
	f.mCallback = c;
	return f;
    }

    public void ChangeFragmentUser() {
	Log.d("FragmentLogin", "changin fragment to user");
	mCallback.call();
    }

}
