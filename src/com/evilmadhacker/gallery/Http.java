package com.evilmadhacker.gallery;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Http {
    private DataShare share = null;
    private Context mContext = null;
    private static AsyncHttpClient http = new AsyncHttpClient();

    public static Http With(Context c) {
	Http h = new Http();
	h.mContext = c;
	h.share = DataShare.Ins(c);
	return h;
    }

    class RspWrapper extends JsonHttpResponseHandler {
	private String url = "";
	private JsonHttpResponseHandler h = null;

	public RspWrapper(String url, JsonHttpResponseHandler h) {
	    this.url = url;
	    this.h = h;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSuccess(JSONObject root) {
	    Log.i("http.url", url);
	    Log.d("http.response", "" + root);
	    int ecode = M.ecode(root);
	    if (ecode != 0) {
		Toast.makeText(mContext, M.emsg(root), Toast.LENGTH_SHORT)
			.show();
		h.onFailure(new IOException(M.emsg(root)));
		return;
	    }

	    JSONObject data = null;
	    try {
		data = root.getJSONObject("data");
	    } catch (Exception e) {
		/*
		Log.e("http.exception", e.toString());
		String msg = "服务器返回的数据无效";
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		h.onFailure(e, root.toString());
		*/
	    }

	    h.onSuccess(data);
	}

	@Override
	public void onFailure(Throwable e, String response) {
	    Log.i("http.url", url);
	    Log.e("http.exception", e.toString());
	    e.printStackTrace();

	    String msg = "服务器出错";
	    if (e instanceof UnknownHostException) {
		msg = "请连接网络";
	    }
	    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	    h.onFailure(e, response);
	}
    }

    public void get(String url, JsonHttpResponseHandler h) {
	Log.d("http", "GET: "+url);
	http.setCookieStore(share.http_cookies);
	http.get(url, new RspWrapper(url, h));
    }
    
    public void post(String url, RequestParams params, JsonHttpResponseHandler h) {
	Log.d("http", "POST: "+url);
	http.setCookieStore(share.http_cookies);
	http.post(url, params, new RspWrapper(url, h));
    }

}
