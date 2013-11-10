package com.madevil.gallery;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.madevil.gallery.ActivityMain.Callback;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class FragmentLogin extends Fragment {
    private Context mContext = null;
    private TextView mMsg = null;
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
	mDialog = ProgressDialog.show(act, "正在登陆", "登陆中，请稍后……", true, false);

	RequestParams params = new RequestParams();
	params.put("login_info", share.login_info);

	BasicClientCookie cookie = new BasicClientCookie("user", share.user.id);
	share.http_cookies.addCookie(cookie);
	Http.With(mContext).post(G.Url.doLogin(), params, new JsonHttpResponseHandler() {
	    @Override
	    public void onSuccess(JSONObject json_data) {
		mDialog.dismiss();
		try {
		    share.user.nick = json_data.getString("nick");
		    share.user.avatar = json_data.optString("avatar", "");
		    Log.d("ActivityMain", "login success!");
		    ChangeFragmentUser();
		} catch (Exception e) {
		    share.is_login = false;
		    String msg = "服务器数据错误，请重试登陆";
		    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		    return;
		}
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
		// TODO Auto-generated method stub
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
	mMsg = (TextView) view.findViewById(R.id.login_text_msg);
	mMsg.setText(share.login_info);
	mButtonLoginQQ = (ImageView) view.findViewById(R.id.login_btn_qq);
	mButtonLoginQQ.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		LoginByQQ();
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
